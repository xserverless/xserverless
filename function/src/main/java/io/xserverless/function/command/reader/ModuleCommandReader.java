package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.ModuleCommand;
import org.objectweb.asm.ModuleVisitor;

public class ModuleCommandReader extends ModuleVisitor {
    private final CommandGroup<ModuleCommand> commandGroup = new CommandGroup<>();

    public ModuleCommandReader(int api) {
        super(api);
    }

    public CommandGroup<ModuleCommand> getCommandList() {
        return commandGroup;
    }

    @Override
    public void visitMainClass(String mainClass) {
        commandGroup.add(new ModuleCommand.MainClass(mainClass));
    }

    @Override
    public void visitPackage(String packaze) {
        commandGroup.add(new ModuleCommand.Package(packaze));
    }

    @Override
    public void visitRequire(String module, int access, String version) {
        commandGroup.add(new ModuleCommand.Require(module, access, version));
    }

    @Override
    public void visitExport(String packaze, int access, String... modules) {
        commandGroup.add(new ModuleCommand.Export(packaze, access, modules));
    }

    @Override
    public void visitOpen(String packaze, int access, String... modules) {
        commandGroup.add(new ModuleCommand.Open(packaze, access, modules));
    }

    @Override
    public void visitUse(String service) {
        commandGroup.add(new ModuleCommand.Use(service));
    }

    @Override
    public void visitProvide(String service, String... providers) {
        commandGroup.add(new ModuleCommand.Provide(service, providers));
    }

    @Override
    public void visitEnd() {
        commandGroup.add(new ModuleCommand.End());
    }
}
