package io.xserverless.function.command.writer;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.commands.FieldCommand;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import org.objectweb.asm.Type;

public class CommandFilter {
    private final Set<String> allowTypeSet = new HashSet<>();
    private final Set<String> allowFunctionSet = new HashSet<>();
    private final Set<String> allowStateSet = new HashSet<>();

    public static final CommandFilter ALL = new CommandFilter() {
        public boolean type(String name) {
            return true;
        }

        public boolean function(String owner, String name, String descriptor) {
            return true;
        }

        @Override
        public boolean state(String owner, String name, String descriptor) {
            return true;
        }
    };

    private CommandFilter() {
    }

    public void allowType(XObject type) {
        Type t = Type.getType(type.getDescriptor());
        allowTypeSet.add(t.getClassName().replace('.', '/'));
    }

    public void allowFunction(XObject function) {
        String string = new StringJoiner(":", "[", "]").add(function.getOwner()).add(function.getName()).add(function.getDescriptor()).toString();
        allowFunctionSet.add(string);
//        allowTypeSet.add(function.getOwner());
    }

    public void allowState(XObject state) {
        String string = new StringJoiner(":", "[", "]").add(state.getOwner()).add(state.getName()).add(state.getDescriptor()).toString();
        allowStateSet.add(string);
//        allowTypeSet.add(state.getOwner());
    }

    public boolean type(String name) {
        return allowTypeSet.contains(name);
    }

    public boolean function(String owner, String name, String descriptor) {
        String string = new StringJoiner(":", "[", "]").add(owner).add(name).add(descriptor).toString();
        return allowFunctionSet.contains(string);
    }

    public boolean state(String owner, String name, String descriptor) {
        String string = new StringJoiner(":", "[", "]").add(owner).add(name).add(descriptor).toString();
        return allowStateSet.contains(string);
    }

    public static CommandFilter createFilter(XGroup group, XObject entry) {
        Set<XObject> related = new HashSet<>();

        LinkedList<XObject> stack = new LinkedList<>();

        // entry
        stack.add(entry);
        // entry owner constructor
        group.stream()
                .filter(XObject::isFunction)
                .filter(obj -> Objects.equals(obj.getOwner(), entry.getOwner()) &&
                        Objects.equals(obj.getName(), "<init>") &&
                        Objects.equals(obj.getDescriptor(), "()V"))
                .forEach(stack::add);

        // all related
        while (!stack.isEmpty()) {
            XObject object = stack.pop();

            // all related
            Set<XObject> objects = related(group, object, related);
            for (XObject obj : objects) {
                if (obj.isFunction()) {
                    XObject type = group.createTypeByName(obj.getOwner());
                    stack.add(type);
                }
                stack.add(obj);
            }
            // whole type
            if (object.isType()) {
                // annotation
                String ownerName = Type.getType(object.getDescriptor()).getInternalName();
                if (group.isAnnotation(object)) {
                    group.stream().filter(obj -> !obj.isType())
                            .filter(obj -> !related.contains(obj))
                            .filter(obj -> Objects.equals(ownerName, obj.getOwner()))
                            .forEach(stack::add);
                }
                // entity
                if (group.isEntity(object)) {
                    group.stream().filter(obj -> !obj.isType())
                            .filter(obj -> !related.contains(obj))
                            .filter(obj -> Objects.equals(ownerName, obj.getOwner()))
                            .forEach(stack::add);
                }
                // constructors
                boolean constructorExists = false;
                for (XObject obj : related) {
                    if (obj.isFunction()
                            && Objects.equals(obj.getOwner(), ownerName) &&
                            Objects.equals(obj.getName(), "<init>") &&
                            Objects.equals(obj.getDescriptor(), "()V")) {
                        // exists
                        constructorExists = true;
                        break;
                    }
                }
                if (!constructorExists) {
                    stack.add(group.createFunction(ownerName, "<init>", "()V"));
                }
            }


            if (stack.isEmpty()) {
                // implemented methods
                related.stream()
                        .filter(XObject::isFunction)
                        .flatMap(func -> group.findImplementFunctions(func).stream())
                        .filter(func -> related.contains(group.createTypeByName(func.getOwner())))
                        .filter(func -> !related.contains(func))
                        .forEach(stack::add);
            }
        }

        CommandFilter commandFilter = new CommandFilter();

        for (XObject obj : related) {
            if (obj.isFunction()) {
                commandFilter.allowFunction(obj);
            } else if (obj.isType()) {
                commandFilter.allowType(obj);
            } else if (obj.isState()) {
                commandFilter.allowState(obj);
            }
        }
        return commandFilter;
    }

    private static final String[] IGNORED_PREFIXES = new String[]{
            "org/springframework",
            "java/",
            "javax/"
    };

    private static Set<XObject> related(XGroup group, XObject object, Set<XObject> related) {
        if (related.contains(object)) {
            return Collections.emptySet();
        }

        // exclude ignored type prefixes
        if (!object.isType() && object.getOwner() != null) {
            for (String prefix : IGNORED_PREFIXES) {
                if (object.getOwner().startsWith(prefix)) {
                    return Collections.emptySet();
                }
            }
        }
        if (object.isType()) {
            for (String prefix : IGNORED_PREFIXES) {
                if (object.getDescriptor().startsWith("L" + prefix)) {
                    return Collections.emptySet();
                }
            }
        }

        related.add(object);

        // relates
        Set<XObject> set = new HashSet<>(group.relatedReadOnly(object));

        // implements autowired
        if (object.isState()) {
            CommandGroup.ClassCommandGroup commandGroup = group.getClassMap().get(object.getOwner());

            if (commandGroup.getCommands().stream()
                    .filter(c -> c instanceof ClassCommand.Field)
                    .map(classCommand -> ((ClassCommand.Field) classCommand))
                    .filter(field -> Objects.equals(object.getName(), field.getName()))
                    .flatMap(field -> field.getField().getCommands().stream())
                    .filter(fieldCommand -> fieldCommand instanceof FieldCommand.Annotation)
                    .map(fieldCommand -> ((FieldCommand.Annotation) fieldCommand))
                    .anyMatch(annotation -> Objects.equals("Lorg/springframework/beans/factory/annotation/Autowired;", annotation.getDescriptor()))) {
                XObject type = group.createType(object.getDescriptor());
                set.addAll(group.findImplementType(type));
            }
        }

        return set;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CommandFilter:\n");
        stringBuilder.append("allow type:\n");
        for (String s : allowTypeSet) {
            stringBuilder.append("\t").append(s).append("\n");
        }
        stringBuilder.append("allow function:\n");
        for (String s : allowFunctionSet) {
            stringBuilder.append("\t").append(s).append("\n");
        }
        stringBuilder.append("allow state:\n");
        for (String s : allowStateSet) {
            stringBuilder.append("\t").append(s).append("\n");
        }
        return stringBuilder.toString();
    }
}
