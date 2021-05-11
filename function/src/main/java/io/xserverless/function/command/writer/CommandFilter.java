package io.xserverless.function.command.writer;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import io.xserverless.function.dto.Function;
import io.xserverless.function.dto.State;


public class CommandFilter {
    private Set<String> allowTypeSet = new HashSet<>();
    private Set<String> allowFunctionSet = new HashSet<>();
    private Set<String> allowStateSet = new HashSet<>();

    public static final CommandFilter ALL = new CommandFilter() {
        public boolean type(String name) {
            return true;
        }

        public boolean function(String owner, String name, String descriptor) {
            return true;
        }

        public boolean state(String descriptor) {
            return true;
        }
    };

    public void allowType(String name) {
        allowTypeSet.add(name);
    }

    public void allowFunction(Function function) {
        String string = new StringJoiner(":", "[", "]").add(function.getOwner()).add(function.getName()).add(function.getDescriptor()).toString();
        allowFunctionSet.add(string);
        allowTypeSet.add(function.getOwner());
    }

    public void allowState(State state) {
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
