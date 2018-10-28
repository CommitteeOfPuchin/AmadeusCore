package mjaroslav.bots.core.amadeus.commands;

import java.util.Arrays;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandReload extends BaseCommandDialogSure {
    public CommandReload(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "reload");
    }

    @Override
    public void executeConfirmed(IUser sender, IMessage source, String args) throws Exception {
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (!argsParsed.isEmpty()) {
            if (isAll(source, argsParsed.get(0))) {
                core.loadAll();
                core.sendDone(source, core.langs.translate(source, "answer_reloaded_all"));
            } else
            if (argIndex(source, "configs", argsParsed) == 0) {
                core.loadConfigs();
                core.sendDone(source, core.langs.translate(source, "answer_reloaded_configs"));
            } else if (argIndex(source, "perms", argsParsed) == 0) {
                core.loadPerms();
                core.sendDone(source, core.langs.translate(source, "answer_reloaded_langs"));
            } else if (argIndex(source, "langs", argsParsed) == 0) {
                core.loadLangs();
                core.sendDone(source, core.langs.translate(source, "answer_reloaded_langs"));
            } else
                core.sendError(source, core.langs.translate(source, "answer_bad_args"));
        } else
            core.sendError(source, core.langs.translate(source, "answer_bad_args"));
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("all", "names", "configs", "langs");
    }
}
