package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.TypePath;

public interface RecordComponentCommand extends Command {
    @Data
    @AllArgsConstructor
    class Annotation implements RecordComponentCommand {
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements RecordComponentCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class Attribute implements RecordComponentCommand {
        org.objectweb.asm.Attribute attribute;
    }

    @Data
    @AllArgsConstructor
    class End implements RecordComponentCommand {
    }
}
