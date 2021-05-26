package io.xserverless.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.commands.MethodCommand;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;

public class XFunction {
    public void analysis(InputStream jarInputStream, Function<XObject, OutputStream> outputStreamProvider, FunctionFilter functionFilter) throws IOException {
        XGroup group = new FunctionConverter().readJar(jarInputStream);

        analysis(outputStreamProvider, functionFilter, group);
    }

    public void analysis(Function<XObject, OutputStream> outputStreamProvider, FunctionFilter functionFilter, XGroup group) {
        List<CommandGroup<MethodCommand>> commandGroups = group.stream()
                .filter(XObject::isFunction)
                .flatMap(xObject -> {
                    CommandGroup<MethodCommand> commandGroup = group.getMethodCommandGroup(xObject.getOwner(), xObject.getName(), xObject.getDescriptor());
                    List<CommandGroup<MethodCommand>> entries = new ArrayList<>();
                    if (commandGroup != null) {
                        for (MethodCommand command : commandGroup.getCommands()) {
                            if (command instanceof MethodCommand.Annotation && functionFilter.allowedByAnnotationType(((MethodCommand.Annotation) command).getDescriptor())) {
                                entries.add(commandGroup);
                                break;
                            }
                        }
                    }
                    return entries.stream();
                })
                .collect(Collectors.toList());
        for (CommandGroup<MethodCommand> commandGroup : commandGroups) {
            XObject main = group.createOrGetFunction(commandGroup.getOwner(), commandGroup.getName(), commandGroup.getDescriptor());
            CommandFilter commandFilter = CommandFilter.createFilter(group, main);

            System.out.println("filters:\n" + commandFilter);
            System.out.println();

            OutputStream outputStream = outputStreamProvider.apply(main);
            if (outputStream != null) {
                try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream)) {
                    for (CommandGroup<ClassCommand> classCommandCommandGroup : group.getClassMap().values()) {
                        byte[] bytes = new ClassCommandWriter().write(classCommandCommandGroup, commandFilter);
                        if (bytes.length > 0) {
                            System.out.println(classCommandCommandGroup.getName());
                            ZipEntry zipEntry = new ZipEntry(classCommandCommandGroup.getName() + ".class");
                            jarOutputStream.putNextEntry(zipEntry);
                            jarOutputStream.write(bytes);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
