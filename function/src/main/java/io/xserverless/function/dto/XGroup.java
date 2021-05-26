package io.xserverless.function.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
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
    private final Map<String, CommandGroup.ClassCommandGroup> classMap = new HashMap<>();

    public void addClass(String name, CommandGroup.ClassCommandGroup classCommandCommandGroup) {
        classMap.put(name, classCommandCommandGroup);
    }

    public Map<String, CommandGroup.ClassCommandGroup> getClassMap() {
        return classMap;
    }

    public void putMethodCommandGroup(String owner, String name, String descriptor, CommandGroup<MethodCommand> commandGroup) {
        methodMap.put(key(owner, name, descriptor), commandGroup);
    }

    public CommandGroup<MethodCommand> getMethodCommandGroup(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        return methodMap.get(id);
    }

    public XObject createFunction(String owner, String name, String descriptor) {
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

    public XObject createState(String owner, String name, String descriptor) {
        String id = key(owner, name, descriptor);
        if (!stateMap.containsKey(id)) {
            XObject state = new XObject(owner, name, descriptor);
            stateMap.put(id, state);
            pairMap.put(state, new HashSet<>());
        }
        return stateMap.get(id);
    }

    public XObject createTypeByName(String name) {
        return createType("L" + name + ";");
    }

    public XObject createType(String desc) {
        String descriptor = desc.replace("[", "");
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

    public void updateInherits() {
        Map<XObject, Set<XObject>> revertPairMap = new HashMap<>();
        for (Map.Entry<XObject, Set<XObject>> entry : pairMap.entrySet()) {
            for (XObject object : entry.getValue()) {
                if (!revertPairMap.containsKey(object)) {
                    revertPairMap.put(object, new HashSet<>());
                }
                revertPairMap.get(object).add(entry.getKey());
            }
        }
        List<XObject> functionList = new ArrayList<>(revertPairMap.keySet());

        for (XObject object : functionList) {
            String owner = object.getOwner();
            String name = object.getName();
            String descriptor = object.getDescriptor();

            CommandGroup.ClassCommandGroup classCommandGroup = getClassMap().get(owner);
            if (classCommandGroup != null && !classCommandGroup.getMethodMap().containsKey(name + descriptor)) {
                LinkedList<String> typeNames = new LinkedList<>();
                typeNames.add(owner);
                while (!typeNames.isEmpty()) {
                    String type = typeNames.pop();
                    CommandGroup.ClassCommandGroup commandGroup = getClassMap().get(type);
                    if (commandGroup == null) {
                        continue;
                    }

                    if (commandGroup.getDefaultCommand() != null) {
                        String[] interfaces = commandGroup.getDefaultCommand().getInterfaces();
                        if (interfaces != null) {
                            typeNames.addAll(Arrays.asList(interfaces));
                        }
                        String superName = commandGroup.getDefaultCommand().getSuperName();
                        if (superName != null) {
                            typeNames.add(superName);
                        }
                    }

                    if (commandGroup.getMethodMap().containsKey(name + descriptor)) {
                        ClassCommand.Method method = commandGroup.getMethodMap().get(name + descriptor);
                        Set<XObject> objects = revertPairMap.get(object);
                        for (XObject function : objects) {
                            addPair(function, createFunction(method.getMethod().getOwner(), name, descriptor));
                        }
                    }
                }
            }
        }
    }

    public Set<XObject> relatedReadOnly(XObject obj) {
        return pairMap.getOrDefault(obj, Collections.emptySet());
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

    public Stream<XObject> stream() {
        return pairMap.keySet().stream();
    }

    public boolean isAnnotation(XObject object) {
        CommandGroup.ClassCommandGroup commandGroup = classMap.get(Type.getType(object.getDescriptor()).getInternalName());
        return commandGroup != null && commandGroup.isAnnotation();
    }
}
