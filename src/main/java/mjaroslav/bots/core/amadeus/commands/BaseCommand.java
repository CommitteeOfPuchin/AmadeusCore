package mjaroslav.bots.core.amadeus.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
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

    public boolean hasArg(String arg, List<String> argsParsed) {
        for (String checkArg : handler.getNameHandler().getArgNames(name, arg))
            if (argsParsed.contains(checkArg))
                return true;
        return false;
    }

    public boolean hasArg(String arg, HashMap<String, String> argsParsed) {
        for (String checkArg : handler.getNameHandler().getArgNames(name, arg))
            if (argsParsed.containsKey(checkArg))
                return true;
        return false;
    }

    public String argValue(String arg, HashMap<String, String> argsParsed) {
        for (String checkArg : handler.getNameHandler().getArgNames(name, arg))
            if (argsParsed.containsKey(checkArg))
                return argsParsed.get(checkArg);
        return null;
    }

    public int argIndex(String arg, List<String> argsParsed) {
        for (String checkArg : handler.getNameHandler().getArgNames(name, arg))
            if (argsParsed.contains(checkArg))
                return argsParsed.indexOf(checkArg);
        return -1;
    }

    public String argValue(String arg, List<String> argsParsed) {
        int index = argIndex(arg, argsParsed);
        if (index >= 0 && argsParsed.size() > index)
            return argsParsed.get(index + 1);
        return null;
    }

    public void answer(IMessage source, String message, EmbedObject embed) {
        long to = source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID();
        core.sendMessage(to, message, embed);
    }

    public void answerDone(IMessage source, String message) {
        long to = source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID();
        core.sendDone(to, message);
    }

    public void answerError(IMessage source, String message) {
        long to = source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID();
        core.sendError(to, message);
    }

    public void answerWarn(IMessage source, String message) {
        long to = source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID();
        core.sendWarn(to, message);
    }

    public String getHelpDesc() {
        return core.translate("help." + name);
    }

    public String getHelpDesc(String args) {
        String value = core.translate("help.noarg");
        if (getArgsList().contains(args))
            value = core.translate("help." + name + "." + args);
        return value;
    }

    public List<String> getArgsList() {
        return Collections.emptyList();
    }

    public String getPermissions() {
        return handler.hasPermissionHandller() ? handler.getPermissionHandler().getDefault() : "";
    }

    public String getPermissions(String arg) {
        return getPermissions();
    }
}
