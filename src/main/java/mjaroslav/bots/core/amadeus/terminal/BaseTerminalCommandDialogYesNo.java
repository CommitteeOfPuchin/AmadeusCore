package mjaroslav.bots.core.amadeus.terminal;

import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public abstract class BaseTerminalCommandDialogYesNo extends BaseTerminalCommand {

    public BaseTerminalCommandDialogYesNo(AmadeusCore core, TerminalCommandHandler handler, String name) {
        super(core, handler, name);
    }

    private String cache = null;

    @Override
    public final void execute(String args) throws Exception {
        if (AmadeusUtils.stringIsEmpty(cache)) {
            if (isForce(args))
                executeYes(args);
            else {
                cache = args;
                answer("You sure?");
            }
            return;
        }
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (isYes(argsParsed.get(0))) {
            if (AmadeusUtils.stringIsNotEmpty(cache)) {
                String temp = cache;
                cache = null;
                executeYes(temp);
            } else
                answer("First run the command!");
        } else {
            if (AmadeusUtils.stringIsNotEmpty(cache)) {
                String temp = cache;
                cache = null;
                executeNo(temp);
            } else
                answer("First run the command!");
        }
    }

    public abstract void executeYes(String args) throws Exception;

    public abstract void executeNo(String args) throws Exception;
}
