package io.xserverless.function.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import io.xserverless.function.AsmTest;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.command.sample.SampleClass;
import io.xserverless.function.command.writer.ClassCommandWriter;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class CommandTest {
    @Test
    public void readClass() {
        try (InputStream inputStream = AsmTest.class.getResourceAsStream("/" + ClassCommandReader.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;

            CommandList<ClassCommand> commandList = ClassCommandReader.read(inputStream, ASM7);

            for (ClassCommand command : commandList.getCommands()) {
                printCommand(command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void writeClass() {
        File file = new File("./output/", SampleClass.class.getName().replace('.', '/') + ".class");
        file.getParentFile().mkdirs();
        System.out.println(file.getAbsolutePath());
        try (InputStream inputStream = AsmTest.class.getResourceAsStream("/" + SampleClass.class.getName().replace('.', '/') + ".class");
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            assert inputStream != null;
            CommandList<ClassCommand> commandList = ClassCommandReader.read(inputStream, ASM7);

            byte[] bytes = new ClassCommandWriter().write(commandList);

            fileOutputStream.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printCommand(Command command) {
        Class<? extends Command> commandClass = command.getClass();
        System.out.println(commandClass.getSimpleName());
        for (Field declaredField : commandClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                Object o = declaredField.get(command);
                if (o instanceof CommandList) {
                    for (Command c : ((CommandList<?>) o).getCommands()) {
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
