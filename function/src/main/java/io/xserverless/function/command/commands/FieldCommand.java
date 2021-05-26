package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

public interface FieldCommand extends Command {
    void write(FieldVisitor visitor);

    @Data
    @AllArgsConstructor
    class Annotation implements FieldCommand {
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(FieldVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotation(descriptor, visible);
log("AnnotationVisitor annotationVisitor = visitor.visitAnnotation(descriptor, visible);", descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements FieldCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(FieldVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
log("AnnotationVisitor annotationVisitor = visitor.visitTypeAnnotation(typeRef, typePath, descriptor, visible);", typeRef, typePath, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Attribute implements FieldCommand {
        org.objectweb.asm.Attribute attribute;

        @Override
        public void write(FieldVisitor visitor) {
            visitor.visitAttribute(attribute);
log("visitor.visitAttribute(attribute);", attribute);
        }
    }

    @Data
    @AllArgsConstructor
    class End implements FieldCommand {
        @Override
        public void write(FieldVisitor visitor) {
            visitor.visitEnd();
log("visitor.visitEnd();");
        }
    }
}
