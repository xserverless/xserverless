package io.xserverless.function.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.MethodOperations;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.commands.MethodCommand;
import org.objectweb.asm.Type;

public class XGroup {
    private final Map<String, XObject> functionMap = new HashMap<>();
    private final Map<String, XObject> stateMap = new HashMap<>();
    private final Map<String, XObject> typeMap = new HashMap<>();
    private final Map<XObject, Set<XObject>> pairMap = new HashMap<>();
    private final Map<String, CommandGroup<MethodCommand>> methodMap = new HashMap<>();
    private final Map<String, CommandGroup<ClassCommand>> classMap = new HashMap<>();

    public void addClass(String name, CommandGroup<ClassCommand> classCommandCommandGroup) {
         classMap.put(name, classCommandCommandGroup);
    }

    public Map<String, CommandGroup<ClassCommand>> getClassMap() {
        return classMap;
    }

    public void putMethodCommandGroup(String owner, String name, String descriptor, CommandGroup<MethodCommand> commandGroup) {
        methodMap.put(key(owner, name, descriptor), commandGroup);
    }

    public CommandGroup<MethodCommand> getMethodCommandGroup(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        return methodMap.get(id);
    }

    public XObject createOrGetFunction(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        if (!functionMap.containsKey(id)) {
            XObject function = new XObject(owner, name, descriptor);
            functionMap.put(id, function);
            pairMap.put(function, new HashSet<>());
        }
        return functionMap.get(id);
    }

    public String key(String owner, String name, String descriptor) {
        return new StringJoiner(":", "[", "]").add(owner).add(name).add(descriptor).toString();
    }

    public XObject createOrGetState(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        if (!stateMap.containsKey(id)) {
            XObject state = new XObject(owner, name, descriptor);
            stateMap.put(id, state);
            pairMap.put(state, new HashSet<>());
        }
        return stateMap.get(id);
    }

    public XObject createTypeByName(String name) {
        return createOrGetType("L" + name + ";");
    }

    public XObject createOrGetType(String descriptor) {
        if (!typeMap.containsKey(descriptor)) {
            XObject type = new XObject(descriptor);
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

        if (related.isFunction()) {
            Type methodType = Type.getMethodType(related.getDescriptor());

            methodType.getReturnType();
        }

    }

    public Set<XObject> relatedReadOnly(XObject obj) {
        return Collections.unmodifiableSet(pairMap.getOrDefault(obj, Collections.emptySet()));
    }

    public Set<XObject> states(String owner, String name, String descriptor, Set<String> checked) {
        if (checked.contains(key(owner, name, descriptor))) {
            // checked
            return Collections.emptySet();
        }
        if (Objects.equals("<init>", name)) {
            // constructors
            return Collections.emptySet();
        }
        Set<XObject> objects = pairMap.get(new XObject(owner, name, descriptor));
        if (objects == null) {
            // rt.jar
            return Collections.emptySet();
        }
        Set<XObject> states = new HashSet<>();
        for (XObject object : objects) {
            if (object.isState()) {
                states.add((object));
            }
        }
        if (!states.isEmpty()) {
            return states;
        }

        CommandGroup<MethodCommand> commandGroup = getMethodCommandGroup(owner, name, descriptor);
        if (commandGroup == null) {
            // out of scope
            return Collections.emptySet();
        }
        List<MethodOperations.Operation> operations = MethodOperations.operations(commandGroup);

        for (MethodOperations.Operation operation : operations) {
            MethodOperations.Ref ref = operation.getRef();
            if (ref != null && !Objects.equals(operation.getName(), "<init>")) {
                MethodOperations.Operation createdFrom = ref.getCreatedFrom();
                if (createdFrom != null) {
                    if (!Objects.equals(createdFrom.getName(), "<init>")) {
                        states.addAll(states(operation.getOwner(), operation.getName(), operation.getDescriptor(), checked));
                        checked.add(key(operation.getOwner(), operation.getName(), operation.getDescriptor()));
                    }
                }
            }
        }

        return states;
    }

    public void iterator(Consumer<XObject> c) {
        for (XObject object : pairMap.keySet()) {
            c.accept(object);
        }
    }

    public Stream<XObject> stream() {
        return pairMap.keySet().stream();
    }
}
