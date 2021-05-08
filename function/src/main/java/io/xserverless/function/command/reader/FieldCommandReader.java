package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.FieldCommand;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

public class FieldCommandReader extends FieldVisitor {
    private CommandList<FieldCommand> commandList;

    public FieldCommandReader(int api) {
        super(api);
        commandList = new CommandList<>();
    }

    public CommandList<FieldCommand> getCommandList() {
        return commandList;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new FieldCommand.Annotation(descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new FieldCommand.TypeAnnotation(typeRef, typePath, descriptor, visible, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        commandList.add(new FieldCommand.Attribute(attribute));
    }

    @Override
    public void visitEnd() {
        commandList.add(new FieldCommand.End());
    }
}
