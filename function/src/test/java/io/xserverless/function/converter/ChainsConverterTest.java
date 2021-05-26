package io.xserverless.function.converter;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import io.xserverless.samples.chains.A;
import io.xserverless.samples.chains.B;
import io.xserverless.samples.chains.C;
import io.xserverless.samples.chains.D;
import io.xserverless.samples.states.Counter;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM9;

public class ChainsConverterTest {
    @Test
    public void testConverter() {
        XGroup group = new XGroup();
        readFunctions(A.class, group);
        readFunctions(B.class, group);
        readFunctions(C.class, group);
        readFunctions(D.class, group);

        group.stream().forEach(obj -> {
            if (obj.isFunction()) {
                System.out.println(obj);
            }
        });

        System.out.println("----------- function chains --------------");

        group.stream().forEach(obj -> {
            if (obj.isFunction()) {
                outputChains(group, obj, new HashSet<>());
            }
        });
    }

    @Test
    public void testStateless() {
        XGroup group = new XGroup();
        readFunctions(Counter.class, group);

        group.stream().forEach(obj -> {
            if (obj.isFunction()) {
                System.out.println((obj).getOwner());
                System.out.println((obj).getName());
                System.out.println((obj).getDescriptor());
                System.out.println(group.states((obj).getOwner(), (obj).getName(), (obj).getDescriptor(), new HashSet<>()));
                System.out.println();
            }
        });
    }

    private void outputChains(XGroup group, XObject start, Set<XObject> counted) {
        if (counted.contains(start)) {
            return;
        }

        for (int i = 0; i < counted.size(); i++) {
            System.out.print("\t");
        }
        System.out.print("|-\t");
        System.out.println(start.getOwner() + "." + start.getName() + start.getDescriptor());

        counted.add(start);
        for (XObject object : group.relatedReadOnly(start)) {
            if (object.isFunction()) {
                outputChains(group, object, counted);
            }
        }
        counted.remove(start);
    }

    private void readFunctions(Class<?> c, XGroup group) {
        try (InputStream inputStream = c.getResourceAsStream("/" + c.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            CommandGroup.ClassCommandGroup commandGroup = ClassCommandReader.read(inputStream, ASM9);
            new FunctionConverter().getFunctions(commandGroup, group);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
