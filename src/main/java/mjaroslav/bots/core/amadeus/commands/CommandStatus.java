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
        StringBuilder answer = new StringBuilder();
        answer.append(core.translate("status.botinfo") + "\n\n");
        answer.append(core.translate("status.discord") + "\n");
        answer.append(core.translate("status.guilds", core.getClient().getGuilds().size()) + "\n");
        answer.append(core.translate("status.users", core.getClient().getUsers().size()) + "\n");
        answer.append(core.translate("status.channels", core.getClient().getChannels().size()) + "\n\n");
        answer.append(core.translate("status.bot") + "\n");
        answer.append(core.translate("status.handlers", core.getCommandHandlers().size()) + "\n");
        answer.append(core.translate("status.commands", core.getCommandCount()) + "\n");
        answer.append(core.translate("status.langs", core.getLangHandler().getLangs().size()) + "\n");
        answer.append(core.translate("status.configs", core.getConfigurationHandlers().size()) + "\n");
        double max = Runtime.getRuntime().maxMemory();
        double curr = max - Runtime.getRuntime().freeMemory();
        String per = String.format("%.2f", curr * 100F / max) + "%";
        max /= 8 * 1024 * 1024;
        curr /= 8 * 1024 * 1024;
        answer.append(core.translate("status.memory", curr, max, per) + "\n");
        answer(source, "", new EmbedBuilder().withThumbnail(core.getClient().getOurUser().getAvatarURL())
                .withColor(0x00FF00).appendDesc(answer.toString()).withAuthorName(core.name).build());
    }
}
