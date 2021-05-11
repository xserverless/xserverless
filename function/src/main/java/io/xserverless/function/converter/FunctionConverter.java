package io.xserverless.function.converter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.Function;

import static org.objectweb.asm.Opcodes.ASM7;

public class FunctionConverter {
    public List<Function> readFunctions(InputStream inputStream) {
        List<Function> functionList = new ArrayList<>();
        CommandList<ClassCommand> commandList = ClassCommandReader.read(inputStream, ASM7);

        String owner = null;
        for (ClassCommand classCommand : commandList.getCommands()) {
            if (classCommand instanceof ClassCommand.Default) {
                owner = ((ClassCommand.Default) classCommand).getName();
            } else if (classCommand instanceof ClassCommand.Method) {
                functionList.add(((ClassCommand.Method) classCommand).getFunction(owner));
            }
        }

        return functionList;
    }
}
