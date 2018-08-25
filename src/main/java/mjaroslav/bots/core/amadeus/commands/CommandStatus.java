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
        max /= 8388608;
        curr /= 8388608;
        answer.append(core.translate("status.memory", curr, max, per) + "\n");
        answer(source, "",
                new EmbedBuilder().withThumbnail(core.getClient().getOurUser().getAvatarURL().replace("webp", "png"))
                        .withColor(0x00FF00).appendDesc(answer.toString())
                        .withFooterIcon(core.getClient().getUserByID(core.devId).getAvatarURL().replace("webp", "png"))
                        .withAuthorName("AmadeusCore > " + core.name)
                        .withFooterText(
                                core.translate("status.owner", "@" + core.getClient().getUserByID(core.devId).getName() + "#" + core.getClient().getUserByID(core.devId).getDiscriminator()))
                        .build());
    }
}
