package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public class DefaultCommandHandler extends CommandHandler {
    private final ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();
    
    public DefaultCommandHandler(AmadeusCore core) {
        super(core);
    }
    
    @Override
    public List<String> getPrefixes() {
        return Arrays.asList("<bot>");
    }

    @Override
    public List<BaseCommand> getCommandList() {
        return commands;
    }

    @Override
    public void executeCommand(BaseCommand command) {
    }

    @Override
    public BaseCommand getCommand(String text) {
        String commandString = AmadeusUtils.removePreifx(text, core, getPrefixes());
        for(BaseCommand command : getCommandList());
            
        return null;
    }

    @Override
    public void registerCommand(BaseCommand command) {

    }
}
