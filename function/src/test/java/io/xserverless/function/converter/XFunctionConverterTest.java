package io.xserverless.function.converter;

import java.io.InputStream;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.XGroup;
import io.xserverless.samples.fibonacci.Fibonacci;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class XFunctionConverterTest {
    @Test
    public void testConverter() {
        try (InputStream inputStream = Fibonacci.class.getResourceAsStream("/" + Fibonacci.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            CommandGroup<ClassCommand> commandGroup = ClassCommandReader.read(inputStream, ASM7);
            XGroup group = new XGroup();
            new FunctionConverter().getFunctions(commandGroup, group);

            group.iterator(System.out::println);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
