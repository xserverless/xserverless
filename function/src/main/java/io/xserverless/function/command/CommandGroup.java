package io.xserverless.function.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.xserverless.function.command.commands.ClassCommand;
import lombok.Data;

@Data
public class CommandGroup<C extends Command> {
    private String owner;
    private String name;
    private String descriptor;
    private List<C> commands = new ArrayList<>();

    public void add(C c) {
        commands.add(c);
    }

    @Data
    public static class ClassCommandGroup extends CommandGroup<ClassCommand> {
        private ClassCommand.Default defaultCommand;

        private Map<String, ClassCommand.Method> methodMap = new HashMap<>();

        private boolean isAnnotation;

        private Set<String> annotations = new HashSet<>();

        @Override
        public void add(ClassCommand classCommand) {
            super.add(classCommand);

            if (classCommand instanceof ClassCommand.Default) {
                defaultCommand = ((ClassCommand.Default) classCommand);

                isAnnotation = false;

                String[] interfaces = defaultCommand.getInterfaces();
                if (interfaces != null) {
                    for (String s : interfaces) {
                        if (Objects.equals("java/lang/annotation/Annotation", s)) {
                            isAnnotation = true;
                            break;
                        }
                    }
                }
            }

            if (classCommand instanceof ClassCommand.Method) {
                ClassCommand.Method method = (ClassCommand.Method) classCommand;
                methodMap.put(method.getName() + method.getDescriptor(), method);
            }

            if (classCommand instanceof ClassCommand.Annotation) {
                annotations.add(((ClassCommand.Annotation) classCommand).getDescriptor());
            }
        }
    }
}
