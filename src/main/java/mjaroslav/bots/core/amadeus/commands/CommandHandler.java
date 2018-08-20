package mjaroslav.bots.core.amadeus.commands;

import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class CommandHandler {
    public final AmadeusCore core;
    public final String name;

    public CommandHandler(AmadeusCore core, String name) {
        this.core = core;
        this.name = name;
    }

    public abstract List<String> getPrefixes();

    public abstract List<BaseCommand> getCommandList();

    public abstract boolean executeCommand(MessageReceivedEvent event);

    public abstract BaseCommand getCommand(String text);

    public abstract void registerCommand(BaseCommand command);

    public abstract CommandNameHandler getNameHandler();

    public abstract BaseCommand getHelp();
}
