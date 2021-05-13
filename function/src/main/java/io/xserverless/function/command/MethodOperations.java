package io.xserverless.function.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import io.xserverless.function.command.commands.MethodCommand;
import lombok.Data;
import lombok.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

@Getter
public class MethodOperations {
    @Data
    public static class Ref {
        String owner;
        String name;
        String descriptor;
        String signature;
        Operation createdFrom;

        public Ref(String descriptor) {
            this.descriptor = descriptor;
        }

        public Ref(String owner, String name, String descriptor, String signature) {
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
        }

        public Ref(String name, String descriptor, String signature) {
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
        }

        public boolean category1() {
            return !category2();
        }

        public boolean category2() {
            return LONG.equals(descriptor) || DOUBLE.equals(descriptor);
        }
    }

    @Data
    public static class Operation {
        private Ref ref;
        private String name;
        private String owner;
        private String descriptor;
        private boolean isStatic;
    }

    public static final String VOID = Type.VOID_TYPE.getDescriptor();
    public static final String BOOLEAN = Type.BOOLEAN_TYPE.getDescriptor();
    public static final String CHAR = Type.CHAR_TYPE.getDescriptor();
    public static final String BYTE = Type.BYTE_TYPE.getDescriptor();
    public static final String SHORT = Type.SHORT_TYPE.getDescriptor();
    public static final String INT = Type.INT_TYPE.getDescriptor();
    public static final String FLOAT = Type.FLOAT_TYPE.getDescriptor();
    public static final String LONG = Type.LONG_TYPE.getDescriptor();
    public static final String DOUBLE = Type.DOUBLE_TYPE.getDescriptor();

    private final List<Operation> operationList = new ArrayList<>();
    private final LinkedList<Ref> refStack = new LinkedList<>();
    private final List<MethodCommand.LocalVariable> localVariableList = new ArrayList<>();
    private final List<MethodCommand.Label> labels = new ArrayList<>();
    private final Map<Integer, Ref> refStored = new HashMap<>();
    private String nextLabel;

    public MethodOperations clone() {
        MethodOperations methodOperations = new MethodOperations();
        methodOperations.operationList.addAll(operationList);
        methodOperations.refStack.addAll(refStack);
        methodOperations.localVariableList.addAll(localVariableList);
        methodOperations.labels.addAll(labels);
        methodOperations.refStored.putAll(refStored);
        return methodOperations;
    }

    public void ops(String input, String output) {
        if (input != null && input.length() > 0) {
            for (String s : input.split(",")) {
                refStack.pop();
            }
        }
        for (String s : output.split(",")) {
            refStack.push(new Ref(s));
        }
    }

    public static void operations(CommandGroup<MethodCommand> commandGroup) {
        MethodOperations methodOperations = new MethodOperations();
        for (MethodCommand command : commandGroup.getCommands()) {
            if (command instanceof MethodCommand.LocalVariable) {
                methodOperations.localVariableList.add((MethodCommand.LocalVariable) command);
            }
        }

        LinkedList<MethodOperations> operations = new LinkedList<>();
        operations.add(methodOperations);

        while (!operations.isEmpty()) {
            MethodOperations op = operations.pop();
            operations.addAll(operations(op, commandGroup));

            System.out.println("============================");
            System.out.println("operations:");
            for (Operation operation : op.operationList) {
                Ref ref = operation.getRef();
                StringBuilder stringBuilder = new StringBuilder();
                if (operation.isStatic) {
                    stringBuilder.append("static\t");
                }

                if (ref != null) {
                    stringBuilder.append(ref.getDescriptor()).append(".").append(operation.getOwner()).append(".").append(operation.getName()).append(".").append(operation.getDescriptor()).append("\t<--\t");
                    if (ref.getCreatedFrom() != null) {
                        stringBuilder.append(ref.getCreatedFrom().getName()).append(".").append(ref.getCreatedFrom().getDescriptor());
                    } else {
                        stringBuilder.append("null");
                    }
                } else {
                    stringBuilder.append("N/A.").append(operation.getOwner()).append(".").append(operation.getName()).append(operation.getDescriptor());
                }
                System.out.println(stringBuilder);
            }
            System.out.println("============================");

            System.out.println();
        }
    }

    private static List<MethodOperations> operations(MethodOperations methodOperations, CommandGroup<MethodCommand> commandGroup) {
        for (MethodCommand.Label operationsLabel : methodOperations.getLabels()) {
            if (operationsLabel.getLabel().toString().equals(methodOperations.nextLabel)) {
                return new ArrayList<>();
            }
        }

        List<MethodOperations> list = new ArrayList<>();

        boolean start = false;
        loop:
        for (MethodCommand command : commandGroup.getCommands()) {
            if (!start) {
                if (methodOperations.nextLabel == null) {
                    start = true;
                } else if (command instanceof MethodCommand.Label && ((MethodCommand.Label) command).getLabel().toString().equals(methodOperations.nextLabel)) {
                    start = true;
                } else {
                    continue;
                }
            }
            list.addAll(command.op(methodOperations));
            if (command instanceof MethodCommand.Insn) {
                switch (((MethodCommand.Insn) command).getOpcode()) {
                    case Opcodes.IRETURN:
                        break loop;
                    case Opcodes.LRETURN:
                        break loop;
                    case Opcodes.FRETURN:
                        break loop;
                    case Opcodes.DRETURN:
                        break loop;
                    case Opcodes.ARETURN:
                        break loop;
                    case Opcodes.RETURN:
                        break loop;
                    case Opcodes.ATHROW:
                        break loop;
                    default:
                        break;
                }
            }
            if (command instanceof MethodCommand.JumpInsn) {
                switch (((MethodCommand.JumpInsn) command).getOpcode()) {
                    case Opcodes.GOTO:
                        break loop;
                    default:
                        break;
                }
            }
        }

        return list;
    }

    public void setNextLabel(String nextLabel) {
        this.nextLabel = nextLabel;
    }

    public void printStack() {
        StringJoiner stringJoiner = new StringJoiner(",");
        List<Ref> list = new ArrayList<>(refStack);
        Collections.reverse(list);
        for (Ref ref : list) {
            if (ref.getCreatedFrom() != null) {
                stringJoiner.add(ref.getDescriptor() + "(" + ref.getCreatedFrom().getName() + ")");
            } else {
                stringJoiner.add(ref.getDescriptor() + "(null)");
            }
        }
        System.out.println("STACK:\t" + stringJoiner);
    }
}
