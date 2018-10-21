package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class CommandStatus extends BaseCommand {
    public CommandStatus(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "status");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        EmbedBuilder builder = core.info.toEmbedBuilder(source.getGuild(), sender);
        StringBuilder answer = new StringBuilder();
        answer.append(core.langs.translate(source, "status.guilds", core.client.getGuilds().size()) + "\n");
        answer.append(core.langs.translate(source, "status.users", core.client.getUsers().size()) + "\n");
        answer.append(core.langs.translate(source, "status.channels", core.client.getChannels().size()));
        builder.appendField(core.langs.translate(source, "status.discord"), answer.toString(), true);
        answer.delete(0, answer.length());
        answer.append(core.langs.translate(source, "status.handlers", core.listOfCommandHandlers().size()) + "\n");
        answer.append(core.langs.translate(source, "status.commands", core.getCommandCount()) + "\n");
        answer.append(core.langs.translate(source, "status.databases", core.databases.count()) + "\n");
        answer.append(core.langs.translate(source, "status.langs", core.i18n.count()) + "\n");
        answer.append(core.langs.translate(source, "status.configs", core.listOfConfigurationHandlers().size()) + "\n");
        double max = Runtime.getRuntime().maxMemory();
        double curr = max - Runtime.getRuntime().freeMemory();
        String per = String.format("%.2f", curr * 100F / max) + "%";
        max /= 8388608;
        curr /= 8388608;
        builder.appendField(core.langs.translate(source, "status.memory"),
                core.langs.translate(source, "status.memory.value", curr, max, per), true);
        builder.appendField(core.langs.translate(source, "status.bot"), answer.toString(), true);
        if (!core.optionHideInvite && core.info.hasInvite())
            builder.appendField(core.langs.translate(source, "status.invite"), core.info.getInvite(), true);
        core.answerMessage(source, builder.withColor(0x00FF00).build());
    }
}
