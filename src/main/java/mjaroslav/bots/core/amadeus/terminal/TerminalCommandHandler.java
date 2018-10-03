package mjaroslav.bots.core.amadeus.terminal;

import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class TerminalCommandHandler extends Thread {
    public final AmadeusCore core;

    public TerminalCommandHandler(AmadeusCore core) {
        super();
        this.core = core;
    }

    public abstract List<BaseTerminalCommand> getCommandList();

    public abstract void executeCommand(String command);

    public abstract BaseTerminalCommand getCommand(String text);

    public abstract void registerCommands();

    public abstract void registerCommand(BaseTerminalCommand command);

    public abstract String readLine();
    
    public abstract void answer(Object answer);
}
