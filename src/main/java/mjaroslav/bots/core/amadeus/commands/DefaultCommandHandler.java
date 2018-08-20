package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class DefaultCommandHandler extends CommandHandler {
    private final ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();

    private final CommandNameHandler nameHandler = new DefaultCommandNameHandler(core);

    public DefaultCommandHandler(AmadeusCore core) {
        super(core, "default");
        getNameHandler().loadNames();
    }

    @Override
    public List<String> getPrefixes() {
        return getNameHandler().getPrefixes();
    }

    @Override
    public List<BaseCommand> getCommandList() {
        return commands;
    }

    @Override
    public boolean executeCommand(MessageReceivedEvent event) {
        String text = event.getMessage().getContent();
        String commandString = AmadeusUtils.removePreifx(text, core, getPrefixes());
        if (text.length() > commandString.length()) {
            BaseCommand command = getCommand(commandString);
            if (command != null) {
                String args = AmadeusUtils.removePreifx(commandString, core, command);
                try {
                    command.execute(event.getAuthor(), event.getMessage(), args);
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
    public BaseCommand getCommand(String text) {
        for (BaseCommand command : getCommandList())
            for (String name : getNameHandler().getNames(command.getName()))
                if (text.toLowerCase().startsWith(name))
                    return command;
        return null;
    }

    @Override
    public void registerCommand(BaseCommand command) {
        for (BaseCommand checkCommand : commands)
            for (String checkCommandName : getNameHandler().getNames(checkCommand.getName()))
                for (String commandName : getNameHandler().getNames(command.getName()))
                    if (checkCommandName.equals(commandName))
                        return;
        commands.add(command);
        System.out.println("Command added");
    }

    @Override
    public CommandNameHandler getNameHandler() {
        return nameHandler;
    }

    @Override
    public BaseCommand getHelp() {
        return getCommand("help");
    }
}
