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
        answer.append(core.langs.translate(source, "status_guilds", core.client.getGuilds().size()) + "\n");
        answer.append(core.langs.translate(source, "status_users", core.client.getUsers().size()) + "\n");
        answer.append(core.langs.translate(source, "status_channels", core.client.getChannels().size()));
        builder.appendField(core.langs.translate(source, "status_discord"), answer.toString(), true);
        answer.delete(0, answer.length());
        answer.append(core.langs.translate(source, "status_handlers", core.listOfCommandHandlers().size()) + "\n");
        answer.append(core.langs.translate(source, "status_commands", core.getCommandCount()) + "\n");
        answer.append(core.langs.translate(source, "status_databases", core.databases.count()) + "\n");
        answer.append(core.langs.translate(source, "status_langs", core.i18n.count()) + "\n");
        answer.append(core.langs.translate(source, "status_configs", core.listOfConfigurationHandlers().size()) + "\n");
        double max = Runtime.getRuntime().maxMemory();
        double curr = max - Runtime.getRuntime().freeMemory();
        String per = String.format("%.2f", curr * 100F / max) + "%";
        max /= 8388608;
        curr /= 8388608;
        builder.appendField(core.langs.translate(source, "status_memory"),
                core.langs.translate(source, "status_memory_value", curr, max, per), true);
        builder.appendField(core.langs.translate(source, "status_bot"), answer.toString(), true);
        if (!core.optionHideInvite && core.info.hasInvite())
            builder.appendField(core.langs.translate(source, "status_invite"), core.info.getInvite(), true);
        core.answerMessage(source, builder.withColor(0x00FF00).build());
    }
}
