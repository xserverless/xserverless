package io.xserverless.function.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.MethodCommand;

public class XGroup {
    private final Map<String, XFunction> functionMap = new HashMap<>();
    private final Map<String, XState> stateMap = new HashMap<>();
    private final Map<String, XType> typeMap = new HashMap<>();
    private final Map<XObject, Set<XObject>> pairMap = new HashMap<>();
    private final Map<String, CommandGroup<MethodCommand>> methodMap = new HashMap<>();

    public void putMethodCommandGroup(String owner, String name, String descriptor, CommandGroup<MethodCommand> commandGroup) {
        methodMap.put(key(owner, name, descriptor), commandGroup);
    }

    public CommandGroup<MethodCommand> getMethodCommandGroup(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        return methodMap.get(id);
    }

    public XFunction createOrGetFunction(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        if (!functionMap.containsKey(id)) {
            XFunction function = new XFunction(owner, name, descriptor);
            functionMap.put(id, function);
            pairMap.put(function, new HashSet<>());
        }
        return functionMap.get(id);
    }

    private String key(String owner, String name, String descriptor) {
        return new StringJoiner(":", "[", "]").add(owner).add(name).add(descriptor).toString();
    }

    public XState createOrGetState(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        if (!stateMap.containsKey(id)) {
            XState state = new XState(owner, name, descriptor);
            stateMap.put(id, state);
            pairMap.put(state, new HashSet<>());
        }
        return stateMap.get(id);
    }

    public XType createOrGetType(String descriptor) {
        if (!typeMap.containsKey(descriptor)) {
            XType type = new XType(descriptor);
            typeMap.put(descriptor, type);
            pairMap.put(type, new HashSet<>());
        }
        return typeMap.get(descriptor);
    }

    public void addPair(XObject obj, XObject related) {
        if (!pairMap.containsKey(obj)) {
            pairMap.put(obj, new HashSet<>());
        }
        pairMap.get(obj).add(related);
    }

    public Set<XObject> relatedReadOnly(XObject obj) {
        return Collections.unmodifiableSet(pairMap.getOrDefault(obj, Collections.emptySet()));
    }

    public void iterator(Consumer<XObject> c) {
        for (XObject object : pairMap.keySet()) {
            c.accept(object);
        }
    }
}
