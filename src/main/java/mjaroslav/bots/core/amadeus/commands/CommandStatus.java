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
        EmbedBuilder builder = core.info.toEmbedBuilder();
        StringBuilder answer = new StringBuilder();
        answer.append(core.translate("status.guilds", core.getClient().getGuilds().size()) + "\n");
        answer.append(core.translate("status.users", core.getClient().getUsers().size()) + "\n");
        answer.append(core.translate("status.channels", core.getClient().getChannels().size()));
        builder.appendField(core.translate("status.discord"), answer.toString(), true);
        answer.delete(0, answer.length());
        answer.append(core.translate("status.handlers", core.listOfCommandHandlers().size()) + "\n");
        answer.append(core.translate("status.commands", core.getCommandCount()) + "\n");
        answer.append(core.translate("status.databases", core.listOfDatabaseHandlers().size()) + "\n");
        answer.append(core.translate("status.langs", core.getLangHandler().getLangs().size()) + "\n");
        answer.append(core.translate("status.configs", core.listOfConfigurationHandlers().size()) + "\n");
        double max = Runtime.getRuntime().maxMemory();
        double curr = max - Runtime.getRuntime().freeMemory();
        String per = String.format("%.2f", curr * 100F / max) + "%";
        max /= 8388608;
        curr /= 8388608;
        builder.appendField(core.translate("status.memory"), core.translate("status.memory.value", curr, max, per),
                true);
        builder.appendField(core.translate("status.bot"), answer.toString(), true);
        if (!core.hideInvite && core.info.hasInvite())
            builder.appendField(core.translate("status.invite"), core.info.getInvite(), true);
        answer(source, "", builder.withColor(0x00FF00).build());
    }
}
