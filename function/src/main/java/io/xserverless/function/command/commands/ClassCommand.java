package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;

public interface ClassCommand extends Command {

    void write(ClassWriter classWriter);

    @Data
    @AllArgsConstructor
    class Default implements ClassCommand {
        int version;
        int access;
        String name;
        String signature;
        String superName;
        String[] interfaces;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visit(version, access, name, signature, superName, interfaces);
        }
    }

    @Data
    @AllArgsConstructor
    class Source implements ClassCommand {
        String source;
        String debug;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitSource(source, debug);
        }
    }

    @Data
    @AllArgsConstructor
    class Module implements ClassCommand {
        String name;
        int access;
        String version;
        CommandList<ModuleCommand> module;

        @Override
        public void write(ClassWriter classWriter) {
            ModuleVisitor moduleVisitor = classWriter.visitModule(name, access, version);
            for (ModuleCommand command : module.getCommands()) {
                command.write(moduleVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class NestHost implements ClassCommand {
        String nestHost;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitNestHost(nestHost);
        }
    }

    @Data
    @AllArgsConstructor
    class OuterClass implements ClassCommand {
        String owner;
        String name;
        String descriptor;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitOuterClass(owner, name, descriptor);
        }
    }

    @Data
    @AllArgsConstructor
    class Annotation implements ClassCommand {
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(ClassWriter classWriter) {
            AnnotationVisitor annotationVisitor = classWriter.visitAnnotation(descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class TypeAnnotation implements ClassCommand {
        int typeRef;
        TypePath typePath;
        String descriptor;
        boolean visible;
        CommandList<AnnotationCommand> annotation;

        @Override
        public void write(ClassWriter classWriter) {
            AnnotationVisitor annotationVisitor = classWriter.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Attribute implements ClassCommand {
        org.objectweb.asm.Attribute attribute;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitAttribute(attribute);
        }
    }

    @Data
    @AllArgsConstructor
    class NestMember implements ClassCommand {
        String nestMember;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitNestMember(nestMember);
        }
    }

    @Data
    @AllArgsConstructor
    class PermittedSubclass implements ClassCommand {
        String permittedSubclass;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitPermittedSubclass(permittedSubclass);
        }
    }

    @Data
    @AllArgsConstructor
    class InnerClass implements ClassCommand {
        String name;
        String outerName;
        String innerName;
        int access;

        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitInnerClass(name, outerName, innerName, access);
        }
    }

    @Data
    @AllArgsConstructor
    class RecordComponent implements ClassCommand {
        String name;
        String descriptor;
        String signature;
        CommandList<RecordComponentCommand> recordComponent;

        @Override
        public void write(ClassWriter classWriter) {
            RecordComponentVisitor recordComponentVisitor = classWriter.visitRecordComponent(name, descriptor, signature);
            for (RecordComponentCommand command : recordComponent.getCommands()) {
                command.write(recordComponentVisitor);
            }
        }
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

        @Override
        public void write(ClassWriter classWriter) {
            FieldVisitor fieldVisitor = classWriter.visitField(access, name, descriptor, signature, value);
            for (FieldCommand fieldCommand : field.getCommands()) {
                fieldCommand.write(fieldVisitor);
            }
        }
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

        @Override
        public void write(ClassWriter classWriter) {
            MethodVisitor methodVisitor = classWriter.visitMethod(access, name, descriptor, signature, exceptions);
            for (MethodCommand methodCommand : method.getCommands()) {
                methodCommand.write(methodVisitor);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class End implements ClassCommand {
        @Override
        public void write(ClassWriter classWriter) {
            classWriter.visitEnd();
        }
    }
}
