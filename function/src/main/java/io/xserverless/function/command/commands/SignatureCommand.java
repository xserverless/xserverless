package io.xserverless.function.command.commands;

import io.xserverless.function.command.Command;
import io.xserverless.function.command.CommandGroup;
import io.xserverless.function.dto.XGroup;
import io.xserverless.function.dto.XObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objectweb.asm.signature.SignatureVisitor;

public interface SignatureCommand extends Command {
    default void write(SignatureVisitor visitor) {
    }

    default void updateType(String owner, XGroup group) {
    }

    default void updateFunctionAndState(XObject object, XGroup group) {
    }

    @Data
    @AllArgsConstructor
    class FormalTypeParameter implements SignatureCommand {
        String name;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitFormalTypeParameter(name);
            log("visitor.visitFormalTypeParameter(name);", name);
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            group.addPair(object, group.createTypeByName(name));
        }
    }

    @Data
    @AllArgsConstructor
    class ClassBound implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitClassBound();
            log("visitor.visitClassBound();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class InterfaceBound implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitInterfaceBound();
            log("visitor.visitInterfaceBound();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Superclass implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitSuperclass();
            log("visitor.visitSuperclass();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class Interface implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitInterface();
            log("visitor.visitInterface();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class ParameterType implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitParameterType();
            log("visitor.visitParameterType();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class ReturnType implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitReturnType();
            log("visitor.visitReturnType();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class ExceptionType implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitExceptionType();
            log("visitor.visitExceptionType();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class BaseType implements SignatureCommand {
        char descriptor;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitBaseType(descriptor);
            log("visitor.visitBaseType(descriptor);", descriptor);
        }
    }

    @Data
    @AllArgsConstructor
    class TypeVariable implements SignatureCommand {
        String name;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitTypeVariable(name);
            log("visitor.visitTypeVariable(name);", name);
        }

        @Override
        public void updateType(String owner, XGroup group) {
            group.addPair(group.createTypeByName(owner), group.createTypeByName(name));
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            group.addPair(object, group.createTypeByName(name));
        }
    }

    @Data
    @AllArgsConstructor
    class ArrayType implements SignatureCommand {
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitArrayType();
            log("visitor.visitArrayType();");
            for (SignatureCommand command : signature.getCommands()) {
                command.write(visitor);
            }
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class ClassType implements SignatureCommand {
        String name;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitClassType(name);
            log("visitor.visitClassType(name);", name);
        }

        @Override
        public void updateType(String owner, XGroup group) {
            group.addPair(group.createTypeByName(owner), group.createTypeByName(name));
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            group.addPair(object, group.createTypeByName(name));
        }
    }

    @Data
    @AllArgsConstructor
    class InnerClassType implements SignatureCommand {
        String name;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitInnerClassType(name);
            log("visitor.visitInnerClassType(name);", name);
        }

        @Override
        public void updateType(String owner, XGroup group) {
            group.addPair(group.createTypeByName(owner), group.createTypeByName(name));
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            group.addPair(object, group.createTypeByName(name));
        }
    }

    @Data
    @AllArgsConstructor
    class TypeArgument implements SignatureCommand {
        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitTypeArgument();
            log("visitor.visitTypeArgument();");
        }
    }

    @Data
    @AllArgsConstructor
    class TypeArgumentWildcard implements SignatureCommand {
        char wildcard;
        CommandGroup<SignatureCommand> signature;

        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitTypeArgument(wildcard);
            log("visitor.visitTypeArgument(wildcard);", wildcard);
        }

        @Override
        public void updateType(String owner, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateType(owner, group);
            }
        }

        @Override
        public void updateFunctionAndState(XObject object, XGroup group) {
            for (SignatureCommand command : signature.getCommands()) {
                command.updateFunctionAndState(object, group);
            }
        }
    }

    @Data
    @AllArgsConstructor
    class End implements SignatureCommand {
        @Override
        public void write(SignatureVisitor visitor) {
            visitor.visitEnd();
            log("visitor.visitEnd();");
        }
    }
}
