package io.xserverless.function.converter;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import io.xserverless.function.command.reader.ClassCommandReader;
import io.xserverless.function.dto.XFunction;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import io.xserverless.samples.chains.A;
import io.xserverless.samples.chains.B;
import io.xserverless.samples.chains.C;
import io.xserverless.samples.chains.D;
import org.junit.Test;

import static org.objectweb.asm.Opcodes.ASM7;

public class ChainsConverterTest {
    @Test
    public void testConverter() {
        XGroup group = new XGroup();
        readFunctions(A.class, group);
        readFunctions(B.class, group);
        readFunctions(C.class, group);
        readFunctions(D.class, group);

        group.iterator(obj -> {
            if (obj instanceof XFunction) {
                System.out.println(obj);
            }
        });

        System.out.println("----------- function chains --------------");

        group.iterator(obj -> {
            if (obj instanceof XFunction) {
                outputChains(group, ((XFunction) obj), new HashSet<>());
            }
        });
    }

    private void outputChains(XGroup group, XFunction start, Set<XObject> counted) {
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
            if (object instanceof XFunction) {
                outputChains(group, (XFunction) object, counted);
            }
        }
        counted.remove(start);
    }

    private void readFunctions(Class<?> c, XGroup group) {
        try (InputStream inputStream = c.getResourceAsStream("/" + c.getName().replace('.', '/') + ".class")) {
            assert inputStream != null;
            CommandGroup<ClassCommand> commandGroup = ClassCommandReader.read(inputStream, ASM7);
            new FunctionConverter().getFunctions(commandGroup, group);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
