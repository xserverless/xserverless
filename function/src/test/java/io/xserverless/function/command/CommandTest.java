package io.xserverless.function.command;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.samples.fibonacci.Fibonacci;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class CommandTest {
    @Test
    public void readClass() {
        try (InputStream inputStream = Fibonacci.class.getResourceAsStream("/" + Fibonacci.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;

            CommandGroup<ClassCommand> commandGroup = ClassCommandReader.read(inputStream, ASM7);

            for (ClassCommand command : commandGroup.getCommands()) {
                printCommand(command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void writeClass() {
        try (InputStream inputStream = Fibonacci.class.getResourceAsStream("/" + Fibonacci.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            CommandGroup<ClassCommand> commandGroup = ClassCommandReader.read(inputStream, ASM7);

            for (ClassCommand classCommand : commandGroup.getCommands()) {
                if (classCommand instanceof ClassCommand.Default) {
                    ((ClassCommand.Default) classCommand).setName("io/xserverless/function/command/TestFibonacci");
                }
            }

            byte[] bytes = new ClassCommandWriter().write(commandGroup, CommandFilter.ALL);

            Class<?> sampleClass = new TestClassLoader().defineClass("io.xserverless.function.command.TestFibonacci", bytes);
            Method main = sampleClass.getMethod("print", int.class);
            System.out.println(main);
            main.invoke(null, 10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b)
                throws ClassFormatError {
            return defineClass(name, b, 0, b.length);
        }
    }

    private void printCommand(Command command) {
        Class<? extends Command> commandClass = command.getClass();
        System.out.println(commandClass.getName());
        for (Field declaredField : commandClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                Object o = declaredField.get(command);
                if (o instanceof CommandGroup) {
                    for (Command c : ((CommandGroup<?>) o).getCommands()) {
                        printCommand(c);
                    }
                } else {
                    System.out.println("\t" + declaredField.getName() + ":\t" + o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }
}
