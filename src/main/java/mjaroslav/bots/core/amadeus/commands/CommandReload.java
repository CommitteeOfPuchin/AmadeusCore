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
        if (!argsParsed.isEmpty() && argIndex("all", argsParsed) != 0) {
            if (argIndex("names", argsParsed) == 0) {
                if (argsParsed.size() >= 2) {
                    String value = argValue("names", argsParsed);
                    if (value.toLowerCase().equals("all"))
                        for (CommandHandler handler : core.getCommandHandlers()) {
                            if (handler.getNameHandler() != null)
                                handler.getNameHandler().loadNames();
                        }
                    else {
                        CommandHandler handler = core.getCommandHanler(value.toLowerCase());
                        if (handler != null && handler.hasNameHandller())
                            handler.getNameHandler().loadNames();
                        else {
                            answerError(source, core.translate("error.reload.nohandler.command", value.toLowerCase()));
                            return;
                        }
                    }
                    answerDone(source, core.translate("done.reload.names"));
                } else {
                    answerError(source, core.translate("error.badargs"));
                    return;
                }
            } else if (argIndex("configs", argsParsed) == 0) {
                if (argsParsed.size() >= 2) {
                    String value = argValue("configs", argsParsed);
                    if (value.toLowerCase().equals("all"))
                        for (ConfigurationHandler handler : core.getConfigurationHandlers())
                            handler.readConfig();
                    else {
                        ConfigurationHandler handler = core.getConfigurationHandler(value.toLowerCase());
                        if (handler != null)
                            handler.readConfig();
                        else {
                            answerError(source, core.translate("error.reload.nohandler.config", value.toLowerCase()));
                            return;
                        }
                    }
                    answerDone(source, core.translate("done.reload.configs"));
                } else {
                    answerError(source, core.translate("error.badargs"));
                    return;
                }
            } else if (argIndex("perms", argsParsed) == 0) {
                if (argsParsed.size() >= 2) {
                    String value = argValue("perms", argsParsed);
                    if (value.toLowerCase().equals("all"))
                        for (CommandHandler handler : core.getCommandHandlers()) {
                            if (handler.hasPermissionHandller())
                                handler.getPermissionHandler().loadPermissions();
                        }
                    else {
                        CommandHandler handler = core.getCommandHanler(value.toLowerCase());
                        if (handler != null && handler.hasPermissionHandller())
                            handler.getPermissionHandler().loadPermissions();
                        else {
                            answerError(source, core.translate("error.reload.nohandler.command", value.toLowerCase()));
                            return;
                        }
                    }
                    answerDone(source, core.translate("done.reload.perms"));
                } else {
                    answerError(source, core.translate("error.badargs"));
                    return;
                }
            } else if (argIndex("langs", argsParsed) == 0) {
                core.getLangHandler().loadLangs();
                answerDone(source, core.translate("done.reload.langs"));
            } else
                answerError(source, core.translate("error.badargs"));
        } else {
            for (CommandHandler handler : core.getCommandHandlers())
                if (handler.getNameHandler() != null)
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
    
    @Override
    public String getPermissions() {
        return "admin";
    }
}
