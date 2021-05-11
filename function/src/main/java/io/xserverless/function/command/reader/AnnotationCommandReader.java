package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.AnnotationCommand;
import org.objectweb.asm.AnnotationVisitor;

public class AnnotationCommandReader extends AnnotationVisitor {
    private CommandGroup<AnnotationCommand> commandGroup;

    public AnnotationCommandReader(int api) {
        super(api);
        commandGroup = new CommandGroup<>();
    }

    public CommandGroup<AnnotationCommand> getCommandList() {
        return commandGroup;
    }

    @Override
    public void visit(String name, Object value) {
        commandGroup.add(new AnnotationCommand.Default(name, value));
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        commandGroup.add(new AnnotationCommand.Enum(name, descriptor, value));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new AnnotationCommand.Annotation(name, descriptor, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandGroup.add(new AnnotationCommand.Array(name, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitEnd() {
        commandGroup.add(new AnnotationCommand.End());
    }
}
