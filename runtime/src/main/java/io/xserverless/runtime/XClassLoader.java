package io.xserverless.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class XClassLoader extends ClassLoader {
    private AnnotationConfigApplicationContext context;
    private File dir;
    private Class<?> clazz = null;
    private Method method = null;

    public XClassLoader(XRegister register, File dir, String c) {
        this.dir = dir;
        loadClasses(dir);

        context = new AnnotationConfigApplicationContext();
        context.setClassLoader(this);
        context.scan(c.substring(0, c.lastIndexOf(".")));
        context.refresh();

        try {
            clazz = loadClass(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        register.register(dir.getAbsolutePath(), this);
    }

    public Object invoke(String m, Object... args) throws Exception {
        if (method == null) {
            if (args != null) {
                Class<?>[] argTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    argTypes[i] = args[i].getClass();
                }
                method = clazz.getMethod(m, argTypes);
            } else {
                method = clazz.getMethod(m);
            }
        }
        return method.invoke(context.getBean(clazz), args);
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

    @Override
    protected URL findResource(String name) {
        File file = new File(dir, name);
        if (file.exists()) {
            try {
                return file.toPath().toUri().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return super.getResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        URL url = findResource(name);
        if (url != null) {
            return Collections.enumeration(Collections.singletonList(url));
        }
        return super.findResources(name);
    }
}
