package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandList;
import io.xserverless.function.command.commands.ModuleCommand;
import org.objectweb.asm.ModuleVisitor;

public class ModuleCommandReader extends ModuleVisitor {
    private CommandList<ModuleCommand> commandList;

    public ModuleCommandReader(int api) {
        super(api);
        commandList = new CommandList<>();
    }

    public CommandList<ModuleCommand> getCommandList() {
        return commandList;
    }

    @Override
    public void visitMainClass(String mainClass) {
        commandList.add(new ModuleCommand.MainClass(mainClass));
    }

    @Override
    public void visitPackage(String packaze) {
        commandList.add(new ModuleCommand.Package(packaze));
    }

    @Override
    public void visitRequire(String module, int access, String version) {
        commandList.add(new ModuleCommand.Require(module, access, version));
    }

    @Override
    public void visitExport(String packaze, int access, String... modules) {
        commandList.add(new ModuleCommand.Export(packaze, access, modules));
    }

    @Override
    public void visitOpen(String packaze, int access, String... modules) {
        commandList.add(new ModuleCommand.Open(packaze, access, modules));
    }

    @Override
    public void visitUse(String service) {
        commandList.add(new ModuleCommand.Use(service));
    }

    @Override
    public void visitProvide(String service, String... providers) {
        commandList.add(new ModuleCommand.Provide(service, providers));
    }

    @Override
    public void visitEnd() {
        commandList.add(new ModuleCommand.End());
    }
}
