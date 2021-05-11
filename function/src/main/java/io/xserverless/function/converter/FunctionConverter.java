package io.xserverless.function.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.dto.Function;

public class FunctionConverter {
    public List<Function> getFunctions(CommandGroup<ClassCommand> commandGroup) {
        List<Function> functionList = new ArrayList<>();
        String owner = null;
        for (ClassCommand classCommand : commandGroup.getCommands()) {
            if (classCommand instanceof ClassCommand.Default) {
                owner = ((ClassCommand.Default) classCommand).getName();
            } else if (classCommand instanceof ClassCommand.Method) {
                functionList.add(((ClassCommand.Method) classCommand).getFunction(owner));
            }
        }

        return functionList;
    }

    public Map<String, Function> convert(List<CommandGroup<ClassCommand>> commandGroupList) {
        Map<String, Function> functionMap = new HashMap<>();
        for (CommandGroup<ClassCommand> commandGroup : commandGroupList) {
            for (Function function : getFunctions(commandGroup)) {
                functionMap.put(function.getId(), function);
            }
        }
        return functionMap;
    }
}
