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
            if (hasArg("handler", argsParsed))
                handler = core.getCommandHanler(argValue("handler", argsParsed));
            if (handler == null)
                handler = this.handler;
            BaseCommand command = null;
            if (hasArg("command", argsParsed))
                command = handler.getCommand(argValue("command", argsParsed));
            EmbedBuilder builder = new EmbedBuilder().withColor(0x00FF00);
            StringBuilder desc = new StringBuilder();
            if (command != null) {
                builder.withAuthorName(core.translate("help.commandname", command.name));
                String argName = "";
                if (hasArg("arg", argsParsed))
                    argName = argValue("arg", argsParsed);
                if (AmadeusUtils.stringIsEmpty(argName)) {
                    desc.append(command.getHelpDesc());
                    desc.append("\n\n" + core.translate("help.commands.names") + "\n");
                    for (String name : handler.getNameHandler().getNames(command.name))
                        desc.append("\"" + name + "\" ");
                    if (!command.getArgsList().isEmpty())
                        desc.append("\n\n" + core.translate("help.args") + "\n");
                    for (String arg : command.getArgsList())
                        desc.append("\"" + arg + "\" ");
                } else {
                    desc.append(command.getHelpDesc(argName));
                    desc.append("\n\n" + core.translate("help.commands.names") + "\n");
                    for (String name : handler.getNameHandler().getArgNames(command.name, argName))
                        desc.append("\"" + name + "\" ");
                    builder.withAuthorName(core.translate("help.commandname", command.name + " > " + argName));
                }

                builder.withDesc(desc.toString().trim());
                answer(source, "", builder.build());
            } else {
                builder.withAuthorName(core.translate("help.handlername", handler.name));
                desc.append(core.translate("help.commands") + "\n");
                for (BaseCommand com : handler.getCommandList())
                    desc.append("\"" + com.name + "\" ");
                builder.withDesc(desc.toString().trim());
                answer(source, "", builder.build());
            }
        } else
            execute(sender, source, "command=" + name + " handler=" + handler.name);
    }

    @Override
    public String getHelpDesc(String args) {
        String result = super.getHelpDesc(args);
        switch (args) {
        case "command":
            result = core.translate("help." + name + ".command");
            break;
        case "handler":
            result = core.translate("help." + name + ".handler");
            break;
        }
        return result;
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("command", "handler", "arg");
    }
}
