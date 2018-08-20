package mjaroslav.bots.core.amadeus.commands;

import java.util.Arrays;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.config.ConfigurationHandler;
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
        if (!argsParsed.isEmpty() && argsParsed.indexOf("all") != 0) {
            if (argsParsed.get(0).toLowerCase().equals("names")) {
                if (argsParsed.size() >= 2) {
                    if (argsParsed.get(1).toLowerCase().equals("all"))
                        for (CommandHandler handler : core.getCommandHandlers())
                            handler.getNameHandler().loadNames();
                    else {
                        CommandHandler handler = core.getCommandHanler(argsParsed.get(1).toLowerCase());
                        if (handler != null)
                            handler.getNameHandler().loadNames();
                        else {
                            answerError(source,
                                    core.translate("error.reload.nohandler.command", argsParsed.get(1).toLowerCase()));
                            return;
                        }
                    }
                    answerDone(source, core.translate("done.reload.names"));
                } else {
                    answerError(source, core.translate("error.badargs"));
                    return;
                }
            } else if (argsParsed.get(0).toLowerCase().equals("configs")) {
                if (argsParsed.size() >= 2) {
                    if (argsParsed.get(1).toLowerCase().equals("all"))
                        for (ConfigurationHandler handler : core.getConfigurationHandlers())
                            handler.readConfig();
                    else {
                        ConfigurationHandler handler = core.getConfigurationHandler(argsParsed.get(1).toLowerCase());
                        if (handler != null)
                            handler.readConfig();
                        else {
                            answerError(source,
                                    core.translate("error.reload.nohandler.config", argsParsed.get(1).toLowerCase()));
                            return;
                        }
                    }
                    answerDone(source, core.translate("done.reload.configs"));
                } else {
                    answerError(source, core.translate("error.badargs"));
                    return;
                }
            } else if (argsParsed.get(0).toLowerCase().equals("langs")) {
                core.getLangHandler().loadLangs();
                answerDone(source, core.translate("done.reload.langs"));
            } else
                answerError(source, core.translate("error.badargs"));
        } else {
            for (CommandHandler handler : core.getCommandHandlers())
                handler.getNameHandler().loadNames();
            core.getLangHandler().loadLangs();
            for (ConfigurationHandler handler : core.getConfigurationHandlers())
                handler.readConfig();
            answerDone(source, core.translate("done.reload.all"));
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

    @Override
    public String getHelpDesc(String args) {
        return super.getHelpDesc(args);
    }
}
