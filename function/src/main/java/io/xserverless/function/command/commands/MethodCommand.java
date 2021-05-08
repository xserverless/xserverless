package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.Handle;
import org.objectweb.asm.TypePath;

public interface MethodCommand extends Command {
    @Data
    @AllArgsConstructor
    class Parameter implements MethodCommand {
        String name;
        int access;
    }

    @Data
    @AllArgsConstructor
    class AnnotationDefault implements MethodCommand {
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class Annotation implements MethodCommand {
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class AnnotableParameterCount implements MethodCommand {
        int parameterCount;
        boolean visible;
    }

    @Data
    @AllArgsConstructor
    class ParameterAnnotation implements MethodCommand {
        int parameter;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class Attribute implements MethodCommand {
        org.objectweb.asm.Attribute attribute;
    }

    @Data
    @AllArgsConstructor
    class Code implements MethodCommand {
    }

    @Data
    @AllArgsConstructor
    class Frame implements MethodCommand {
        int type;
        int numLocal;
        Object[] local;
        int numStack;
        Object[] stack;
    }

    @Data
    @AllArgsConstructor
    class Insn implements MethodCommand {
        int opcode;
    }

    @Data
    @AllArgsConstructor
    class IntInsn implements MethodCommand {
        int opcode;
        int operand;
    }

    @Data
    @AllArgsConstructor
    class VarInsn implements MethodCommand {
        int opcode;
        int var;
    }

    @Data
    @AllArgsConstructor
    class TypeInsn implements MethodCommand {
        int opcode;
        String type;
    }

    @Data
    @AllArgsConstructor
    class FieldInsn implements MethodCommand {
        int opcode;
        String owner;
        String name;
        String descriptor;
    }

    @Data
    @AllArgsConstructor
    class MethodInsn implements MethodCommand {
        int opcode;
        String owner;
        String name;
        String descriptor;
        boolean isInterface;
    }

    @Data
    @AllArgsConstructor
    class InvokeDynamicInsn implements MethodCommand {
        String name;
        String descriptor;
        Handle bootstrapMethodHandle;
        Object[] bootstrapMethodArguments;
    }

    @Data
    @AllArgsConstructor
    class JumpInsn implements MethodCommand {
        int opcode;
        org.objectweb.asm.Label label;
    }

    @Data
    @AllArgsConstructor
    class Label implements MethodCommand {
        org.objectweb.asm.Label label;
    }

    @Data
    @AllArgsConstructor
    class LdcInsn implements MethodCommand {
        Object value;
    }

    @Data
    @AllArgsConstructor
    class IincInsn implements MethodCommand {
        int var;
        int increment;
    }

    @Data
    @AllArgsConstructor
    class TableSwitchInsn implements MethodCommand {
        int min;
        int max;
        org.objectweb.asm.Label dflt;
        org.objectweb.asm.Label[] labels;
    }

    @Data
    @AllArgsConstructor
    class LookupSwitchInsn implements MethodCommand {
        org.objectweb.asm.Label dflt;
        int[] keys;
        org.objectweb.asm.Label[] labels;
    }

    @Data
    @AllArgsConstructor
    class MultiANewArrayInsn implements MethodCommand {
        String descriptor;
        int numDimensions;
    }

    @Data
    @AllArgsConstructor
    class InsnAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class TryCatchBlock implements MethodCommand {
        org.objectweb.asm.Label start;
        org.objectweb.asm.Label end;
        org.objectweb.asm.Label handler;
        String type;
    }

    @Data
    @AllArgsConstructor
    class TryCatchAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class LocalVariable implements MethodCommand {
        String name;
        String descriptor;
        String signature;
        org.objectweb.asm.Label start;
        org.objectweb.asm.Label end;
        int index;
    }

    @Data
    @AllArgsConstructor
    class LocalVariableAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        org.objectweb.asm.Label[] start;
        org.objectweb.asm.Label[] end;
        int[] index;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class LineNumber implements MethodCommand {
        int line;
        org.objectweb.asm.Label start;
    }

    @Data
    @AllArgsConstructor
    class Maxs implements MethodCommand {
        int maxStack;
        int maxLocals;
    }

    @Data
    @AllArgsConstructor
    class End implements MethodCommand {
    }

}
