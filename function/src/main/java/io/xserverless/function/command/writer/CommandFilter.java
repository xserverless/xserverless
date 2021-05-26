package io.xserverless.function.command.writer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

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
        allowTypeSet.add(function.getOwner());
    }

    public void allowState(XObject state) {
        String string = new StringJoiner(":", "[", "]").add(state.getOwner()).add(state.getName()).add(state.getDescriptor()).toString();
        allowStateSet.add(string);
        allowTypeSet.add(state.getOwner());
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
        entryConstructor(group, entry, related);
        relatedFunctions(group, entry, related);
        relatedTypes(group, group.createTypeByName(entry.getOwner()), related);

        CommandFilter commandFilter = new CommandFilter();

        List<XObject> types = new ArrayList<>();

        for (XObject obj : related) {
            if (obj.isFunction()) {
                types.add(group.createTypeByName(obj.getOwner()));
                for (XObject object : group.relatedReadOnly(obj)) {
                    if (object.isType()) {
                        types.add(object);
                    }
                }
            } else if (obj.isType()) {
                types.add(obj);
            }
        }

        for (XObject type : types) {
            relatedTypes(group, type, related);
        }

        for (XObject obj : related) {
            if (obj.isFunction()) {
                commandFilter.allowFunction(obj);
                for (XObject object : group.relatedReadOnly(obj)) {
                    if (object.isType()) {
                        commandFilter.allowType(object);
                    } else if (object.isState()) {
                        commandFilter.allowState(object);
                    }
                }
            } else if (obj.isType()) {
                commandFilter.allowType(obj);
            }
        }
        return commandFilter;
    }

    private static void entryConstructor(XGroup group, XObject entry, Set<XObject> related) {
        group.iterator(obj -> {
            if (obj.isFunction()) {
                if (Objects.equals(obj.getOwner(), entry.getOwner()) &&
                        Objects.equals(obj.getName(), "<init>") &&
                        Objects.equals(obj.getDescriptor(), "()V")) {
                    relatedFunctions(group, obj, related);
                }
            }
        });
    }

    private static void relatedTypes(XGroup group, XObject entry, Set<XObject> related) {
        if (related.contains(entry)) {
            return;
        }

        related.add(entry);

        Set<XObject> relatedSet = group.relatedReadOnly(entry);
        if (relatedSet != null) {
            for (XObject object : relatedSet) {
                if (object.isType()) {
                    relatedTypes(group, object, related);
                }
            }
        }
    }

    private static void relatedFunctions(XGroup group, XObject entry, Set<XObject> related) {
        if (related.contains(entry)) {
            return;
        }

        related.add(entry);

        Set<XObject> relatedSet = group.relatedReadOnly(entry);
        if (relatedSet != null) {
            for (XObject object : relatedSet) {
                if (object.isFunction()) {
                    relatedFunctions(group, object, related);
                }
            }
        }
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
