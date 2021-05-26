package io.xserverless.function.command.commands;

import java.util.ArrayList;
import java.util.List;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.MethodOperations;
import io.xserverless.function.command.OpcodeDecode;
import io.xserverless.function.command.reader.SignatureCommandReader;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import static org.objectweb.asm.Opcodes.ASM9;

public interface MethodCommand extends Command {
    void write(MethodVisitor visitor);

    default List<MethodOperations> op(MethodOperations operations) {
        return new ArrayList<>();
    }

    default void updateFunction(XObject function, XGroup group) {
    }

    @Data
    @AllArgsConstructor
    class Parameter implements MethodCommand {
        String name;
        int access;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitParameter(name, access);
            log("visitor.visitParameter(name, access);", name, access);
        }
    }

    @Data
    @AllArgsConstructor
    class AnnotationDefault implements MethodCommand {
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotationDefault();
            log("AnnotationVisitor annotationVisitor = visitor.visitAnnotationDefault();");
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Annotation implements MethodCommand {
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotation(descriptor, visible);
            log("AnnotationVisitor annotationVisitor = visitor.visitAnnotation(descriptor, visible);", descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
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
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
            log("AnnotationVisitor annotationVisitor = visitor.visitTypeAnnotation(typeRef, typePath, descriptor, visible);", typeRef, typePath, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
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
            log("visitor.visitAnnotableParameterCount(parameterCount, visible);", parameterCount, visible);
        }
    }

    @Data
    @AllArgsConstructor
    class ParameterAnnotation implements MethodCommand {
        int parameter;
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitParameterAnnotation(parameter, descriptor, visible);
            log("AnnotationVisitor annotationVisitor = visitor.visitParameterAnnotation(parameter, descriptor, visible);", parameter, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
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
            log("visitor.visitAttribute(attribute);", attribute);
        }
    }

    @Data
    @AllArgsConstructor
    class Code implements MethodCommand {
        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitCode();
            log("visitor.visitCode();");
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
            log("visitor.visitFrame(type, numLocal, local, numStack, stack);", type, numLocal, local, numStack, stack);
        }
    }

    @Data
    @AllArgsConstructor
    class Insn implements MethodCommand {
        int opcode;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitInsn(opcode);
            log("visitor.visitInsn(opcode);", opcode);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode]);
            switch (opcode) {
                case Opcodes.NOP:
                    break;
                case Opcodes.ACONST_NULL:
                    operations.getRefStack().push(new MethodOperations.Ref(null, null, null));
                    break;
                case Opcodes.ICONST_M1:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.ICONST_0:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.ICONST_1:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.ICONST_2:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.ICONST_3:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.ICONST_4:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.ICONST_5:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.LCONST_0:
                    operations.ops(null, MethodOperations.LONG);
                    break;
                case Opcodes.LCONST_1:
                    operations.ops(null, MethodOperations.LONG);
                    break;
                case Opcodes.FCONST_0:
                    operations.ops(null, MethodOperations.FLOAT);
                    break;
                case Opcodes.FCONST_1:
                    operations.ops(null, MethodOperations.FLOAT);
                    break;
                case Opcodes.FCONST_2:
                    operations.ops(null, MethodOperations.FLOAT);
                    break;
                case Opcodes.DCONST_0:
                    operations.ops(null, MethodOperations.DOUBLE);
                    break;
                case Opcodes.DCONST_1:
                    operations.ops(null, MethodOperations.DOUBLE);
                    break;
                case Opcodes.IALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.LALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.FALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.DALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.AALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.BALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.CALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.SALOAD:
                    operations.getRefStack().pop();
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.getType(operations.getRefStack().pop().getDescriptor()).getElementType().getDescriptor(), null));
                    break;
                case Opcodes.IASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.LASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.FASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.DASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.AASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.BASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.CASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.SASTORE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.POP:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.POP2:
                    MethodOperations.Ref ref = operations.getRefStack().pop();
                    if (ref.category1()) {
                        operations.getRefStack().pop();
                    }
                    break;
                case Opcodes.DUP:
                    operations.getRefStack().push(operations.getRefStack().peek());
                    break;
                case Opcodes.DUP_X1: {
                    MethodOperations.Ref value1 = operations.getRefStack().pop();
                    MethodOperations.Ref value2 = operations.getRefStack().pop();
                    operations.getRefStack().push(value1);
                    operations.getRefStack().push(value2);
                    operations.getRefStack().push(value1);
                }
                break;
                case Opcodes.DUP_X2: {
                    MethodOperations.Ref value1 = operations.getRefStack().pop();
                    MethodOperations.Ref value2 = operations.getRefStack().pop();
                    if (value2.category2()) {
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                    } else {
                        MethodOperations.Ref value3 = operations.getRefStack().pop();
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value3);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                    }
                }
                break;
                case Opcodes.DUP2: {
                    MethodOperations.Ref value1 = operations.getRefStack().pop();
                    if (value1.category2()) {
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value1);
                    } else {
                        MethodOperations.Ref value2 = operations.getRefStack().pop();
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                    }
                }
                break;
                case Opcodes.DUP2_X1: {
                    MethodOperations.Ref value1 = operations.getRefStack().pop();
                    MethodOperations.Ref value2 = operations.getRefStack().pop();
                    if (value1.category2()) {
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                    } else {
                        MethodOperations.Ref value3 = operations.getRefStack().pop();
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value3);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                    }
                }
                break;
                case Opcodes.DUP2_X2: {
                    MethodOperations.Ref value1 = operations.getRefStack().pop();
                    MethodOperations.Ref value2 = operations.getRefStack().pop();
                    if (value1.category2() && value2.category2()) {
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                        break;
                    }
                    MethodOperations.Ref value3 = operations.getRefStack().pop();
                    if (value1.category1() && value2.category1() && value3.category2()) {
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value3);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                        break;
                    }
                    if (value1.category2() && value2.category1() && value3.category1()) {
                        operations.getRefStack().push(value1);
                        operations.getRefStack().push(value3);
                        operations.getRefStack().push(value2);
                        operations.getRefStack().push(value1);
                        break;
                    }
                    MethodOperations.Ref value4 = operations.getRefStack().pop();
                    operations.getRefStack().push(value2);
                    operations.getRefStack().push(value1);
                    operations.getRefStack().push(value4);
                    operations.getRefStack().push(value3);
                    operations.getRefStack().push(value2);
                    operations.getRefStack().push(value1);

                }
                break;
                case Opcodes.SWAP: {
                    MethodOperations.Ref value1 = operations.getRefStack().pop();
                    MethodOperations.Ref value2 = operations.getRefStack().pop();
                    operations.getRefStack().push(value1);
                    operations.getRefStack().push(value2);
                }
                break;
                case Opcodes.IADD:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LADD:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.FADD:
                    operations.ops("*,*", MethodOperations.FLOAT);
                    break;
                case Opcodes.DADD:
                    operations.ops("*,*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.ISUB:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LSUB:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.FSUB:
                    operations.ops("*,*", MethodOperations.FLOAT);
                    break;
                case Opcodes.DSUB:
                    operations.ops("*,*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.IMUL:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LMUL:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.FMUL:
                    operations.ops("*,*", MethodOperations.FLOAT);
                    break;
                case Opcodes.DMUL:
                    operations.ops("*,*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.IDIV:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LDIV:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.FDIV:
                    operations.ops("*,*", MethodOperations.FLOAT);
                    break;
                case Opcodes.DDIV:
                    operations.ops("*,*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.IREM:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LREM:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.FREM:
                    operations.ops("*,*", MethodOperations.FLOAT);
                    break;
                case Opcodes.DREM:
                    operations.ops("*,*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.INEG:
                    // noop
                    break;
                case Opcodes.LNEG:
                    // noop
                    break;
                case Opcodes.FNEG:
                    // noop
                    break;
                case Opcodes.DNEG:
                    // noop
                    break;
                case Opcodes.ISHL:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LSHL:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.ISHR:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LSHR:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.IUSHR:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LUSHR:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.IAND:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LAND:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.IOR:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LOR:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.IXOR:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.LXOR:
                    operations.ops("*,*", MethodOperations.LONG);
                    break;
                case Opcodes.I2L:
                    operations.ops("*", MethodOperations.LONG);
                    break;
                case Opcodes.I2F:
                    operations.ops("*", MethodOperations.FLOAT);
                    break;
                case Opcodes.I2D:
                    operations.ops("*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.L2I:
                    operations.ops("*", MethodOperations.INT);
                    break;
                case Opcodes.L2F:
                    operations.ops("*", MethodOperations.FLOAT);
                    break;
                case Opcodes.L2D:
                    operations.ops("*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.F2I:
                    operations.ops("*", MethodOperations.INT);
                    break;
                case Opcodes.F2L:
                    operations.ops("*", MethodOperations.LONG);
                    break;
                case Opcodes.F2D:
                    operations.ops("*", MethodOperations.DOUBLE);
                    break;
                case Opcodes.D2I:
                    operations.ops("*", MethodOperations.INT);
                    break;
                case Opcodes.D2L:
                    operations.ops("*", MethodOperations.LONG);
                    break;
                case Opcodes.D2F:
                    operations.ops("*", MethodOperations.FLOAT);
                    break;
                case Opcodes.I2B:
                    operations.ops("*", MethodOperations.BYTE);
                    break;
                case Opcodes.I2C:
                    operations.ops("*", MethodOperations.CHAR);
                    break;
                case Opcodes.I2S:
                    operations.ops("*", MethodOperations.SHORT);
                    break;
                case Opcodes.LCMP:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.FCMPL:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.FCMPG:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.DCMPL:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.DCMPG:
                    operations.ops("*,*", MethodOperations.INT);
                    break;
                case Opcodes.IRETURN:
                    operations.getRefStack().clear();
                    break;
                case Opcodes.LRETURN:
                    operations.getRefStack().clear();
                    break;
                case Opcodes.FRETURN:
                    operations.getRefStack().clear();
                    break;
                case Opcodes.DRETURN:
                    operations.getRefStack().clear();
                    break;
                case Opcodes.ARETURN:
                    operations.getRefStack().clear();
                    break;
                case Opcodes.RETURN:
                    operations.getRefStack().clear();
                    break;
                case Opcodes.ARRAYLENGTH:
                    operations.ops("*", MethodOperations.INT);
                    break;
                case Opcodes.ATHROW:
                    MethodOperations.Ref pop = operations.getRefStack().pop();
                    operations.getRefStack().clear();
                    operations.getRefStack().push(pop);
                    break;
                case Opcodes.MONITORENTER:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.MONITOREXIT:
                    operations.getRefStack().pop();
                    break;
                default:
                    break;
            }
            return new ArrayList<>();
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
            log("visitor.visitIntInsn(opcode, operand);", opcode, operand);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode]);
            switch (opcode) {
                case Opcodes.BIPUSH:
                    operations.getRefStack().push(new MethodOperations.Ref(null, Type.BYTE_TYPE.getDescriptor(), null));
                    break;
                case Opcodes.SIPUSH:
                    break;
                case Opcodes.NEWARRAY:
                    break;
                default:
                    break;
            }
            return new ArrayList<>();
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
            log("visitor.visitVarInsn(opcode, var);", opcode, var);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode] + "\tvar=" + var);
            switch (opcode) {
                case Opcodes.ILOAD:
                    operations.ops(null, MethodOperations.INT);
                    break;
                case Opcodes.LLOAD:
                    operations.ops(null, MethodOperations.LONG);
                    break;
                case Opcodes.FLOAD:
                    operations.ops(null, MethodOperations.FLOAT);
                    break;
                case Opcodes.DLOAD:
                    operations.ops(null, MethodOperations.DOUBLE);
                    break;
                case Opcodes.ALOAD:
                    boolean loaded = false;
                    if (operations.getRefStored().containsKey(var)) {
                        operations.getRefStack().push(operations.getRefStored().get(var));
                    } else {
                        for (LocalVariable localVariable : operations.getLocalVariableList()) {
                            if (localVariable.getIndex() == var) {
                                boolean start = false;
                                boolean end = false;
                                for (Label label : operations.getLabels()) {
                                    if (label.getLabel().toString().equals(localVariable.getStart().toString())) {
                                        start = true;
                                    }
                                    if (label.getLabel().toString().equals(localVariable.getEnd().toString())) {
                                        end = true;
                                    }
                                }
                                if (start && !end) {
                                    MethodOperations.Ref ref = new MethodOperations.Ref(localVariable.getName(), localVariable.getDescriptor(), localVariable.getSignature());
                                    MethodOperations.Operation operation = new MethodOperations.Operation();
                                    operation.setDescriptor("LOCAL_VARIABLE");
                                    operation.setName("LOCAL_VARIABLE");
                                    operation.setOwner("LOCAL_VARIABLE");
                                    ref.setCreatedFrom(operation);
                                    operations.getRefStack().push(ref);
                                    loaded = true;
                                    break;
                                }
                            }
                        }
                        if (!loaded) {
                            throw new IllegalStateException("invalid ALOAD opcode.");
                        }
                    }

                    break;
                case Opcodes.ISTORE:
                    operations.getRefStored().put(var, operations.getRefStack().pop());
                    break;
                case Opcodes.LSTORE:
                    operations.getRefStored().put(var, operations.getRefStack().pop());
                    break;
                case Opcodes.FSTORE:
                    operations.getRefStored().put(var, operations.getRefStack().pop());
                    break;
                case Opcodes.DSTORE:
                    operations.getRefStored().put(var, operations.getRefStack().pop());
                    break;
                case Opcodes.ASTORE:
                    operations.getRefStored().put(var, operations.getRefStack().pop());
                    break;
                case Opcodes.RET:
                    // NOOP
                    break;
                default:
                    break;
            }
            return new ArrayList<>();
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
            log("visitor.visitTypeInsn(opcode, type);", opcode, type);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode] + "\ttype=" + type);
            switch (opcode) {
                case Opcodes.NEW: {
                    MethodOperations.Ref ref = new MethodOperations.Ref(type);
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setOwner("NEW");
                    operation.setName("NEW");
                    operation.setDescriptor("NEW");
                    ref.setCreatedFrom(operation);
                    operations.getRefStack().push(ref);
                }
                break;
                case Opcodes.ANEWARRAY:
                    operations.getRefStack().pop();
                    MethodOperations.Ref ref = new MethodOperations.Ref("[" + type);
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setOwner("ANEWARRAY");
                    operation.setName("ANEWARRAY");
                    operation.setDescriptor("ANEWARRAY");
                    ref.setCreatedFrom(operation);
                    operations.getRefStack().push(ref);
                    break;
                case Opcodes.CHECKCAST:
                    // NOOP
                    break;
                case Opcodes.INSTANCEOF:
                    operations.ops("*", MethodOperations.INT);
                    break;
                default:
                    break;
            }
            return new ArrayList<>();
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
            log("visitor.visitFieldInsn(opcode, owner, name, descriptor);", opcode, owner, name, descriptor);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode] + "\towner=" + owner + " name=" + name + " descriptor=" + descriptor);
            switch (opcode) {
                case Opcodes.GETSTATIC: {
                    MethodOperations.Ref ref = new MethodOperations.Ref(owner, name, descriptor, null);
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setStatic(true);
                    operation.setOwner("STATIC");
                    operation.setName("STATIC");
                    operation.setDescriptor("STATIC");
                    ref.setCreatedFrom(operation);
                    operations.getRefStack().push(ref);
                }
                break;
                case Opcodes.PUTSTATIC:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.GETFIELD: {
                    operations.getRefStack().pop();
                    MethodOperations.Ref ref = new MethodOperations.Ref(owner, name, descriptor, null);
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setStatic(true);
                    operation.setOwner("FIELD");
                    operation.setName("FIELD");
                    operation.setDescriptor("FIELD");
                    ref.setCreatedFrom(operation);
                    operations.getRefStack().push(ref);
                }
                break;
                case Opcodes.PUTFIELD:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                default:
                    break;
            }
            return new ArrayList<>();
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetState(owner, name, descriptor));
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
            log("visitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);", opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode] + "\towner=" + owner + " name=" + name + " descriptor=" + descriptor);
            switch (opcode) {
                case Opcodes.INVOKEVIRTUAL: {
                    Type[] types = Type.getArgumentTypes(descriptor);
                    for (Type type : types) {
                        operations.getRefStack().pop();
                    }
                    MethodOperations.Ref objectRef = operations.getRefStack().pop();
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setRef(objectRef);
                    operation.setOwner(owner);
                    operation.setName(name);
                    operation.setDescriptor(descriptor);
                    operations.getOperationList().add(operation);

                    Type returnType = Type.getReturnType(descriptor);
                    if (!returnType.equals(Type.VOID_TYPE)) {
                        MethodOperations.Ref ref = new MethodOperations.Ref(returnType.getDescriptor());
                        ref.setCreatedFrom(operation);
                        operations.getRefStack().push(ref);
                    }
                }
                break;
                case Opcodes.INVOKESPECIAL: {
                    Type[] types = Type.getArgumentTypes(descriptor);
                    for (Type type : types) {
                        operations.getRefStack().pop();
                    }

                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setOwner(owner);
                    operation.setName(name);
                    operation.setDescriptor(descriptor);

                    if ("<init>".equals(name)) {
                        operations.getRefStack().pop();
                        MethodOperations.Ref ref = operations.getRefStack().peek();
                        operation.setRef(ref);
                        if (ref != null) {
                            ref.setCreatedFrom(operation);
                        }
                    } else {
                        Type returnType = Type.getReturnType(descriptor);
                        if (!returnType.equals(Type.VOID_TYPE)) {
                            MethodOperations.Ref objectRef = operations.getRefStack().pop();
                            operation.setRef(objectRef);
                            MethodOperations.Ref ref = new MethodOperations.Ref(returnType.getDescriptor());
                            ref.setCreatedFrom(operation);
                            operations.getRefStack().push(ref);
                        }
                    }
                    operations.getOperationList().add(operation);
                }
                break;
                case Opcodes.INVOKESTATIC: {
                    Type[] types = Type.getArgumentTypes(descriptor);
                    for (Type type : types) {
                        operations.getRefStack().pop();
                    }
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setOwner(owner);
                    operation.setName(name);
                    operation.setDescriptor(descriptor);

                    operation.setStatic(true);

                    Type returnType = Type.getReturnType(descriptor);
                    if (!returnType.equals(Type.VOID_TYPE)) {
                        MethodOperations.Ref ref = new MethodOperations.Ref(returnType.getDescriptor());
                        ref.setCreatedFrom(operation);
                        operations.getRefStack().push(ref);
                    }
                    operations.getOperationList().add(operation);
                }
                break;
                case Opcodes.INVOKEINTERFACE: {
                    Type[] types = Type.getArgumentTypes(descriptor);
                    for (Type type : types) {
                        operations.getRefStack().pop();
                    }

                    MethodOperations.Ref objectRef = operations.getRefStack().pop();
                    MethodOperations.Operation operation = new MethodOperations.Operation();
                    operation.setRef(objectRef);
                    operation.setOwner(owner);
                    operation.setName(name);
                    operation.setDescriptor(descriptor);
                    operations.getOperationList().add(operation);

                    Type returnType = Type.getReturnType(descriptor);
                    if (!returnType.equals(Type.VOID_TYPE)) {
                        MethodOperations.Ref ref = new MethodOperations.Ref(returnType.getDescriptor());
                        ref.setCreatedFrom(operation);
                        operations.getRefStack().push(ref);
                    }
                    operations.getOperationList().add(operation);
                }
                break;
                default:
                    break;
            }
            return new ArrayList<>();
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetFunction(owner, name, descriptor));
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
            log("visitor.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);", name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tDynamicInsn");
            Type[] types = Type.getArgumentTypes(descriptor);
            for (Type type : types) {
                operations.getRefStack().pop();
            }
            return new ArrayList<>();
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetState(function.getOwner(), name, descriptor));

            System.out.println("==== bootstrap method arguments ====");
            if (bootstrapMethodArguments != null) {
                for (Object bootstrapMethodArgument : bootstrapMethodArguments) {
                    System.out.println(bootstrapMethodArgument + "\t:\t" + bootstrapMethodArgument.getClass());
                    if(bootstrapMethodArgument instanceof Handle) {
                        Handle handle = (Handle) bootstrapMethodArgument;
                        group.addPair(function, group.createOrGetFunction(handle.getOwner(), handle.getName(), handle.getDesc()));
                    }
                }
            }
            System.out.println("---- bootstrap method arguments ----");
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
            log("visitor.visitJumpInsn(opcode, label);", opcode, label);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\t" + OpcodeDecode.CODES[opcode] + "\tlable=" + label);
            switch (opcode) {
                case Opcodes.IFEQ:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IFNE:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IFLT:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IFGE:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IFGT:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IFLE:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ICMPEQ:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ICMPNE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ICMPLT:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ICMPGE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ICMPGT:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ICMPLE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ACMPEQ:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IF_ACMPNE:
                    operations.getRefStack().pop();
                    operations.getRefStack().pop();
                    break;
                case Opcodes.GOTO:
                    // no op
                    break;
                case Opcodes.JSR:
                    // no op
                    break;
                case Opcodes.IFNULL:
                    operations.getRefStack().pop();
                    break;
                case Opcodes.IFNONNULL:
                    operations.getRefStack().pop();
                    break;
                default:
                    break;
            }
            List<MethodOperations> list = new ArrayList<>();
            MethodOperations clone = operations.clone();
            clone.setNextLabel(label.toString());
            list.add(clone);
            return list;
        }
    }

    @Data
    @AllArgsConstructor
    class Label implements MethodCommand {
        org.objectweb.asm.Label label;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLabel(label);
            log("visitor.visitLabel(label);", label);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tLabel=" + label.toString());
            operations.getLabels().add(this);
            return new ArrayList<>();
        }
    }

    @Data
    @AllArgsConstructor
    class LdcInsn implements MethodCommand {
        Object value;

        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitLdcInsn(value);
            log("visitor.visitLdcInsn(value);", value);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tLdc\tvalue=" + value + "\tclass=" + value.getClass());
            MethodOperations.Ref ref;
            if (value instanceof Type) {
                ref = new MethodOperations.Ref(Type.getType(Class.class).getDescriptor());
            } else {
                ref = new MethodOperations.Ref(String.valueOf(value));
            }
            MethodOperations.Operation operation = new MethodOperations.Operation();
            operation.setDescriptor("LDC");
            operation.setName("LDC");
            operation.setOwner("LDC");
            ref.setCreatedFrom(operation);
            operations.getRefStack().push(ref);
            return new ArrayList<>();
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
            log("visitor.visitIincInsn(var, increment);", var, increment);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tIinc");
            // NO OP
            return new ArrayList<>();
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
            log("visitor.visitTableSwitchInsn(min, max, dflt, labels);", min, max, dflt, labels);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tTableSwitch");
            operations.getRefStack().pop();
            return new ArrayList<>();
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
            log("visitor.visitLookupSwitchInsn(dflt, keys, labels);", dflt, keys, labels);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tLookupSwitch");
            operations.getRefStack().pop();
            return new ArrayList<>();
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
            log("visitor.visitMultiANewArrayInsn(descriptor, numDimensions);", descriptor, numDimensions);
        }

        @Override
        public List<MethodOperations> op(MethodOperations operations) {
            operations.printStack();
            System.out.println("opcode:\tMultiANewArray");
            for (int i = 0; i < numDimensions; i++) {
                operations.getRefStack().pop();
            }
            operations.getRefStack().push(new MethodOperations.Ref(MethodOperations.VOID));
            return new ArrayList<>();
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
        }
    }

    @Data
    @AllArgsConstructor
    class InsnAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
            log("AnnotationVisitor annotationVisitor = visitor.visitInsnAnnotation(typeRef, typePath, descriptor, visible);", typeRef, typePath, descriptor, visible);

            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
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
            log("visitor.visitTryCatchBlock(start, end, handler, type);", start, end, handler, type);
        }
    }

    @Data
    @AllArgsConstructor
    class TryCatchAnnotation implements MethodCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
            log("AnnotationVisitor annotationVisitor = visitor.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);", typeRef, typePath, descriptor, visible);

            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
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
            log("visitor.visitLocalVariable(name, descriptor, signature, start, end, index);", name, descriptor, signature, start, end, index);
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));

            new SignatureCommandReader(ASM9).updateFunctionAndState(signature, function, group);
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
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(MethodVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
            log("AnnotationVisitor annotationVisitor = visitor.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);", typeRef, typePath, start, end, index, descriptor, visible);

            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        @Override
        public void updateFunction(XObject function, XGroup group) {
            group.addPair(function, group.createOrGetType(descriptor));
            for (AnnotationCommand annotationCommand : annotation.getCommands()) {
                annotationCommand.updateFunction(function);
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
            log("visitor.visitLineNumber(line, start);", line, start);
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
            log("visitor.visitMaxs(maxStack, maxLocals);", maxStack, maxLocals);
        }
    }

    @Data
    @AllArgsConstructor
    class End implements MethodCommand {
        @Override
        public void write(MethodVisitor visitor) {
            visitor.visitEnd();
            log("visitor.visitEnd();");
        }
    }
}
