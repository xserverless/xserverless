package io.xserverless.function.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.command.writer.ClassCommandWriter;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.converter.FunctionConverter;
import io.xserverless.function.dto.Function;
import io.xserverless.function.dto.State;
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

        Map<String, Function> functionMap = new FunctionConverter().convert(list);
        CommandFilter commandFilter = createFilter(functionMap, functionMap.get("io/xserverless/samples/chains/A.ab.()V"));

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

    private CommandFilter createFilter(Map<String, Function> functionMap, Function entry) {
        Map<String, Function> relatedMap = new HashMap<>();
        relatedFunctions(functionMap, entry, relatedMap);

        CommandFilter commandFilter = new CommandFilter();

        for (Function function : relatedMap.values()) {
            for (String relatedType : function.getRelatedTypes()) {
                commandFilter.allowType(relatedType);
            }
            commandFilter.allowFunction(function);
            for (State relatedState : function.getRelatedStates()) {
                commandFilter.allowState(relatedState);
            }
        }

        return commandFilter;
    }

    private void relatedFunctions(Map<String, Function> functionMap, Function entry, Map<String, Function> relatedMap) {
        if (relatedMap.containsKey(entry.getId())) {
            return;
        }

        relatedMap.put(entry.getId(), entry);

        Function function = functionMap.get(entry.getId());
        for (String relatedFunction : function.getRelatedFunctions()) {
            if (functionMap.containsKey(relatedFunction)) {
                relatedFunctions(functionMap, functionMap.get(relatedFunction), relatedMap);
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
