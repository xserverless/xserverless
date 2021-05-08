package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.AnnotationCommand;
import org.objectweb.asm.AnnotationVisitor;

public class AnnotationCommandReader extends AnnotationVisitor {
    private CommandList<AnnotationCommand> commandList;

    public AnnotationCommandReader(int api) {
        super(api);
        commandList = new CommandList<>();
    }

    public CommandList<AnnotationCommand> getCommandList() {
        return commandList;
    }

    @Override
    public void visit(String name, Object value) {
        commandList.add(new AnnotationCommand.Default(name, value));
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        commandList.add(new AnnotationCommand.Enum(name, descriptor, value));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new AnnotationCommand.Annotation(name, descriptor, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationCommandReader annotationCommandReader = new AnnotationCommandReader(api);
        commandList.add(new AnnotationCommand.Array(name, annotationCommandReader.getCommandList()));
        return annotationCommandReader;
    }

    @Override
    public void visitEnd() {
        commandList.add(new AnnotationCommand.End());
    }
}
