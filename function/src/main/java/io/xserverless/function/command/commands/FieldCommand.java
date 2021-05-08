package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.TypePath;

public interface FieldCommand extends Command {
    @Data
    @AllArgsConstructor
    class Annotation implements FieldCommand {
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements FieldCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class Attribute implements FieldCommand {
        org.objectweb.asm.Attribute attribute;
    }

    @Data
    @AllArgsConstructor
    class End implements FieldCommand {
    }
}
