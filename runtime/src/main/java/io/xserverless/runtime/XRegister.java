package io.xserverless.runtime;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

public class XRegister {
    private static final Map<String, XClassLoader> classLoaderMap = new WeakHashMap<>();

    public void register(String key, XClassLoader classLoader) {
        classLoaderMap.put(key, classLoader);
    }

    public XClassLoader getClassLoader(File dir) {
        String path = dir.getAbsolutePath();

        XClassLoader classLoader = classLoaderMap.get(path);
        if (classLoader == null) {
            synchronized (Integer.valueOf(path.hashCode())) {
                classLoader = classLoaderMap.get(path);
                if (classLoader == null) {
                    classLoader = new XClassLoader(dir, this);
                }
            }
        }
        return classLoader;
    }
}
