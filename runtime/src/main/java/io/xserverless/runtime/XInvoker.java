package io.xserverless.runtime;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class XInvoker {
    private static final XRegister register = new XRegister();

    // TODO replace the class storage to database later.
    public Object invoke(File dir, String mainClass, String method, Object... args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        XClassLoader classLoader = register.getClassLoader(dir);
        ctx.setClassLoader(classLoader);
        ctx.scan(mainClass.substring(0, mainClass.lastIndexOf(".")));
//        ctx.register(Controller.class);
//
//        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
//        xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));
//        PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
//        propReader.loadBeanDefinitions(new ClassPathResource("otherBeans.properties"));
        ctx.refresh();

        Class<?> c = classLoader.loadClass(mainClass);
        Object o = ctx.getBean(c);
        Method clazzMethod;
        if (args != null) {
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            clazzMethod = o.getClass().getMethod(method, argTypes);
        } else {
            clazzMethod = o.getClass().getMethod(method);
        }

        return clazzMethod.invoke(o, args);
    }
}
