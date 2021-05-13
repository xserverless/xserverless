package io.xserverless.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class XClassLoader extends ClassLoader {
    private Map<String, Class<?>> classMap = new HashMap<>();

    public XClassLoader(File dir, XRegister register) {
        loadClasses(dir);
        register.register(dir.getAbsolutePath(), this);
    }

    public Object invoke(String mainClass, String method, Object... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!classMap.containsKey(mainClass)) {
            classMap.put(mainClass, loadClass(mainClass));
        }
        Class<?> clazz = classMap.get(mainClass);
        Object o = clazz.getConstructor().newInstance();

        Method clazzMethod;
        if (args != null) {
            Class<?>[] argTypes = new Class[args.length];
            clazzMethod = clazz.getMethod(method, argTypes);
        } else {
            clazzMethod = clazz.getMethod(method);
        }

        return clazzMethod.invoke(o, args);
    }

    private void loadClasses(File dir) {
        String rootAbsolutePath = dir.getAbsolutePath();
        int dirPathLength = rootAbsolutePath.length();
        if (!rootAbsolutePath.endsWith("/")) {
            dirPathLength++;
        }

        LinkedList<File> fileStack = new LinkedList<>();
        fileStack.push(dir);

        while (!fileStack.isEmpty()) {
            File file = fileStack.pop();
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    fileStack.addAll(0, Arrays.asList(files));
                }
            } else if (file.getName().endsWith(".class")) {
                String className = file.getAbsolutePath()
                        .substring(dirPathLength)
                        .replace('/', '.');
                className = className.substring(0, className.length() - 6);
                try {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    defineClass(className, bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
