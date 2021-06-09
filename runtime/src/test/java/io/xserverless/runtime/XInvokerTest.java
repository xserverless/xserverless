package io.xserverless.runtime;

import java.io.File;

import org.junit.Test;

public class XInvokerTest {
    @Test
    public void testInvoke() throws Exception {
        Object o = new XInvoker().invoke(new File("/Users/liminghua/git/io.xserverless/xserverless/runtime/target/echo"),
                "io.xserverless.devcenter.functions.controller.FunctionController",
                "echo",
                "hello world");
        System.out.println(o);
    }
}
