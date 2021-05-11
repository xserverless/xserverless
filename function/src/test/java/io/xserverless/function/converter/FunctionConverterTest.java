package io.xserverless.function.converter;

import java.io.InputStream;
import java.util.List;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.Function;
import io.xserverless.samples.fibonacci.Fibonacci;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class FunctionConverterTest {
    @Test
    public void testConverter() {
        try (InputStream inputStream = Fibonacci.class.getResourceAsStream("/" + Fibonacci.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            CommandGroup<ClassCommand> commandGroup = ClassCommandReader.read(inputStream, ASM7);
            List<Function> functionList = new FunctionConverter().getFunctions(commandGroup);

            for (Function function : functionList) {
                System.out.println(function.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
