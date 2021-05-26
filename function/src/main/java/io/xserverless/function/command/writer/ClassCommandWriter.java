package io.xserverless.function.command.writer;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ASM9;

public class ClassCommandWriter {
    public byte[] write(CommandGroup.ClassCommandGroup commandGroup, CommandFilter filter) {
        if (filter.type(commandGroup.getName())) {
            ClassWriter classWriter = new ClassWriter(ASM9);
            for (ClassCommand command : commandGroup.getCommands()) {
                command.write(classWriter, filter);
            }

            return classWriter.toByteArray();
        }
        return new byte[0];
    }
}
