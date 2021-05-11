package io.xserverless.function.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.XFunction;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import io.xserverless.function.dto.XState;
import io.xserverless.function.dto.XType;
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

        CommandFilter commandFilter = createFilter(group, group.createOrGetFunction("io/xserverless/samples/chains/A", "ab", "()V"));

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

    private CommandFilter createFilter(XGroup group, XFunction entry) {
        Set<XFunction> related = new HashSet<>();
        relatedFunctions(group, entry, related);

        CommandFilter commandFilter = new CommandFilter();

        for (XFunction function : related) {
            commandFilter.allowFunction(function);
            for (XObject object : group.relatedReadOnly(function)) {
                if (object instanceof XType) {
                    commandFilter.allowType(((XType) object));
                } else if (object instanceof XState) {
                    commandFilter.allowState(((XState) object));
                }
            }
        }

        return commandFilter;
    }

    private void relatedFunctions(XGroup group, XFunction entry, Set<XFunction> related) {
        if (related.contains(entry)) {
            return;
        }

        related.add(entry);

        Set<XObject> relatedSet = group.relatedReadOnly(entry);
        if (relatedSet != null) {
            for (XObject object : relatedSet) {
                if (object instanceof XFunction) {
                    relatedFunctions(group, (XFunction) object, related);
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
