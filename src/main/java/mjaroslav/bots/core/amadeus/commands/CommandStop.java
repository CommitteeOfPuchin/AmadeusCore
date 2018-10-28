package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandStop extends BaseCommandDialogSure {
    public CommandStop(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "stop");
    }

    @Override
    public void executeConfirmed(IUser sender, IMessage source, String args) throws Exception {
        core.sendWarn(source, core.langs.translate(source, "bot_stopping"));
        core.stopBot();
    }

    @Override
    public boolean onlyOwner() {
        return true;
    }
}
