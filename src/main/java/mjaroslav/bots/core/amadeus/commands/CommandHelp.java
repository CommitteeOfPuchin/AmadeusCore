package mjaroslav.bots.core.amadeus.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
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
            if (hasArg(source, "handler", argsParsed))
                handler = core.getCommandHandler(argValue(source, "handler", argsParsed));
            if (handler == null)
                handler = this.handler;
            BaseCommand command = null;
            if (hasArg(source, "command", argsParsed))
                command = handler.getCommand(source.getGuild(), sender, argValue(source, "command", argsParsed));
            EmbedBuilder builder = new EmbedBuilder().withColor(0x00FF00);
            StringBuilder desc = new StringBuilder();
            if (command != null) {
                builder.withAuthorName(core.langs.translate(source.getGuild(), sender, "help.commandname",
                        handler.name + " > " + command.name));
                String argName = "";
                if (hasArg(source, "arg", argsParsed))
                    argName = argValue(source, "arg", argsParsed);
                if (AmadeusUtils.stringIsEmpty(argName)) {
                    desc.append(command.getHelpDesc(source));
                    desc.append("\n\n" + core.langs.translate(source, "help.commands.names") + "\n");
                    for (String name : core.langs.getNames(source, command))
                        desc.append("\"" + name + "\" ");
                    if (!command.getArgsList().isEmpty())
                        desc.append("\n\n" + core.langs.translate(source, "help.args") + "\n");
                    for (String arg : command.getArgsList())
                        desc.append("\"" + arg + "\" ");
                } else {
                    desc.append(command.getHelpDesc(source, argName));
                    desc.append("\n\n" + core.langs.translate(source, "help.commands.names") + "\n");
                    for (String name : core.langs.getNamesArg(source, command, argName))
                        desc.append("\"" + name + "\" ");
                    builder.withAuthorName(core.langs.translate(source, "help.commandname",
                            handler.name + " > " + command.name + " > " + argName));
                }

                builder.withDesc(desc.toString().trim());
                core.answerMessage(source, builder.build());
            } else {
                builder.withAuthorName(core.langs.translate(source, "help.handlername", handler.name));
                desc.append(core.langs.translate(source, "help.commands") + "\n");
                for (BaseCommand com : handler.getCommandList())
                    desc.append("\"" + com.name + "\" ");
                builder.withDesc(desc.toString().trim());
                core.answerMessage(source, builder.build());
            }
        } else
            execute(sender, source, "command=" + name + " handler=" + handler.name);
    }

    @Override
    public String getHelpDesc(IMessage message, String args) {
        String result = super.getHelpDesc(message, args);
        switch (args) {
            case "command":
                result = core.langs.translate(message, "help." + name + ".command");
                break;
            case "handler":
                result = core.langs.translate(message, "help." + name + ".handler");
                break;
        }
        return result;
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("command", "handler", "arg");
    }
}
