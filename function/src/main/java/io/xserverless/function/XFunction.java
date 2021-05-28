package io.xserverless.function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.MethodCommand;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;

public class XFunction {
    public void analysis(InputStream jarInputStream, Consumer<Map.Entry<XObject, byte[]>> consumer, FunctionFilter functionFilter) throws IOException {
        XGroup group = new FunctionConverter().readJar(jarInputStream);

        analysis(consumer, functionFilter, group);
    }

    void analysis(Consumer<Map.Entry<XObject, byte[]>> consumer, FunctionFilter functionFilter, XGroup group) {
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
            XObject main = group.createFunction(commandGroup.getOwner(), commandGroup.getName(), commandGroup.getDescriptor());
            CommandFilter commandFilter = CommandFilter.createFilter(group, main);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (JarOutputStream jarOutputStream = new JarOutputStream(stream)) {
                for (CommandGroup.ClassCommandGroup classCommandCommandGroup : group.getClassMap().values()) {
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

            consumer.accept(new AbstractMap.SimpleEntry<>(main, stream.toByteArray()));
        }
    }
}
