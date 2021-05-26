package io.xserverless.function.command;

import java.io.InputStream;

import io.xserverless.function.AsmTest;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM9;

public class MethodOperationsTest {
    @Test
    public void testOperation() {
        try (InputStream inputStream = AsmTest.class.getResourceAsStream("/" + AsmTest.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;

            CommandGroup.ClassCommandGroup commandGroup = ClassCommandReader.read(inputStream, ASM9);

            for (ClassCommand command : commandGroup.getCommands()) {
                if (command instanceof ClassCommand.Method) {
                    MethodOperations.operations(((ClassCommand.Method) command).getMethod());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
