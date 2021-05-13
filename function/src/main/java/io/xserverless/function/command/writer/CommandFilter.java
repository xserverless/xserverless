package io.xserverless.function.command.writer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import io.xserverless.function.dto.XFunction;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import io.xserverless.function.dto.XState;
import io.xserverless.function.dto.XType;
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

    public void allowType(XType type) {
        Type t = Type.getType(type.getDescriptor());
        allowTypeSet.add(t.toString());
    }

    public void allowFunction(XFunction function) {
        String string = new StringJoiner(":", "[", "]").add(function.getOwner()).add(function.getName()).add(function.getDescriptor()).toString();
        allowFunctionSet.add(string);
        allowTypeSet.add(function.getOwner().replace('/', '.'));
    }

    public void allowState(XState state) {
        String string = new StringJoiner(":", "[", "]").add(state.getOwner()).add(state.getName()).add(state.getDescriptor()).toString();
        allowStateSet.add(string);
        allowTypeSet.add(state.getOwner().replace('/', '.'));
    }

    public boolean type(String name) {
        return allowTypeSet.contains(name.replace('/', '.'));
    }

    public boolean function(String owner, String name, String descriptor) {
        String string = new StringJoiner(":", "[", "]").add(owner).add(name).add(descriptor).toString();
        return allowFunctionSet.contains(string);
    }

    public boolean state(String owner, String name, String descriptor) {
        String string = new StringJoiner(":", "[", "]").add(owner).add(name).add(descriptor).toString();
        return allowStateSet.contains(string);
    }

    public static CommandFilter createFilter(XGroup group, XFunction entry) {
        Set<XFunction> related = new HashSet<>();
        entryConstructor(group, entry, related);
        relatedFunctions(group, entry, related);

        CommandFilter commandFilter = new CommandFilter();

        for (XFunction function : related) {
            commandFilter.allowFunction(function);
            for (XObject object : group.relatedReadOnly(function)) {
                if (object instanceof XType) {
                    commandFilter.allowType(((XType) object));
                } else if (object instanceof XState) {
                    commandFilter.allowState(((XState) object));
                }
            }
        }

        return commandFilter;
    }

    private static void entryConstructor(XGroup group, XFunction entry, Set<XFunction> related) {
        group.iterator(obj -> {
            if (obj instanceof XFunction) {
                XFunction function = (XFunction) obj;
                if (Objects.equals(function.getOwner(), entry.getOwner()) &&
                        Objects.equals(function.getName(), "<init>") &&
                        Objects.equals(function.getDescriptor(), "()V")) {
                    relatedFunctions(group, function, related);
                }
            }
        });
    }

    private static void relatedFunctions(XGroup group, XFunction entry, Set<XFunction> related) {
        if (related.contains(entry)) {
            return;
        }

        related.add(entry);

        Set<XObject> relatedSet = group.relatedReadOnly(entry);
        if (relatedSet != null) {
            for (XObject object : relatedSet) {
                if (object instanceof XFunction) {
                    relatedFunctions(group, (XFunction) object, related);
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
