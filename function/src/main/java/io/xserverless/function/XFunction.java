package io.xserverless.function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.AnnotationCommand;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.commands.MethodCommand;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import org.slf4j.LoggerFactory;

public class XFunction {
    public void analysis(InputStream jarInputStream, Consumer<XEntry> consumer, FunctionFilter functionFilter) throws IOException {
        XGroup group = new FunctionConverter().readJar(jarInputStream);

        analysis(consumer, functionFilter, group);
    }

    void analysis(Consumer<XEntry> consumer, FunctionFilter functionFilter, XGroup group) {
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

            LoggerFactory.getLogger(getClass()).info("analysis entry: {}", main.getName());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (JarOutputStream jarOutputStream = new JarOutputStream(stream)) {
                for (CommandGroup.ClassCommandGroup classCommandCommandGroup : group.getClassMap().values()) {
                    byte[] bytes = new ClassCommandWriter().write(classCommandCommandGroup, commandFilter);
                    if (bytes.length > 0) {
                        ZipEntry zipEntry = new ZipEntry(classCommandCommandGroup.getName() + ".class");
                        jarOutputStream.putNextEntry(zipEntry);
                        jarOutputStream.write(bytes);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            consumer.accept(new XEntry(main, stream.toByteArray(), getUrlPattern(main, group)));

            LoggerFactory.getLogger(getClass()).info("analysis entry ended: {}", main.getName());
        }
    }

    private String getUrlPattern(XObject method, XGroup group) {
        final CommandGroup.ClassCommandGroup classCommandGroup = group.getClassMap().get(method.getOwner());

        List<String> pattern = new ArrayList<>();

        for (ClassCommand command : classCommandGroup.getCommands()) {
            if (command instanceof ClassCommand.Annotation) {
                final ClassCommand.Annotation annotation = (ClassCommand.Annotation) command;
                if (annotation.getDescriptor().equals("Lorg/springframework/web/bind/annotation/RequestMapping;")) {
                    final String path = path(annotation.getAnnotation().getCommands());
                    if (path != null) {
                        pattern.addAll(Arrays.asList(path.split("/")));
                        break;
                    }
                }
            }
        }

        final CommandGroup<MethodCommand> methodCommandGroup = group.getMethodCommandGroup(method.getOwner(), method.getName(), method.getDescriptor());
        for (MethodCommand command : methodCommandGroup.getCommands()) {
            if (command instanceof MethodCommand.Annotation) {
                MethodCommand.Annotation annotation = (MethodCommand.Annotation) command;
                if (annotation.getDescriptor().equals("Lorg/springframework/web/bind/annotation/RequestMapping;")
                        || annotation.getDescriptor().equals("Lorg/springframework/web/bind/annotation/GetMapping;")
                        || annotation.getDescriptor().equals("Lorg/springframework/web/bind/annotation/PostMapping;")
                        || annotation.getDescriptor().equals("Lorg/springframework/web/bind/annotation/DeleteMapping;")
                        || annotation.getDescriptor().equals("Lorg/springframework/web/bind/annotation/PutMapping;")
                ) {
                    final String path = path(annotation.getAnnotation().getCommands());
                    if (path != null) {
                        pattern.addAll(Arrays.asList(path.split("/")));
                        break;
                    }
                }
            }
        }

        final StringJoiner stringJoiner = new StringJoiner("/");
        for (String s : pattern) {
            if (!s.isEmpty()) {
                stringJoiner.add(s);
            }
        }

        return "/" + stringJoiner;
    }

    private String path(List<AnnotationCommand> annotationCommands) {
        for (AnnotationCommand command : annotationCommands) {
            if (command instanceof AnnotationCommand.Array) {
                AnnotationCommand.Array array = (AnnotationCommand.Array) command;
                if (array.getName().equals("value") || array.getName().equals("path")) {
                    for (AnnotationCommand annotationCommand : array.getAnnotation().getCommands()) {
                        if (annotationCommand instanceof AnnotationCommand.Default) {
                            final AnnotationCommand.Default c = (AnnotationCommand.Default) annotationCommand;
                            if (c.getValue() != null) {
                                return c.getValue().toString();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
