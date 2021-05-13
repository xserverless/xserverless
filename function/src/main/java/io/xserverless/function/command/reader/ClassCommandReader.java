package io.xserverless.function.command.reader;

import java.io.IOException;
import java.io.InputStream;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ClassCommand;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

public class ClassCommandReader extends ClassVisitor {
    private final CommandGroup<ClassCommand> commandGroup = new CommandGroup<>();

    public ClassCommandReader(int api) {
        super(api);
    }

    public static CommandGroup<ClassCommand> read(InputStream inputStream, int api) {
        ClassCommandReader classCommandReader = new ClassCommandReader(api);
        try {
            ClassReader classReader = new ClassReader(inputStream);
            classReader.accept(classCommandReader, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classCommandReader.getCommandList();
    }

    public CommandGroup<ClassCommand> getCommandList() {
        return commandGroup;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        commandGroup.setName(name);
        commandGroup.add(new ClassCommand.Default(version, access, name, signature, superName, interfaces));
    }

    @Override
    public void visitSource(String source, String debug) {
        commandGroup.add(new ClassCommand.Source(source, debug));
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        ModuleCommandReader moduleCommandReader = new ModuleCommandReader(api);
        commandGroup.add(new ClassCommand.Module(name, access, version, moduleCommandReader.getCommandList()));
        return moduleCommandReader;
    }

    @Override
    public void visitNestHost(String nestHost) {
        commandGroup.add(new ClassCommand.NestHost(nestHost));
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        commandGroup.add(new ClassCommand.OuterClass(owner, name, descriptor));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new ClassCommand.Annotation(descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new ClassCommand.TypeAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        commandGroup.add(new ClassCommand.Attribute(attribute));
    }

    @Override
    public void visitNestMember(String nestMember) {
        commandGroup.add(new ClassCommand.NestMember(nestMember));
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        commandGroup.add(new ClassCommand.PermittedSubclass(permittedSubclass));
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        commandGroup.add(new ClassCommand.InnerClass(name, outerName, innerName, access));
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        RecordComponentCommandReader recordComponentCommandReader = new RecordComponentCommandReader(api);
        commandGroup.add(new ClassCommand.RecordComponent(name, descriptor, signature, recordComponentCommandReader.getCommandList()));
        return recordComponentCommandReader;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldCommandReader fieldCommandReader = new FieldCommandReader(api);
        fieldCommandReader.getCommandList().setName(name);
        fieldCommandReader.getCommandList().setOwner(commandGroup.getName());
        fieldCommandReader.getCommandList().setDescriptor(descriptor);
        commandGroup.add(new ClassCommand.Field(access, name, descriptor, signature, value, fieldCommandReader.getCommandList()));
        return fieldCommandReader;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodCommandReader methodCommandReader = new MethodCommandReader(api);
        methodCommandReader.getCommandList().setName(name);
        methodCommandReader.getCommandList().setOwner(commandGroup.getName());
        methodCommandReader.getCommandList().setDescriptor(descriptor);
        commandGroup.add(new ClassCommand.Method(access, name, descriptor, signature, exceptions, methodCommandReader.getCommandList()));
        return methodCommandReader;
    }

    @Override
    public void visitEnd() {
        commandGroup.add(new ClassCommand.End());
    }
}
