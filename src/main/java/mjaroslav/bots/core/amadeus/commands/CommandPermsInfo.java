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
            answer.append(core.langs.translate(source, "permsinfo.perms") + "\n");
            for (String perm : core.permissions.getPermissions(source.getGuild(), sender))
                answer.append("`" + perm + "` ");
        } else if (hasArg(source, "user", argsParsed)) {
            try {
                long id = Long.parseLong(argValue(source, "user", argsParsed));
                answer.append(core.langs.translate(source, "permsinfo.perms") + "\n");
                for (String perm : core.permissions.getPermissions(source.getGuild(), core.client.getUserByID(id)))
                    answer.append("`" + perm + "` ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (hasArg(source, "discord", argsParsed)) {
            if (source.getChannel() != null) {
                long id = sender.getLongID();
                boolean flag = false;
                try {
                    flag = true;
                    id = Long.parseLong(argValue(source, "discord", argsParsed));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EnumSet<Permissions> permissions = core.client.getUserByID(id)
                        .getPermissionsForGuild(source.getChannel().getGuild());
                if (flag)
                    answer.append(core.langs.translate(source, "permsinfo.permsuser",
                            core.client.getUserByID(id).mention(true)));
                else
                    answer.append(core.langs.translate(source, "permsinfo.perms"));
                EmbedBuilder builder = new EmbedBuilder().withDesc(answer.toString())
                        .withTitle(String.format(":white_check_mark: %s", core.langs.translate(source, "answer.done")))
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
                    available.append(":warning: " + core.langs.translate(source, "permsinfo.empty"));
                if (unavailable.toString().isEmpty())
                    unavailable.append(":warning: " + core.langs.translate(source, "permsinfo.empty"));
                builder.appendField(core.langs.translate(source, "permsinfo.available"), available.toString(), true);
                builder.appendField(core.langs.translate(source, "permsinfo.unavailable"), unavailable.toString(),
                        true);
                core.answerMessage(source, builder.build());
                return;
            }
        }
        core.sendDone(source, answer.toString());
    }
}
