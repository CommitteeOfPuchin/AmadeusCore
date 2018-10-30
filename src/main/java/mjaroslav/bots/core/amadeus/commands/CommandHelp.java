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
            EmbedBuilder builder = new EmbedBuilder().withColor(0x00FF00);
            StringBuilder desc = new StringBuilder();
            HashMap<String, String> argsParsed = AmadeusUtils.parseArgsToMap(args);
            CommandHandler handler = null;
            if (hasArg(source, "handler", argsParsed)) {
                if (argValue(source, "handler", argsParsed).equals("all")) {
                    builder.withAuthorName(core.langs.translate(source, "help_name", "all"));
                    desc.append(core.langs.translate(source, "help_command_default_help_handler") + "\n\n");
                    desc.append(core.langs.translate(source, "help_handlers") + "\n");
                    for (CommandHandler han : core.listOfCommandHandlers())
                        desc.append("`" + han.name + "` ");
                    builder.withDesc(desc.toString().trim());
                    core.answerMessage(source, builder.build());
                    return;
                } else
                    handler = core.getCommandHandler(argValue(source, "handler", argsParsed));
            }
            if (handler == null)
                handler = this.handler;
            BaseCommand command = null;
            if (hasArg(source, "command", argsParsed))
                command = handler.getCommand(source.getGuild(), source.getChannel(), sender, argValue(source, "command", argsParsed));
            if (command != null) {
                builder.withAuthorName(core.langs.translate(source, "help_name", handler.name + " > " + command.name));
                String argName = "";
                if (hasArg(source, "arg", argsParsed))
                    argName = argValue(source, "arg", argsParsed);
                if (AmadeusUtils.stringIsEmpty(argName)) {
                    desc.append(command.getHelpDesc(source));
                    desc.append("\n\n" + core.langs.translate(source, "help_command_default_help_arg"));
                    desc.append("\n\n" + core.langs.translate(source, "help_synonyms") + "\n");
                    for (String name : core.langs.getNames(source, command))
                        desc.append("`" + name + "` ");
                    if (!command.getArgsList().isEmpty())
                        desc.append("\n\n" + core.langs.translate(source, "help_args") + "\n");
                    for (String arg : command.getArgsList())
                        desc.append("`" + arg + "` ");
                } else {
                    desc.append(command.getHelpDesc(source, argName));
                    desc.append("\n\n" + core.langs.translate(source, "help_synonyms") + "\n");
                    for (String name : core.langs.getNamesArg(source, command, argName))
                        desc.append("`" + name + "` ");
                    builder.withAuthorName(core.langs.translate(source, "help_name",
                            handler.name + " > " + command.name + " > " + argName));
                }
                builder.withDesc(desc.toString().trim());
                core.answerMessage(source, builder.build());
            } else {
                builder.withAuthorName(core.langs.translate(source, "help_name", handler.name));
                desc.append(core.langs.translate(source, "help_command_default_help_command") + "\n\n");
                desc.append(core.langs.translate(source, "help_commands") + "\n");
                for (BaseCommand com : handler.getCommandList())
                    desc.append("`" + com.name + "` ");
                builder.withDesc(desc.toString().trim());
                core.answerMessage(source, builder.build());
            }
        } else
            execute(sender, source, "command=" + name + " handler=" + handler.name);
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("command", "handler", "arg");
    }
}
