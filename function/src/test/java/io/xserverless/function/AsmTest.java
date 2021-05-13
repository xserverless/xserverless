package io.xserverless.function;

import java.io.InputStream;
import java.io.PrintWriter;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

/*
 * a unit test shows basic usages of asm.
 */
public class AsmTest {
    @Test
    public void readClass() {
        try (InputStream inputStream = AsmTest.class.getResourceAsStream("/io/xserverless/function/AsmTest.class");
             PrintWriter printWriter = new PrintWriter(System.out)) {

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
        System.out.println(AsmTest.class.getClassLoader());
    }

    int classVariable = 0;

    static int staticVariable = 0;

    void callClassVariable() {
        classVariable++;
    }

    void callStaticVariable() {
        staticVariable++;
    }

    static int multipleReturn() {
        long random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("0")) {
            if(random > 0) {
                return 100;
            }
            return 0;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("1")) {
            if(random > 0) {
                return 100;
            }
            return 1;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("2")) {
            return 2;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("3")) {
            return 3;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("4")) {
            return 4;
        }
        random = System.currentTimeMillis();
        if (String.valueOf(random).endsWith("5")) {
            return 5;
        }
        random = System.currentTimeMillis();
        return -1;
    }
}
