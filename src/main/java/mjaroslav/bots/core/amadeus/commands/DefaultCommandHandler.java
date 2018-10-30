package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
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
        registerCommand(new CommandStop(core, this));
        registerCommand(new CommandPermissions(core, this));
        registerCommand(new CommandInfo(core, this));
        registerCommand(new CommandLangs(core, this));
    }

    @Override
    public List<BaseCommand> getCommandList() {
        return new ArrayList<BaseCommand>(commands.values());
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event) {
        core.loadConfigs();
        String text = event.getMessage().getContent();
        String commandString = AmadeusUtils.removePreifx(text, core, core.langs.getPreifxes(event.getMessage()), false);
        if (text.length() > commandString.length()) {
            BaseCommand command = getCommand(event.getGuild(), event.getChannel(), event.getAuthor(), commandString);
            if (command != null) {
                String args = AmadeusUtils.removePreifx(commandString, core,
                        core.langs.getNames(event.getMessage(), command), false);
                try {
                    if (core.permissions.canUseCommand(event.getGuild(), event.getAuthor(), command, null)) {
                        command.execute(event.getAuthor(), event.getMessage(), args);
                    } else {
                        core.sendError(event.getMessage(), core.langs.translate(event.getMessage(),
                                "answer_no_permissions", command.getCommandPermission()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    core.sendError(event.getMessage(), e);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public BaseCommand getCommand(IGuild guild, IChannel channel, IUser user, String text) {
        for (BaseCommand command : getCommandList())
            for (String name : core.langs.getNames(guild, channel, user, command))
                if (text.toLowerCase().startsWith(name) && (text.toLowerCase().replaceFirst(name, "").startsWith(" ")
                        || text.toLowerCase().replaceFirst(name, "").equals("")))
                    return command;
        return null;
    }

    @Override
    public void registerCommand(BaseCommand command) {
        commands.put(command.name, command);
    }
}
