package mjaroslav.bots.core.amadeus.commands;

import java.sql.ResultSet;
import java.util.ArrayList;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

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
            for (String perm : core.getPermissionHandler().getUserPermissions(sender, source))
                answer.append("`" + perm + "` ");
        } else if (hasArg("dump", argsParsed)) {
            String type = argsParsed.get(argIndex("dump", argsParsed) + 1);
            ResultSet result = core.getDatabaseHandler("default").executeQuery("SELECT * FROM by" + type);
            while (result.next()) {
                answer.append(result.getString(1) + " = " + result.getString(2) + " - " + result.getString(3)).append("\n");
            }
        }
        answerDone(source, answer.toString());
    }
}
