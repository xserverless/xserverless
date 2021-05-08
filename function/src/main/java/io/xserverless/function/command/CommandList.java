package io.xserverless.function.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommandList<C extends Command> {
    private List<C> commands = new ArrayList<>();

    public void add(C c) {
        commands.add(c);
    }
}
