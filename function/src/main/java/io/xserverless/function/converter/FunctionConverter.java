package io.xserverless.function.converter;

import java.util.List;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.dto.XGroup;

public class FunctionConverter {
    public void getFunctions(CommandGroup<ClassCommand> commandGroup, XGroup group) {
        String owner = null;
        for (ClassCommand classCommand : commandGroup.getCommands()) {
            if (classCommand instanceof ClassCommand.Default) {
                owner = ((ClassCommand.Default) classCommand).getName();
            } else if (classCommand instanceof ClassCommand.Method) {
                ((ClassCommand.Method) classCommand).getFunction(owner, group);
            }
        }
    }

    public XGroup convert(List<CommandGroup<ClassCommand>> commandGroupList) {
        XGroup group = new XGroup();
        for (CommandGroup<ClassCommand> commandGroup : commandGroupList) {
            getFunctions(commandGroup, group);
        }
        return group;
    }
}
