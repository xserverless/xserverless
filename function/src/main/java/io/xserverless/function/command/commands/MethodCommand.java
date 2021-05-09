package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

public interface MethodCommand extends Command {
    void write(MethodVisitor visitor);

    @Data
    @AllArgsConstructor
    class Parameter implements MethodCommand {
        String name;
        int access;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitParameter(name, access);
        }
    }

    @Data
    @AllArgsConstructor
    class AnnotationDefault implements MethodCommand {
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotationDefault();
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Annotation implements MethodCommand {
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotation(descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class AnnotableParameterCount implements MethodCommand {
        int parameterCount;
        boolean visible;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitAnnotableParameterCount(parameterCount, visible);
        }
    }

    @Data
    @AllArgsConstructor
    class ParameterAnnotation implements MethodCommand {
        int parameter;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitParameterAnnotation(parameter, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Attribute implements MethodCommand {
        org.objectweb.asm.Attribute attribute;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitAttribute(attribute);
        }
    }

    @Data
    @AllArgsConstructor
    class Code implements MethodCommand {
        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitCode();
        }
    }

    @Data
    @AllArgsConstructor
    class Frame implements MethodCommand {
        int type;
        int numLocal;
        Object[] local;
        int numStack;
        Object[] stack;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitFrame(type, numLocal, local, numStack, stack);
        }
    }

    @Data
    @AllArgsConstructor
    class Insn implements MethodCommand {
        int opcode;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitInsn(opcode);
        }
    }

    @Data
    @AllArgsConstructor
    class IntInsn implements MethodCommand {
        int opcode;
        int operand;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitIntInsn(opcode, operand);
        }
    }

    @Data
    @AllArgsConstructor
    class VarInsn implements MethodCommand {
        int opcode;
        int var;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitVarInsn(opcode, var);
        }
    }

    @Data
    @AllArgsConstructor
    class TypeInsn implements MethodCommand {
        int opcode;
        String type;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitTypeInsn(opcode, type);
        }
    }

    @Data
    @AllArgsConstructor
    class FieldInsn implements MethodCommand {
        int opcode;
        String owner;
        String name;
        String descriptor;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    @Data
    @AllArgsConstructor
    class MethodInsn implements MethodCommand {
        int opcode;
        String owner;
        String name;
        String descriptor;
        boolean isInterface;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Data
    @AllArgsConstructor
    class InvokeDynamicInsn implements MethodCommand {
        String name;
        String descriptor;
        Handle bootstrapMethodHandle;
        Object[] bootstrapMethodArguments;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }
    }

    @Data
    @AllArgsConstructor
    class JumpInsn implements MethodCommand {
        int opcode;
        org.objectweb.asm.Label label;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitJumpInsn(opcode, label);
        }
    }

    @Data
    @AllArgsConstructor
    class Label implements MethodCommand {
        org.objectweb.asm.Label label;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLabel(label);
        }
    }

    @Data
    @AllArgsConstructor
    class LdcInsn implements MethodCommand {
        Object value;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLdcInsn(value);
        }
    }

    @Data
    @AllArgsConstructor
    class IincInsn implements MethodCommand {
        int var;
        int increment;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitIincInsn(var, increment);

        }
    }

    @Data
    @AllArgsConstructor
    class TableSwitchInsn implements MethodCommand {
        int min;
        int max;
        org.objectweb.asm.Label dflt;
        org.objectweb.asm.Label[] labels;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    @Data
    @AllArgsConstructor
    class LookupSwitchInsn implements MethodCommand {
        org.objectweb.asm.Label dflt;
        int[] keys;
        org.objectweb.asm.Label[] labels;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLookupSwitchInsn(dflt, keys, labels);

        }
    }

    @Data
    @AllArgsConstructor
    class MultiANewArrayInsn implements MethodCommand {
        String descriptor;
        int numDimensions;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitMultiANewArrayInsn(descriptor, numDimensions);

        }
    }

    @Data
    @AllArgsConstructor
    class InsnAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitInsnAnnotation(typeRef, typePath, descriptor, visible);

            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class TryCatchBlock implements MethodCommand {
        org.objectweb.asm.Label start;
        org.objectweb.asm.Label end;
        org.objectweb.asm.Label handler;
        String type;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitTryCatchBlock(start, end, handler, type);
        }
    }

    @Data
    @AllArgsConstructor
    class TryCatchAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);

            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
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

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLocalVariable(name, descriptor, signature, start, end, index);
        }
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

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);

            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class LineNumber implements MethodCommand {
        int line;
        org.objectweb.asm.Label start;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLineNumber(line, start);
        }
    }

    @Data
    @AllArgsConstructor
    class Maxs implements MethodCommand {
        int maxStack;
        int maxLocals;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitMaxs(maxStack, maxLocals);
        }
    }

    @Data
    @AllArgsConstructor
    class End implements MethodCommand {
        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitEnd();
        }
    }
}
