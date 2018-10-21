package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class BaseCommand {
    public final AmadeusCore core;
    public final CommandHandler handler;
    public final String name;

    public BaseCommand(AmadeusCore core, CommandHandler handler, String name) {
        this.core = core;
        this.handler = handler;
        this.name = name;
    }

    public abstract void execute(IUser sender, IMessage source, String args) throws Exception;

    public boolean isForce(IMessage message, String args) {
        try {
            for (String arg : AmadeusUtils.parseArgsToArray(args))
                for (String checkArg : core.langs.getNamesCustom(message, "force"))
                    if (arg.toLowerCase().equals(checkArg))
                        return true;
        } catch (Exception e) {}
        try {
            for (String arg : AmadeusUtils.parseArgsToMap(args).keySet())
                for (String checkArg : core.langs.getNamesCustom(message, "force"))
                    if (arg.toLowerCase().equals(checkArg))
                        return true;
        } catch (Exception e) {}
        return false;
    }

    public boolean isYes(IMessage message, String arg) {
        for (String checkArg : core.langs.getNamesCustom(message, "yes"))
            if (arg.toLowerCase().equals(checkArg))
                return true;
        return false;
    }

    public boolean isAll(IMessage message, String arg) {
        for (String checkArg : core.langs.getNamesCustom(message, "all"))
            if (arg.toLowerCase().equals(checkArg))
                return true;
        return false;
    }

    public boolean hasArg(IMessage message, String arg, HashMap<String, String> argsParsed) {
        for (String checkArg : core.langs.getNamesArg(message, handler.name, name, arg))
            if (argsParsed.containsKey(checkArg))
                return true;
        return false;
    }

    public boolean hasArg(IMessage message, String arg, List<String> argsParsed) {
        for (String checkArg : core.langs.getNamesArg(message, handler.name, name, arg))
            if (argsParsed.contains(checkArg))
                return true;
        return false;
    }

    public boolean hasArgAt(IMessage message, String arg, int pos, List<String> argsParsed) {
        if (pos >= argsParsed.size())
            return false;
        for (String checkArg : core.langs.getNamesArg(message, handler.name, name, arg))
            if (argsParsed.get(pos).equals(checkArg))
                return true;
        return false;
    }

    public String argValue(IMessage message, String arg, HashMap<String, String> argsParsed) {
        for (String checkArg : core.langs.getNamesArg(message, handler.name, name, arg))
            if (argsParsed.containsKey(checkArg))
                return argsParsed.get(checkArg);
        return null;
    }

    public int argIndex(IMessage message, String arg, List<String> argsParsed) {
        for (String checkArg : core.langs.getNamesArg(message, handler.name, name, arg))
            if (argsParsed.contains(checkArg))
                return argsParsed.indexOf(checkArg);
        return -1;
    }

    public String argValue(IMessage message, String arg, List<String> argsParsed) {
        int index = argIndex(message, arg, argsParsed);
        if (index >= 0 && argsParsed.size() > index)
            return argsParsed.get(index + 1);
        return null;
    }

    public String getHelpDesc(IMessage message) {
        return core.langs.translate(message, "help." + name);
    }

    public String getHelpDesc(IMessage message, String args) {
        String value = core.langs.translate(message, "help.noarg");
        if (getArgsList().contains(args))
            value = core.langs.translate(message, "help." + name + "." + args);
        return value;
    }

    public List<String> getArgsList() {
        return Collections.emptyList();
    }

    public List<String> getPermissions() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(getCommandPermission());
        if (getArgsList().size() > 0) {
            result.add(getAllArgsPermission());
            for (String arg : getArgsList())
                result.add(getArgPermission(arg));
        }
        return result;
    }

    public String getCommandPermission() {
        return handler.name + "." + name;
    }

    public String getArgPermission(String arg) {
        return handler.name + "." + name + "." + arg;
    }

    public String getAllArgsPermission() {
        return handler.name + "." + name + ".*";
    }

    public boolean onlyOwner() {
        return false;
    }
}
