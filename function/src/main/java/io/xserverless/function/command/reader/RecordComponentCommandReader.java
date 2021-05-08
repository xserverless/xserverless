package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.RecordComponentCommand;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

public class RecordComponentCommandReader extends RecordComponentVisitor {
    private CommandList<RecordComponentCommand> commandList;

    public RecordComponentCommandReader(int api) {
        super(api);
        commandList = new CommandList<>();
    }

    public CommandList<RecordComponentCommand> getCommandList() {
        return commandList;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new RecordComponentCommand.Annotation(descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new RecordComponentCommand.TypeAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        commandList.add(new RecordComponentCommand.Attribute(attribute));
    }

    @Override
    public void visitEnd() {
        commandList.add(new RecordComponentCommand.End());
    }
}
