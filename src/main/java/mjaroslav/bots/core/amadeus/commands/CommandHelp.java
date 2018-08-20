package mjaroslav.bots.core.amadeus.commands;

import java.util.HashMap;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CommandHelp extends BaseCommand {
    public CommandHelp(AmadeusCore core, CommandHandler handler) {
        super(core, handler);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        if (args.length() > 0) {
            HashMap<String, String> argsParsed = AmadeusUtils.parseArgsToMap(args);
            CommandHandler handler = core.getCommandHanler(argsParsed.getOrDefault("handler", ""));
            if (handler == null)
                handler = this.handler;
            BaseCommand command = handler.getCommand(argsParsed.getOrDefault("command", ""));
            EmbedBuilder builder = new EmbedBuilder().withColor(0x00FF00);
            StringBuilder desc = new StringBuilder();
            if (command != null) {
                builder.withAuthorName(command.getName());
                desc.append(command.getHelpDesc()).append("\n**Синонимы:**\n");
                for (String name : handler.getNameHandler().getNames(command.getName()))
                    desc.append("\"" + name + "\" ");
                builder.withDesc(desc.toString().trim());
                answer(source, "", builder.build());
            } else {
                builder.withAuthorName(handler.name);
                desc.append("**Команды:**\n");
                for (BaseCommand com : handler.getCommandList())
                    desc.append("\"" + com.getName() + "\" ");
                builder.withDesc(desc.toString().trim());
                answer(source, "", builder.build());
            }
        } else
            execute(sender, source, "command=" + getName());
    }

    @Override
    public String getHelpDesc() {
        return "Список команд и их описание";
    }
}
