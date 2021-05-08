package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface AnnotationCommand extends Command {
    @Data
    @AllArgsConstructor
    class Default implements AnnotationCommand {
        private String name;
        private Object value;
    }

    @Data
    @AllArgsConstructor
    class Enum implements AnnotationCommand {
        private String name;
        private String descriptor;
        private String value;
    }

    @Data
    @AllArgsConstructor
    class Annotation implements AnnotationCommand {
        private String name;
        private String descriptor;

        private CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class Array implements AnnotationCommand {
        private String name;
        private CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class End implements AnnotationCommand {
    }
}
