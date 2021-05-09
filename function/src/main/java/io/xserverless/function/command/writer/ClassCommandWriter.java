package io.xserverless.function.command.writer;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.ClassCommand;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ASM7;

public class ClassCommandWriter {
    public byte[] write(CommandList<ClassCommand> commandList) {
        ClassWriter classWriter = new ClassWriter(ASM7);
        for (ClassCommand command : commandList.getCommands()) {
            command.write(classWriter);
        }

        return classWriter.toByteArray();
    }
}
