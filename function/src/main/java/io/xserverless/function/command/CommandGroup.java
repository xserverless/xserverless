package io.xserverless.function.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CommandGroup<C extends Command> {
    private String owner;
    private String name;
    private String descriptor;
    private List<C> commands = new ArrayList<>();

    public void add(C c) {
        commands.add(c);
    }
}
