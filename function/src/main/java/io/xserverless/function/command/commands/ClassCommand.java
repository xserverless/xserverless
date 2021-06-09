package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.reader.SignatureCommandReader;
import io.xserverless.function.command.writer.CommandFilter;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import static org.objectweb.asm.Opcodes.ASM9;

public interface ClassCommand extends Command {

    void write(ClassWriter classWriter, CommandFilter filter);

    default void updateType(String owner, XGroup group) {
    }

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
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visit(version, access, name, signature, superName, interfaces);
            log("classWriter.visit(version, access, name, signature, superName, interfaces);", version, access, name, signature, superName, interfaces);
        }

        public void updateType(String owner, XGroup group) {
            if (superName != null) {
                group.addPair(group.createTypeByName(owner), group.createTypeByName(superName));
            }

            if (interfaces != null) {
                for (String s : interfaces) {
                    group.addPair(group.createTypeByName(owner), group.createTypeByName(s));
                }
            }

            new SignatureCommandReader(ASM9).updateType(signature, owner, group);
        }
    }

    @Data
    @AllArgsConstructor
    class Source implements ClassCommand {
        String source;
        String debug;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitSource(source, debug);
            log("classWriter.visitSource(source, debug);", source, debug);
        }
    }

    @Data
    @AllArgsConstructor
    class Module implements ClassCommand {
        String name;
        int access;
        String version;
        CommandGroup<ModuleCommand> module;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            ModuleVisitor moduleVisitor = classWriter.visitModule(name, access, version);
            log("ModuleVisitor moduleVisitor = classWriter.visitModule(name, access, version);", name, access, version);
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
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitNestHost(nestHost);
            log("classWriter.visitNestHost(nestHost);", nestHost);
        }
    }

    @Data
    @AllArgsConstructor
    class OuterClass implements ClassCommand {
        String owner;
        String name;
        String descriptor;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitOuterClass(owner, name, descriptor);
            log("classWriter.visitOuterClass(owner, name, descriptor);", owner, name, descriptor);
        }
    }

    @Data
    @AllArgsConstructor
    class Annotation implements ClassCommand {
        String descriptor;
        boolean visible;
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            AnnotationVisitor annotationVisitor = classWriter.visitAnnotation(descriptor, visible);
            log("AnnotationVisitor annotationVisitor = classWriter.visitAnnotation(descriptor, visible);", descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        public void updateType(String owner, XGroup group) {
            group.addPair(group.createTypeByName(owner), group.createType(descriptor));
            for (AnnotationCommand command : annotation.getCommands()) {
                command.updateType(owner, group);
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
        CommandGroup<AnnotationCommand> annotation;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            AnnotationVisitor annotationVisitor = classWriter.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
            log("AnnotationVisitor annotationVisitor = classWriter.visitTypeAnnotation(typeRef, typePath, descriptor, visible);", typeRef, typePath, descriptor, visible);
            for (AnnotationCommand command : annotation.getCommands()) {
                command.write(annotationVisitor);
            }
        }

        public void updateType(String owner, XGroup group) {
            group.addPair(group.createTypeByName(owner), group.createType(descriptor));
            for (AnnotationCommand command : annotation.getCommands()) {
                command.updateType(owner, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Attribute implements ClassCommand {
        org.objectweb.asm.Attribute attribute;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitAttribute(attribute);
            log("classWriter.visitAttribute(attribute);", attribute);
        }
    }

    @Data
    @AllArgsConstructor
    class NestMember implements ClassCommand {
        String nestMember;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitNestMember(nestMember);
            log("classWriter.visitNestMember(nestMember);", nestMember);
        }
    }

    @Data
    @AllArgsConstructor
    class PermittedSubclass implements ClassCommand {
        String permittedSubclass;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitPermittedSubclass(permittedSubclass);
            log("classWriter.visitPermittedSubclass(permittedSubclass);", permittedSubclass);
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
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitInnerClass(name, outerName, innerName, access);
            log("classWriter.visitInnerClass(name, outerName, innerName, access);", name, outerName, innerName, access);
        }
    }

    @Data
    @AllArgsConstructor
    class RecordComponent implements ClassCommand {
        String name;
        String descriptor;
        String signature;
        CommandGroup<RecordComponentCommand> recordComponent;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            RecordComponentVisitor recordComponentVisitor = classWriter.visitRecordComponent(name, descriptor, signature);
            log("RecordComponentVisitor recordComponentVisitor = classWriter.visitRecordComponent(name, descriptor, signature);", name, descriptor, signature);
            for (RecordComponentCommand command : recordComponent.getCommands()) {
                command.write(recordComponentVisitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            new SignatureCommandReader(ASM9).updateFunctionAndState(signature, group.createState(owner, name, descriptor), group);
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
        CommandGroup<FieldCommand> field;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            if (filter.state(field.getOwner(), field.getName(), field.getDescriptor())) {
                FieldVisitor fieldVisitor = classWriter.visitField(access, name, descriptor, signature, value);
                log("FieldVisitor fieldVisitor = classWriter.visitField(access, name, descriptor, signature, value);", access, name, descriptor, signature, value);
                for (FieldCommand fieldCommand : field.getCommands()) {
                    fieldCommand.write(fieldVisitor);
                }
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            new SignatureCommandReader(ASM9).updateFunctionAndState(signature, group.createState(owner, name, descriptor), group);
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
        CommandGroup<MethodCommand> method;

        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            if (filter.function(method.getOwner(), method.getName(), method.getDescriptor())) {
                MethodVisitor methodVisitor = classWriter.visitMethod(access, name, descriptor, signature, exceptions);
                log("MethodVisitor methodVisitor = classWriter.visitMethod(access, name, descriptor, signature, exceptions);", access, name, descriptor, signature, exceptions);
                for (MethodCommand methodCommand : method.getCommands()) {
                    methodCommand.write(methodVisitor);
                }
            }
        }

        public void updateType(String owner, XGroup group) {
            XObject function = group.createFunction(owner, name, descriptor);
            group.putMethodCommandGroup(owner, name, descriptor, method);

            Type returnType = Type.getReturnType(descriptor);
            group.addPair(function, group.createType(returnType.getDescriptor()));

            Type[] argumentTypes = Type.getArgumentTypes(descriptor);
            for (Type argumentType : argumentTypes) {
                group.addPair(function, group.createType(argumentType.getDescriptor()));
            }

            for (MethodCommand methodCommand : method.getCommands()) {
                methodCommand.updateFunction(function, group);
            }

            new SignatureCommandReader(ASM9).updateFunctionAndState(signature, function, group);
        }
    }

    @Data
    @AllArgsConstructor
    class End implements ClassCommand {
        @Override
        public void write(ClassWriter classWriter, CommandFilter filter) {
            classWriter.visitEnd();
            log("classWriter.visitEnd();");
        }
    }
}
