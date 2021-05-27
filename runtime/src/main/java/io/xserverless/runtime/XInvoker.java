package io.xserverless.runtime;

import java.io.File;

public class XInvoker {
    private static final XRegister register = new XRegister();

    // TODO replace the class storage to database later.
    public Object invoke(File dir, String mainClass, String method, Object... args) throws Exception {
        return register.getClassLoader(dir, mainClass).invoke(method, args);
    }
}
