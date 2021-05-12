package io.xserverless.runtime;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class XInvokerTest {
    @Test
    public void testInvoke() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new XInvoker().invoke(new File("/Users/liminghua/git/io.xserverless/xserverless/function/target/test-outputs"),
                "io.xserverless.samples.chains.B",
                "bc");
    }
}
