package io.xserverless.function.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.XGroup;
import io.xserverless.samples.chains.A;
import io.xserverless.samples.chains.B;
import io.xserverless.samples.chains.C;
import io.xserverless.samples.chains.D;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class CommandFilterTest {
    @Test
    public void testFilter() {
        List<CommandGroup<ClassCommand>> list = new ArrayList<>();
        list.add(readFunctions(A.class));
        list.add(readFunctions(B.class));
        list.add(readFunctions(C.class));
        list.add(readFunctions(D.class));

        XGroup group = new FunctionConverter().convert(list);

        CommandFilter commandFilter = CommandFilter.createFilter(group, group.createOrGetFunction("io/xserverless/samples/chains/A", "ab", "()V"));

        System.out.println(commandFilter);

        File outputDir = new File("./target/test-outputs");
        for (CommandGroup<ClassCommand> commandGroup : list) {
            byte[] bytes = new ClassCommandWriter().write(commandGroup, commandFilter);
            if (bytes.length > 0) {
                File file = new File(outputDir, commandGroup.getName() + ".class");
                file.getParentFile().mkdirs();
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private CommandGroup<ClassCommand> readFunctions(Class<?> c) {
        try (InputStream inputStream = c.getResourceAsStream("/" + c.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            return ClassCommandReader.read(inputStream, ASM7);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
