package mjaroslav.bots.core.amadeus.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public class DefaultTerminalCommandHandler extends TerminalCommandHandler {
    private final HashMap<String, BaseTerminalCommand> commands = new HashMap<String, BaseTerminalCommand>();
    private final Scanner scanner = new Scanner(System.in);

    public DefaultTerminalCommandHandler(AmadeusCore core) {
        super(core);
        setDaemon(true);
    }

    @Override
    public List<BaseTerminalCommand> getCommandList() {
        return new ArrayList<BaseTerminalCommand>(commands.values());
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public void executeCommand(String text) {
        BaseTerminalCommand command = getCommand(text);
        if (command != null) {
            String args = AmadeusUtils.removePreifx(text, core, Arrays.asList(command.name), true);
            try {
                command.execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BaseTerminalCommand getCommand(String text) {
        for (BaseTerminalCommand command : getCommandList())
            if (text.toLowerCase().startsWith(command.name)
                    && (text.toLowerCase().replaceFirst(command.name, "").startsWith(" ")
                            || text.toLowerCase().replaceFirst(command.name, "").equals(""))) {
                return command;
            }
        return null;
    }

    @Override
    public void registerCommands() {
        registerCommand(new TerminalCommandStop(core, this));
        registerCommand(new TerminalCommandChat(core, this));
    }

    @Override
    public void registerCommand(BaseTerminalCommand command) {
        commands.put(command.name, command);
    }

    @Override
    public void answer(Object answer) {
        core.log.info(answer);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            executeCommand(readLine());
        }
    }
}
