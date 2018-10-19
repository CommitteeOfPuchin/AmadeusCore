package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class DefaultCommandHandler extends CommandHandler {
    private final HashMap<String, BaseCommand> commands = new HashMap<String, BaseCommand>();

    public DefaultCommandHandler(AmadeusCore core, String name) {
        super(core, name);
    }

    @Override
    public void registerCommands() {
        registerCommand(new CommandHelp(core, this));
        registerCommand(new CommandStatus(core, this));
        registerCommand(new CommandReload(core, this));
        registerCommand(new CommandExit(core, this));
        registerCommand(new CommandPermsInfo(core, this));
        registerCommand(new CommandBotInfo(core, this));
    }

    @Override
    public List<BaseCommand> getCommandList() {
        return new ArrayList<BaseCommand>(commands.values());
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event) {
        String text = event.getMessage().getContent();
        String commandString = AmadeusUtils.removePreifx(text, core, core.langs.getPrefixes(
                event.getChannel() != null ? event.getChannel().getGuild() : null, event.getAuthor()), false);
        if (text.length() > commandString.length()) {
            BaseCommand command = getCommand(event.getChannel() != null ? event.getChannel().getGuild() : null,
                    event.getAuthor(), commandString);
            if (command != null) {
                String args = AmadeusUtils.removePreifx(commandString, core,
                        core.langs.getNames(event.getChannel() != null ? event.getChannel().getGuild() : null,
                                event.getAuthor(), name + "." + command.name),
                        false);
                try {
                    if (core.permissions.canUseCommand(event.getGuild(), event.getAuthor(), command, null))
                        command.execute(event.getAuthor(), event.getMessage(), args);
                    else {
                        core.sendError(
                                event.getChannel() != null ? event.getChannel().getLongID()
                                        : event.getAuthor().getLongID(),
                                core.translate(event.getGuild(), event.getAuthor(), "perms.nohave",
                                        command.getCommandPermission()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    core.sendError(
                            event.getChannel() != null ? event.getChannel().getLongID() : event.getAuthor().getLongID(),
                            e);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public BaseCommand getCommand(IGuild guild, IUser user, String text) {
        for (BaseCommand command : getCommandList())
            for (String name : core.langs.getNames(guild, user, command.name)) {
                if (text.toLowerCase().startsWith(name) && (text.toLowerCase().replaceFirst(name, "").startsWith(" ")
                        || text.toLowerCase().replaceFirst(name, "").equals(""))) {
                    return command;
                }
            }
        return null;
    }

    @Override
    public void registerCommand(BaseCommand command) {
        commands.put(command.name, command);
    }
}
