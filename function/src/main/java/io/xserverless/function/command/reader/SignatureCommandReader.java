package io.xserverless.function.command.reader;

import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.command.commands.SignatureCommand;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

public class SignatureCommandReader extends SignatureVisitor {
    private final CommandGroup<SignatureCommand> commandGroup = new CommandGroup<>();

    public SignatureCommandReader(int api) {
        super(api);
    }

    public CommandGroup<SignatureCommand> getCommandGroup() {
        return commandGroup;
    }

    public void updateType(String signature, String owner, XGroup group) {
        if (signature != null) {
            new SignatureReader(signature).accept(this);
            CommandGroup<SignatureCommand> commandGroup = getCommandGroup();
            for (SignatureCommand command : commandGroup.getCommands()) {
                command.updateType(owner, group);
            }
        }
    }

    public void updateFunctionAndState(String signature, XObject function, XGroup group) {
        if (signature != null) {
            new SignatureReader(signature).accept(this);
            CommandGroup<SignatureCommand> commandGroup = getCommandGroup();
            for (SignatureCommand command : commandGroup.getCommands()) {
                command.updateFunctionAndState(function, group);
            }
        }
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        commandGroup.add(new SignatureCommand.FormalTypeParameter(name));
    }

    @Override
    public SignatureVisitor visitClassBound() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.ClassBound(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.InterfaceBound(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.Superclass(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public SignatureVisitor visitInterface() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.Interface(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.ParameterType(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.ReturnType(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.ExceptionType(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public void visitBaseType(char descriptor) {
        commandGroup.add(new SignatureCommand.BaseType(descriptor));
    }

    @Override
    public void visitTypeVariable(String name) {
        commandGroup.add(new SignatureCommand.TypeVariable(name));
    }

    @Override
    public SignatureVisitor visitArrayType() {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.ArrayType(signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public void visitClassType(String name) {
        commandGroup.add(new SignatureCommand.ClassType(name));
    }

    @Override
    public void visitInnerClassType(String name) {
        commandGroup.add(new SignatureCommand.InnerClassType(name));
    }

    @Override
    public void visitTypeArgument() {
        commandGroup.add(new SignatureCommand.TypeArgument());
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        SignatureCommandReader signatureCommandReader = new SignatureCommandReader(api);
        commandGroup.add(new SignatureCommand.TypeArgumentWildcard(wildcard, signatureCommandReader.getCommandGroup()));
        return signatureCommandReader;
    }

    @Override
    public void visitEnd() {
        commandGroup.add(new SignatureCommand.End());
    }
}
