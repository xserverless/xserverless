package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.ModuleVisitor;

public interface ModuleCommand extends Command {
    void write(ModuleVisitor visitor);

    @Data
    @AllArgsConstructor
    class MainClass implements ModuleCommand {
        String mainClass;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitMainClass(mainClass);
            log("visitor.visitMainClass(mainClass);", mainClass);
        }
    }

    @Data
    @AllArgsConstructor
    class Package implements ModuleCommand {
        String packaze;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitPackage(packaze);
            log("visitor.visitPackage(packaze);", packaze);
        }
    }

    @Data
    @AllArgsConstructor
    class Require implements ModuleCommand {
        String module;
        int access;
        String version;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitRequire(module, access, version);
            log("visitor.visitRequire(module, access, version);", module, access, version);
        }
    }

    @Data
    @AllArgsConstructor
    class Export implements ModuleCommand {
        String packaze;
        int access;
        String[] modules;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitExport(packaze, access, modules);
            log("visitor.visitExport(packaze, access, modules);", packaze, access, modules);
        }
    }

    @Data
    @AllArgsConstructor
    class Open implements ModuleCommand {
        String packaze;
        int access;
        String[] modules;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitOpen(packaze, access, modules);
            log("visitor.visitOpen(packaze, access, modules);", packaze, access, modules);
        }
    }

    @Data
    @AllArgsConstructor
    class Use implements ModuleCommand {
        String service;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitUse(service);
            log("visitor.visitUse(service);", service);
        }
    }

    @Data
    @AllArgsConstructor
    class Provide implements ModuleCommand {
        String service;
        String[] providers;

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitProvide(service, providers);
            log("visitor.visitProvide(service, providers);", service, providers);
        }
    }

    @Data
    @AllArgsConstructor
    class End implements ModuleCommand {

        @Override
        public void write(ModuleVisitor visitor) {
            visitor.visitEnd();
            log("visitor.visitEnd();");
        }
    }
}
