package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.MethodCommand;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

public class MethodCommandReader extends MethodVisitor {
    private CommandList<MethodCommand> commandList;

    public MethodCommandReader(int api) {
        super(api);
        commandList = new CommandList<>();
    }

    public CommandList<MethodCommand> getCommandList() {
        return commandList;
    }

    @Override
    public void visitParameter(String name, int access) {
        commandList.add(new MethodCommand.Parameter(name, access));
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.AnnotationDefault(annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.Annotation(descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.TypeAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        commandList.add(new MethodCommand.AnnotableParameterCount(parameterCount, visible));
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.ParameterAnnotation(parameter, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        commandList.add(new MethodCommand.Attribute(attribute));
    }

    @Override
    public void visitCode() {
        commandList.add(new MethodCommand.Code());
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        commandList.add(new MethodCommand.Frame(type, numLocal, local, numStack, stack));
    }

    @Override
    public void visitInsn(int opcode) {
        commandList.add(new MethodCommand.Insn(opcode));
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        commandList.add(new MethodCommand.IntInsn(opcode, operand));

    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        commandList.add(new MethodCommand.VarInsn(opcode, var));

    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        commandList.add(new MethodCommand.TypeInsn(opcode, type));
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        commandList.add(new MethodCommand.FieldInsn(opcode, owner, name, descriptor));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        commandList.add(new MethodCommand.MethodInsn(opcode, owner, name, descriptor, false));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        commandList.add(new MethodCommand.MethodInsn(opcode, owner, name, descriptor, isInterface));

    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        commandList.add(new MethodCommand.InvokeDynamicInsn(name, descriptor, bootstrapMethodHandle,
                bootstrapMethodArguments));
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        commandList.add(new MethodCommand.JumpInsn(opcode, label));
    }

    @Override
    public void visitLabel(Label label) {
        commandList.add(new MethodCommand.Label(label));
    }

    @Override
    public void visitLdcInsn(Object value) {
        commandList.add(new MethodCommand.LdcInsn(value));
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        commandList.add(new MethodCommand.IincInsn(var, increment));
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        commandList.add(new MethodCommand.TableSwitchInsn(min, max, dflt, labels));
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        commandList.add(new MethodCommand.LookupSwitchInsn(dflt, keys, labels));
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        commandList.add(new MethodCommand.MultiANewArrayInsn(descriptor, numDimensions));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.InsnAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        commandList.add(new MethodCommand.TryCatchBlock(start, end, handler, type));
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.TryCatchAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        commandList.add(new MethodCommand.LocalVariable(name, descriptor, signature, start, end, index));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new MethodCommand.LocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        commandList.add(new MethodCommand.LineNumber(line, start));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        commandList.add(new MethodCommand.Maxs(maxStack, maxLocals));
    }

    @Override
    public void visitEnd() {
        commandList.add(new MethodCommand.End());
    }
}
