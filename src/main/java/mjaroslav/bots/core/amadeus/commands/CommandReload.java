package mjaroslav.bots.core.amadeus.commands;

import java.util.Arrays;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandReload extends BaseCommandDialogYesNo {
    public CommandReload(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "reload");
    }

    @Override
    public void executeYes(IUser sender, IMessage source, String args) throws Exception {
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (!argsParsed.isEmpty() && argIndex("all", argsParsed) != 0) {
            if (argIndex("names", argsParsed) == 0) {
                core.loadNames();
            } else if (argIndex("configs", argsParsed) == 0) {
                core.loadConfigs();
            } else if (argIndex("perms", argsParsed) == 0) {
                core.loadPerms();
            } else if (argIndex("langs", argsParsed) == 0) {
                core.loadLangs();
                answerDone(source, core.translate(source.getGuild(), sender, "done.reload.langs"));
            } else
                answerError(source, core.translate(source.getGuild(), sender, "error.badargs"));
        } else {
            core.loadAll();
            answerDone(source, core.translate(source.getGuild(), sender, "done.reload.all"));
        }
    }

    @Override
    public void executeNo(IUser sender, IMessage source, String args) throws Exception {
        answerDone(source, "Reloading canceled");
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("all", "names", "configs", "langs");
    }
}
