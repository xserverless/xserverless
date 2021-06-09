package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.RecordComponentCommand;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

public class RecordComponentCommandReader extends RecordComponentVisitor {
    private final CommandGroup<RecordComponentCommand> commandGroup = new CommandGroup<>();

    public RecordComponentCommandReader(int api) {
        super(api);
    }

    public CommandGroup<RecordComponentCommand> getCommandList() {
        return commandGroup;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new RecordComponentCommand.Annotation(descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new RecordComponentCommand.TypeAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        commandGroup.add(new RecordComponentCommand.Attribute(attribute));
    }

    @Override
    public void visitEnd() {
        commandGroup.add(new RecordComponentCommand.End());
    }
}
