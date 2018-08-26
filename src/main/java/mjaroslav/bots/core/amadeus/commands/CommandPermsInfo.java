package mjaroslav.bots.core.amadeus.commands;

import java.util.List;

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
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        CommandHandler handler = this.handler;
        if (hasArg("handler", argsParsed))
            handler = core.getCommandHandler(argValue("handler", argsParsed));
        if (handler == null)
            answerError(source, core.translate("permsinfo.nullhandler", argValue("handler", argsParsed)));
        else if (handler.hasPermissionHandller()) {
            StringBuilder answer = new StringBuilder();
            answer.append(core.translate("perminfo.perms") + "\n");
            for (String perm : handler.getPermissionHandler().getUserPermissions(sender, source))
                answer.append("\"" + perm + "\" ");
            answerDone(source, answer.toString());
        } else
            answerWarn(source, core.translate("permsinfo.nohandler", handler.name));
    }
}
