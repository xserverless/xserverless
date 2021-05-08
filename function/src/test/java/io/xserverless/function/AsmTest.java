package io.xserverless.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.junit.Test;
import org.junit.platform.commons.JUnitException;
import org.junit.runner.RunWith;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

/*
 * a unit test shows basic usages of asm.
 */
public class AsmTest {
    @Test
    public void readClass() {
        try(InputStream inputStream = AsmTest.class.getResourceAsStream("/io/xserverless/function/AsmTest.class");
            PrintWriter printWriter = new PrintWriter(System.out)){

            assert inputStream != null;

            ClassReader classReader = new ClassReader(inputStream);
            classReader.accept(new TraceClassVisitor(printWriter), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void showClassLoader() {
        System.out.println(String.class.getClassLoader());
        System.out.println(java.io.PrintWriter.class.getClassLoader());
        System.out.println(javax.swing.text.ComponentView.class.getClassLoader());
        System.out.println(com.sun.javadoc.AnnotatedType.class.getClassLoader());
        System.out.println(AsmTest.class.getClassLoader());
    }
}
