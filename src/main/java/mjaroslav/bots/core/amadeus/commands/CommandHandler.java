package mjaroslav.bots.core.amadeus.commands;

import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class CommandHandler {
    public final AmadeusCore core;

    public CommandHandler(AmadeusCore core) {
        this.core = core;
    }

    public abstract List<String> getPrefixes();

    public abstract List<BaseCommand> getCommandList();

    public abstract void executeCommand(BaseCommand command);

    public abstract BaseCommand getCommand(String text);

    public abstract void registerCommand(BaseCommand command);
}
