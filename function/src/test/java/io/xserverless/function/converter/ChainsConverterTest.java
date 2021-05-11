package io.xserverless.function.converter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.Function;
import io.xserverless.samples.chains.A;
import io.xserverless.samples.chains.B;
import io.xserverless.samples.chains.C;
import io.xserverless.samples.chains.D;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class ChainsConverterTest {
    @Test
    public void testConverter() {
        List<Function> functionsA = readFunctions(A.class);
        List<Function> functionsB = readFunctions(B.class);
        List<Function> functionsC = readFunctions(C.class);
        List<Function> functionsD = readFunctions(D.class);

        Map<String, Function> functionMap = new HashMap<>();
        for (Function function : functionsA) {
            System.out.println(function);
            functionMap.put(function.getId(), function);
        }
        for (Function function : functionsB) {
            System.out.println(function);
            functionMap.put(function.getId(), function);
        }
        for (Function function : functionsC) {
            System.out.println(function);
            functionMap.put(function.getId(), function);
        }
        for (Function function : functionsD) {
            System.out.println(function);
            functionMap.put(function.getId(), function);
        }

        System.out.println("----------- function chains --------------");

        for (String start : functionMap.keySet()) {
            System.out.println("--");
            outputChains(functionMap, start, new HashSet<>());
        }
    }

    private void outputChains(Map<String, Function> map, String start, Set<String> counted) {
        if (counted.contains(start)) {
            return;
        }

        for (int i = 0; i < counted.size(); i++) {
            System.out.print("\t");
        }
        System.out.print("|-\t");
        System.out.println(start);

        counted.add(start);

        if (map.containsKey(start)) {
            for (String relatedFunction : map.get(start).getRelatedFunctions()) {
                outputChains(map, relatedFunction, counted);
            }
        }

        counted.remove(start);
    }

    private List<Function> readFunctions(Class<?> c) {
        try (InputStream inputStream = c.getResourceAsStream("/" + c.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            CommandGroup<ClassCommand> commandGroup = ClassCommandReader.read(inputStream, ASM7);
            List<Function> functionList = new FunctionConverter().getFunctions(commandGroup);
            return functionList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
