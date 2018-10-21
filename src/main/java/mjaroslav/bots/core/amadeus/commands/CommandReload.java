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
        if (!argsParsed.isEmpty() && argIndex(source, "all", argsParsed) != 0) {
            if (argIndex(source, "configs", argsParsed) == 0) {
                core.loadConfigs();
            } else if (argIndex(source, "perms", argsParsed) == 0) {
                core.loadPerms();
            } else if (argIndex(source, "langs", argsParsed) == 0) {
                core.loadLangs();
                core.sendDone(source, core.langs.translate(source, "done.reload.langs"));
            } else
                core.sendError(source, core.langs.translate(source, "error.badargs"));
        } else {
            core.loadAll();
            core.sendDone(source, core.langs.translate(source, "done.reload.all"));
        }
    }

    @Override
    public void executeNo(IUser sender, IMessage source, String args) throws Exception {
        core.sendDone(source, "Reloading canceled");
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("all", "names", "configs", "langs");
    }
}
