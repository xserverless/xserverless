package io.xserverless.function.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.commands.MethodCommand;
import io.xserverless.function.dto.XFunction;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import org.objectweb.asm.Opcodes;

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

    public enum StatelessType {STATIC_FIELD, FIELD}

    public Set<StatelessType> stateless(String owner, String name, String descriptor, XGroup group) {
        CommandGroup<MethodCommand> commandGroup = group.getMethodCommandGroup(owner, name, descriptor);

        Set<StatelessType> set = new HashSet<>();

        for (MethodCommand command : commandGroup.getCommands()) {
            if (command instanceof MethodCommand.FieldInsn) {
                switch (((MethodCommand.FieldInsn) command).getOpcode()) {
                    case Opcodes.PUTSTATIC:
                    case Opcodes.GETSTATIC:
                        set.add(StatelessType.STATIC_FIELD);
                        break;
                    case Opcodes.PUTFIELD:
                    case Opcodes.GETFIELD:
                        set.add(StatelessType.FIELD);
                        break;
                    default:
                        break;
                }
            }
        }

        XFunction function = group.createOrGetFunction(owner, name, descriptor);
        Set<XObject> xObjects = group.relatedReadOnly(function);
        for (XObject xObject : xObjects) {
            if (xObject instanceof XFunction) {
                Set<StatelessType> types = stateless(((XFunction) xObject).getOwner(), ((XFunction) xObject).getName(), ((XFunction) xObject).getDescriptor(), group);
                if (types.contains(StatelessType.STATIC_FIELD)) {
                    set.addAll(types);
                } else {
                    // TODO 检查调用对应方法的对象是否在方法内创建
                }
            }
        }
        return set;
    }
}
