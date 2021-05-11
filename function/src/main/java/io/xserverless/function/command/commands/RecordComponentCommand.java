package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

public interface RecordComponentCommand extends Command {
    void write(RecordComponentVisitor visitor);

    @Data
    @AllArgsConstructor
    class Annotation implements RecordComponentCommand {
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(RecordComponentVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotation(descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements RecordComponentCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(RecordComponentVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Attribute implements RecordComponentCommand {
        org.objectweb.asm.Attribute attribute;

        @Override
        public void write(RecordComponentVisitor visitor) {
            visitor.visitAttribute(attribute);
        }
    }

    @Data
    @AllArgsConstructor
    class End implements RecordComponentCommand {

        @Override
        public void write(RecordComponentVisitor visitor) {
            visitor.visitEnd();
        }
    }
}
