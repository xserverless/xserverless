package io.xserverless.function.converter;

import java.io.InputStream;
import java.util.List;

import io.xserverless.function.dto.Function;
import io.xserverless.samples.fibonacci.Fibonacci;
import org.junit.Test;

public class FunctionConverterTest {
    @Test
    public void testConverter() {
        try (InputStream inputStream = Fibonacci.class.getResourceAsStream("/" + Fibonacci.class.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            List<Function> functionList = new FunctionConverter().readFunctions(inputStream);

            for (Function function : functionList) {
                System.out.println(function.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
