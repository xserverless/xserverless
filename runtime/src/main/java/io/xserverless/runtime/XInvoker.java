package io.xserverless.runtime;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class XInvoker {
    private static final XRegister register = new XRegister();

    // TODO replace the class storage to database later.
    public Object invoke(File dir, String mainClass, String method, Object... args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        XClassLoader classLoader = register.getClassLoader(dir);
        return classLoader.invoke(mainClass, method, args);
    }
}
