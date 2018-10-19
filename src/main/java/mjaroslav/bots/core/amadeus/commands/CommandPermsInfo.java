package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.EnumSet;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

public class CommandPermsInfo extends BaseCommand {
    public CommandPermsInfo(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "permsinfo");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        StringBuilder answer = new StringBuilder();
        ArrayList<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (argsParsed.isEmpty()) {
            answer.append(core.translate(source.getGuild(), sender, "permsinfo.perms") + "\n");
            for (String perm : core.permissions.getPermissions(source.getGuild(), sender))
                answer.append("`" + perm + "` ");
        } else if (hasArg("discord", argsParsed, source.getChannel() != null ? source.getChannel().getGuild() : null,
                sender)) {
            if (source.getChannel() != null) {
                long id = sender.getLongID();
                try {
                    id = Long.parseLong(argValue("discord", argsParsed,
                            source.getChannel() != null ? source.getChannel().getGuild() : null, sender));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EnumSet<Permissions> permissions = core.getClient().getUserByID(id)
                        .getPermissionsForGuild(source.getChannel().getGuild());
                answer.append(core.translate(source.getGuild(), sender, "permsinfo.perms"));
                EmbedBuilder builder = new EmbedBuilder().withDesc(answer.toString()).withTitle(String
                        .format(":white_check_mark: %s", core.translate(source.getGuild(), sender, "answer.done")))
                        .withColor(0x00FF00);
                StringBuilder available = new StringBuilder();
                StringBuilder unavailable = new StringBuilder();
                for (Permissions permission : Permissions.values()) {
                    if (permissions.contains(permission))
                        available.append(":white_check_mark: " + permission.name() + "\n");
                    else
                        unavailable.append(":no_entry_sign: " + permission.name() + "\n");
                }
                if (available.toString().isEmpty())
                    available.append(":warning: " + core.translate(source.getGuild(), sender, "permsinfo.empty"));
                if (unavailable.toString().isEmpty())
                    unavailable.append(":warning: " + core.translate(source.getGuild(), sender, "permsinfo.empty"));
                builder.appendField(core.translate(source.getGuild(), sender, "permsinfo.available"),
                        available.toString(), true);
                builder.appendField(core.translate(source.getGuild(), sender, "permsinfo.unavailable"),
                        unavailable.toString(), true);
                answer(source, "", builder.build());
                return;
            }
        }
        answerDone(source, answer.toString());
    }
}
