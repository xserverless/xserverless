package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface ModuleCommand extends Command {
    @Data
    @AllArgsConstructor
    class MainClass implements ModuleCommand {
        String mainClass;
    }

    @Data
    @AllArgsConstructor
    class Package implements ModuleCommand {
        String packaze;
    }

    @Data
    @AllArgsConstructor
    class Require implements ModuleCommand {
        String module;
        int access;
        String version;
    }

    @Data
    @AllArgsConstructor
    class Export implements ModuleCommand {
        String packaze;
        int access;
        String[] modules;
    }

    @Data
    @AllArgsConstructor
    class Open implements ModuleCommand {
        String packaze;
        int access;
        String[] modules;
    }

    @Data
    @AllArgsConstructor
    class Use implements ModuleCommand {
        String service;
    }

    @Data
    @AllArgsConstructor
    class Provide implements ModuleCommand {
        String service;
        String[] providers;
    }

    @Data
    @AllArgsConstructor
    class End implements ModuleCommand {
    }
}
