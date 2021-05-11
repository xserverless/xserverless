package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import io.xserverless.function.dto.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;

public interface AnnotationCommand extends Command {
    void write(AnnotationVisitor visitor);

    default void updateFunction(Function function) {
    }
    @Data
    @AllArgsConstructor
    class Default implements AnnotationCommand {
        private String name;
        private Object value;

        @Override
        public void write(AnnotationVisitor visitor) {
            visitor.visit(name, value);
        }
    }

    @Data
    @AllArgsConstructor
    class Enum implements AnnotationCommand {
        private String name;
        private String descriptor;
        private String value;

        @Override
        public void write(AnnotationVisitor visitor) {
            visitor.visitEnum(name, descriptor, value);
        }
    }

    @Data
    @AllArgsConstructor
    class Annotation implements AnnotationCommand {
        private String name;
        private String descriptor;

        private CommandList<AnnotationCommand> annotation;

        @Override
        public void write(AnnotationVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotation(name, descriptor);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Array implements AnnotationCommand {
        private String name;
        private CommandList<AnnotationCommand> annotation;

        @Override
        public void write(AnnotationVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitArray(name);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class End implements AnnotationCommand {
        @Override
        public void write(AnnotationVisitor visitor) {
            visitor.visitEnd();
        }
    }
}
