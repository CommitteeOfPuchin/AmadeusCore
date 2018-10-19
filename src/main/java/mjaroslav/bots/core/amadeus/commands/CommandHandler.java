package mjaroslav.bots.core.amadeus.commands;

import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public abstract class CommandHandler {
    public final AmadeusCore core;
    public final String name;

    public CommandHandler(AmadeusCore core, String name) {
        this.core = core;
        this.name = name;
    }

    public abstract List<BaseCommand> getCommandList();

    public abstract boolean executeCommand(MessageReceivedEvent event);

    public abstract BaseCommand getCommand(IGuild guild, IUser user, String text);

    public abstract void registerCommand(BaseCommand command);

    public abstract void registerCommands();
}
