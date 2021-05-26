package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;

public interface AnnotationCommand extends Command {
    void write(AnnotationVisitor visitor);

    default void updateFunction(XObject function) {
    }

    default void updateType(String type, XGroup group) {

    }

    @Data
    @AllArgsConstructor
    class Default implements AnnotationCommand {
        private String name;
        private Object value;

        @Override
        public void write(AnnotationVisitor visitor) {
            visitor.visit(name, value);
log("visitor.visit(name, value);", name, value);
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
log("visitor.visitEnum(name, descriptor, value);", name, descriptor, value);
        }
    }

    @Data
    @AllArgsConstructor
    class Annotation implements AnnotationCommand {
        private String name;
        private String descriptor;

        private CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(AnnotationVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitAnnotation(name, descriptor);
log("AnnotationVisitor annotationVisitor = visitor.visitAnnotation(name, descriptor);", name, descriptor);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        public void updateType(String owner, XGroup group) {
            group.addPair(group.createTypeByName(owner), group.createOrGetType(descriptor));
            for (AnnotationCommand command : annotation.getCommands()) {
                command.updateType(owner, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Array implements AnnotationCommand {
        private String name;
        private CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(AnnotationVisitor visitor) {
            AnnotationVisitor annotationVisitor = visitor.visitArray(name);
log("AnnotationVisitor annotationVisitor = visitor.visitArray(name);", name);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        public void updateType(String owner, XGroup group) {
            for (AnnotationCommand command : annotation.getCommands()) {
                command.updateType(owner, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class End implements AnnotationCommand {
        @Override
        public void write(AnnotationVisitor visitor) {
            visitor.visitEnd();
log("visitor.visitEnd();");
        }
    }
}
