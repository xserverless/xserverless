package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.TypePath;

public interface ClassCommand extends Command {

    @Data
    @AllArgsConstructor
    class Default implements ClassCommand {
        int version;
        int access;
        String name;
        String signature;
        String superName;
        String[] interfaces;
    }

    @Data
    @AllArgsConstructor
    class Source implements ClassCommand {
        String source;
        String debug;
    }

    @Data
    @AllArgsConstructor
    class Module implements ClassCommand {
        String name;
        int access;
        String version;
        CommandList<ModuleCommand> module;
    }

    @Data
    @AllArgsConstructor
    class NestHost implements ClassCommand {
        String nestHost;
    }

    @Data
    @AllArgsConstructor
    class OuterClass implements ClassCommand {
        String owner;
        String name;
        String descriptor;
    }

    @Data
    @AllArgsConstructor
    class Annotation implements ClassCommand {
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements ClassCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;
    }

    @Data
    @AllArgsConstructor
    class Attribute implements ClassCommand {
        org.objectweb.asm.Attribute attribute;
    }

    @Data
    @AllArgsConstructor
    class NestMember implements ClassCommand {
        String nestMember;
    }

    @Data
    @AllArgsConstructor
    class PermittedSubclass implements ClassCommand {
        String permittedSubclass;
    }

    @Data
    @AllArgsConstructor
    class InnerClass implements ClassCommand {
        String name;
        String outerName;
        String innerName;
        int access;
    }

    @Data
    @AllArgsConstructor
    class RecordComponent implements ClassCommand {
        String name;
        String descriptor;
        String signature;
        CommandList<RecordComponentCommand> recordComponent;
    }

    @Data
    @AllArgsConstructor
    class Field implements ClassCommand {
        int access;
        String name;
        String descriptor;
        String signature;
        Object value;
        CommandList<FieldCommand> field;
    }

    @Data
    @AllArgsConstructor
    class Method implements ClassCommand {
        int access;
        String name;
        String descriptor;
        String signature;
        String[] exceptions;
        CommandList<MethodCommand> method;
    }

    @Data
    @AllArgsConstructor
    class End implements ClassCommand {
    }
}
