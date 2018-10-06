package mjaroslav.bots.core.amadeus.commands;

import java.sql.ResultSet;
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
            answer.append(core.translate("permsinfo.perms") + "\n");
            for (String perm : core.getPermissionHandler().getAllPermissions(sender, source))
                answer.append("`" + perm + "` ");

        } else if (hasArg("discord", argsParsed)) {
            if (source.getChannel() != null) {
                long id = sender.getLongID();
                try {
                    id = Long.parseLong(argValue("discord", argsParsed));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EnumSet<Permissions> permissions = core.getClient().getUserByID(id)
                        .getPermissionsForGuild(source.getChannel().getGuild());
                answer.append(core.translate("permsinfo.perms"));
                EmbedBuilder builder = new EmbedBuilder().withDesc(answer.toString())
                        .withTitle(String.format(":white_check_mark: %s", core.translate("answer.done")))
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
                    available.append(":warning: " + core.translate("permsinfo.empty"));
                if (unavailable.toString().isEmpty())
                    unavailable.append(":warning: " + core.translate("permsinfo.empty"));
                builder.appendField(core.translate("permsinfo.available"), available.toString(), true);
                builder.appendField(core.translate("permsinfo.unavailable"), unavailable.toString(), true);
                answer(source, "", builder.build());
                return;
            }
        } else if (hasArg("dump", argsParsed)) {
            String type = argsParsed.get(argIndex("dump", argsParsed) + 1);
            ResultSet result = core.getDatabaseHandler("default").executeQuery("SELECT * FROM p" + type.toLowerCase());
            while (result.next()) {
                answer.append(result.getString(1) + " = " + result.getString(2) + " - " + result.getString(3))
                        .append("\n");
            }
        }
        answerDone(source, answer.toString());
    }
}
