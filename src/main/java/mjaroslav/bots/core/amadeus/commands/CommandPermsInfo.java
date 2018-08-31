package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandPermsInfo extends BaseCommand {
    public CommandPermsInfo(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "permsinfo");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        StringBuilder answer = new StringBuilder();
        answer.append(core.translate("permsinfo.perms") + "\n");
        for (String perm : core.getPermissionHandler().getUserPermissions(sender, source))
            answer.append("`" + perm + "` ");
        answerDone(source, answer.toString());
    }
}
