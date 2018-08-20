package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandExit extends BaseCommandDialogYesNo {
    public CommandExit(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "exit");
    }

    @Override
    public void executeYes(IUser sender, IMessage source, String args) throws Exception {
        answerWarn(source, core.translate("bot.exit"));
        core.disableBot();
    }

    @Override
    public void executeNo(IUser sender, IMessage source, String args) throws Exception {
        answerDone(source, core.translate("bot.exit.canceled"));
    }
}
