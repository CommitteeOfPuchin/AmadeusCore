package mjaroslav.bots.core.amadeus.commands;

import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class CommandNameHandler {
    public final AmadeusCore core;

    public CommandNameHandler(AmadeusCore core) {
        this.core = core;
    }

    public abstract List<String> getNames(String key);
}
