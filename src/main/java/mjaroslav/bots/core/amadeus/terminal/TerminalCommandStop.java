package mjaroslav.bots.core.amadeus.terminal;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public class TerminalCommandStop extends BaseTerminalCommandDialogYesNo {
    public TerminalCommandStop(AmadeusCore core, TerminalCommandHandler handler) {
        super(core, handler, "stop");
    }

    @Override
    public void executeYes(String args) throws Exception {
        answer("Bot stopping");
        core.disableBot();
    }

    @Override
    public void executeNo(String args) throws Exception {
        answer("Bot stopping canceled");
    }
}
