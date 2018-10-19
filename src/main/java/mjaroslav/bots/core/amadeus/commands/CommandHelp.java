package mjaroslav.bots.core.amadeus.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CommandHelp extends BaseCommand {
    public CommandHelp(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "help");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        if (args.length() > 0) {
            HashMap<String, String> argsParsed = AmadeusUtils.parseArgsToMap(args);
            CommandHandler handler = null;
            if (hasArg("handler", argsParsed, source.getChannel() != null ? source.getChannel().getGuild() : null,
                    sender))
                handler = core.getCommandHandler(argValue("handler", argsParsed,
                        source.getChannel() != null ? source.getChannel().getGuild() : null, sender));
            if (handler == null)
                handler = this.handler;
            BaseCommand command = null;
            if (hasArg("command", argsParsed, source.getChannel() != null ? source.getChannel().getGuild() : null,
                    sender))
                command = handler.getCommand(source.getChannel() != null ? source.getChannel().getGuild() : null,
                        sender, argValue("command", argsParsed,
                                source.getChannel() != null ? source.getChannel().getGuild() : null, sender));
            EmbedBuilder builder = new EmbedBuilder().withColor(0x00FF00);
            StringBuilder desc = new StringBuilder();
            if (command != null) {
                builder.withAuthorName(core.translate(source.getGuild(), sender, "help.commandname",
                        handler.name + " > " + command.name));
                String argName = "";
                if (hasArg("arg", argsParsed, source.getChannel() != null ? source.getChannel().getGuild() : null,
                        sender))
                    argName = argValue("arg", argsParsed,
                            source.getChannel() != null ? source.getChannel().getGuild() : null, sender);
                if (AmadeusUtils.stringIsEmpty(argName)) {
                    desc.append(command.getHelpDesc(sender, source.getGuild()));
                    desc.append("\n\n" + core.translate(source.getGuild(), sender, "help.commands.names") + "\n");
                    for (String name : core.langs.getNames(
                            source.getChannel() != null ? source.getChannel().getGuild() : null, sender,
                            command.handler.name + "." + command.name))
                        desc.append("\"" + name + "\" ");
                    if (!command.getArgsList().isEmpty())
                        desc.append("\n\n" + core.translate(source.getGuild(), sender, "help.args") + "\n");
                    for (String arg : command.getArgsList())
                        desc.append("\"" + arg + "\" ");
                } else {
                    desc.append(command.getHelpDesc(sender, source.getGuild(), argName));
                    desc.append("\n\n" + core.translate(source.getGuild(), sender, "help.commands.names") + "\n");
                    for (String name : core.langs.getNamesArg(
                            source.getChannel() != null ? source.getChannel().getGuild() : null, sender,
                            command.handler.name + "." + command.name, argName))
                        desc.append("\"" + name + "\" ");
                    builder.withAuthorName(core.translate(source.getGuild(), sender, "help.commandname",
                            handler.name + " > " + command.name + " > " + argName));
                }

                builder.withDesc(desc.toString().trim());
                answer(source, "", builder.build());
            } else {
                builder.withAuthorName(core.translate(source.getGuild(), sender, "help.handlername", handler.name));
                desc.append(core.translate(source.getGuild(), sender, "help.commands") + "\n");
                for (BaseCommand com : handler.getCommandList())
                    desc.append("\"" + com.name + "\" ");
                builder.withDesc(desc.toString().trim());
                answer(source, "", builder.build());
            }
        } else
            execute(sender, source, "command=" + name + " handler=" + handler.name);
    }

    @Override
    public String getHelpDesc(IUser user, IGuild guild, String args) {
        String result = super.getHelpDesc(user, guild, args);
        switch (args) {
            case "command":
                result = core.translate(guild, user, "help." + name + ".command");
                break;
            case "handler":
                result = core.translate(guild, user, "help." + name + ".handler");
                break;
        }
        return result;
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("command", "handler", "arg");
    }
}
