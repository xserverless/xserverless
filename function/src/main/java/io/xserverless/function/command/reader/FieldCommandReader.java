package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.FieldCommand;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

public class FieldCommandReader extends FieldVisitor {
    private final CommandGroup<FieldCommand> commandGroup = new CommandGroup<>();

    public FieldCommandReader(int api) {
        super(api);
    }

    public CommandGroup<FieldCommand> getCommandList() {
        return commandGroup;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new FieldCommand.Annotation(descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new FieldCommand.TypeAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        commandGroup.add(new FieldCommand.Attribute(attribute));
    }

    @Override
    public void visitEnd() {
        commandGroup.add(new FieldCommand.End());
    }
}
