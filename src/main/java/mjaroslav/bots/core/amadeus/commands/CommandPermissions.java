package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

public class CommandPermissions extends BaseCommand {
    public CommandPermissions(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "permissions");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        StringBuilder answer = new StringBuilder();
        ArrayList<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (argsParsed.isEmpty()) {
            answer.append(core.langs.translate(source, "permissions_my") + "\n");
            for (String perm : core.permissions.getPermissions(source.getGuild(), sender))
                answer.append("`" + perm + "` ");
        } else if (hasArg(source, "discord", argsParsed)) {
            if (!canUseArg(source, "discord"))
                return;
            if (!core.isPrivateMessage(source)) {
                IUser user = sender;
                boolean flag = false;
                if (hasArg(source, "discord.user", argsParsed)) {
                    if (!canUseArg(source, "discord.user"))
                        return;
                    user = core.argUser(argValue(source, "discord.user", argsParsed));
                    flag = true;
                }
                EnumSet<Permissions> permissions = user.getPermissionsForGuild(source.getChannel().getGuild());
                if (flag)
                    answer.append(core.langs.translate(source, "permissions_my"));
                else
                    answer.append(core.langs.translate(source, "permissions_user", user.mention(true)));
                EmbedBuilder builder = new EmbedBuilder().withDesc(answer.toString())
                        .withTitle(String.format(":white_check_mark: %s", core.langs.translate(source, "answer_done")))
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
                    available.append(":warning: " + core.langs.translate(source, "list_empty"));
                if (unavailable.toString().isEmpty())
                    unavailable.append(":warning: " + core.langs.translate(source, "list_empty"));
                builder.appendField(core.langs.translate(source, "permissions_available"), available.toString(), true);
                builder.appendField(core.langs.translate(source, "permissions_unavailable"), unavailable.toString(),
                        true);
                core.answerMessage(source, builder.build());
                return;
            } else {
                core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                return;
            }
        } else if (hasArg(source, "user", argsParsed)) {
            if (!canUseArg(source, "user"))
                return;
            IUser user = core.argUser(argValue(source, "user", argsParsed));
            if (user == null) {
                core.sendError(source, core.langs.translate(source, "answer_bag_user"));
                return;
            }
            answer.append(core.langs.translate(source, "permissions_user", user.mention(true)) + "\n");
            for (String perm : core.permissions.getPermissions(source.getGuild(), user))
                answer.append("`" + perm + "` ");
        }
        core.sendDone(source, answer.toString());
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("user", "discord", "discord.user");
    }
}
