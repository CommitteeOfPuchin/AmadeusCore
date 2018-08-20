package mjaroslav.bots.core.amadeus.commands;

import java.util.Arrays;
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
            List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
            CommandHandler handler = null;
            if (argsParsed.contains("handler"))
                handler = core.getCommandHanler(argsParsed.get(argsParsed.indexOf("handler")) + 1);
            if (handler == null)
                handler = this.handler;
            BaseCommand command = null;
            if (argsParsed.contains("command"))
                command = handler.getCommand(argsParsed.get(argsParsed.indexOf("command") + 1));
            EmbedBuilder builder = new EmbedBuilder().withColor(0x00FF00);
            StringBuilder desc = new StringBuilder();
            if (command != null) {
                builder.withAuthorName(core.translate("help.commandname", command.name));
                String argName = "";
                if (argsParsed.contains("arg"))
                    argName = argsParsed.get(argsParsed.indexOf("arg") + 1);
                if (AmadeusUtils.stringIsEmpty(argName))
                    desc.append(command.getHelpDesc());
                else {
                    desc.append(command.getHelpDesc(argName));
                    builder.withAuthorName(core.translate("help.commandname", command.name + " > " + argName));
                }
                desc.append("\n\n" + core.translate("help.commands.names") + "\n");
                for (String name : handler.getNameHandler().getNames(command.name))
                    desc.append("\"" + name + "\" ");
                if (!command.getArgsList().isEmpty())
                    desc.append("\n\n" + core.translate("help.args") + "\n");
                for (String arg : command.getArgsList())
                    desc.append("\"" + arg + "\" ");
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
            execute(sender, source, "command help" + name);
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
        return Arrays.asList("command", "handler");
    }
}
