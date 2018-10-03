package mjaroslav.bots.core.amadeus.terminal;

import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public abstract class BaseTerminalCommand {
    public final AmadeusCore core;
    public final TerminalCommandHandler handler;
    public final String name;

    public BaseTerminalCommand(AmadeusCore core, TerminalCommandHandler handler, String name) {
        this.core = core;
        this.handler = handler;
        this.name = name;
    }

    public abstract void execute(String args) throws Exception;

    public boolean isForce(String args) {
        try {
            return hasArg("force", AmadeusUtils.parseArgsToArray(args));
        } catch (Exception e) {
        }
        try {
            return hasArg("force", AmadeusUtils.parseArgsToMap(args));
        } catch (Exception e) {
        }
        return false;
    }

    public boolean isYes(String arg) {
        return arg.toLowerCase().equals("true");
    }

    public boolean isAll(String arg) {
        return arg.toLowerCase().equals("all");
    }

    public boolean hasArg(String arg, List<String> argsParsed) {
        return argsParsed.contains(arg);
    }

    public boolean hasArg(String arg, HashMap<String, String> argsParsed) {
        return argsParsed.containsKey(arg);
    }

    public String argValue(String arg, HashMap<String, String> argsParsed) {
        if (argsParsed.containsKey(arg))
            return argsParsed.get(arg);
        return null;
    }

    public int argIndex(String arg, List<String> argsParsed) {
        if (argsParsed.contains(arg))
            return argsParsed.indexOf(arg);
        return -1;
    }

    public String argValue(String arg, List<String> argsParsed) {
        int index = argIndex(arg, argsParsed);
        if (index >= 0 && argsParsed.size() > index)
            return argsParsed.get(index + 1);
        return null;
    }
    
    public void answer(Object answer) {
        handler.answer(answer);
    }
}
