package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandExit extends BaseCommandDialogYesNo {
    public CommandExit(AmadeusCore core, CommandHandler handler) {
        super(core, handler);
    }

    @Override
    public void executeYes(IUser sender, IMessage source, String args) throws Exception {
        answerWarn(source, "Logging out!");
        core.disableBot();
    }

    @Override
    public void executeNo(IUser sender, IMessage source, String args) throws Exception {
        answerDone(source, "Exit canceled");
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getHelpDesc() {
        return "Stop bot programm";
    }
}
