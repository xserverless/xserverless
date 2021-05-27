package io.xserverless.runtime;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class XInvokerTest {
    @Test
    public void testInvoke() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Object o = new XInvoker().invoke(new File("/Users/liminghua/git/io.xserverless/xserverless/runtime/target/echo"),
                "io.xserverless.devcenter.functions.controller.FunctionController",
                "echo",
                "hello world");
        System.out.println(o);
    }
}
