package mjaroslav.bots.core.amadeus.commands;

import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class BaseCommandDialogYesNo extends BaseCommand {
    public BaseCommandDialogYesNo(AmadeusCore core, CommandHandler handler) {
        super(core, handler);
    }

    private final HashMap<Long, String> cache = new HashMap<Long, String>();

    @Override
    public final void execute(IUser sender, IMessage source, String args) throws Exception {
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (argsParsed.isEmpty()) {
            cache.put(source.getAuthor().getLongID(), args);
            answerWarn(source, "Are you sure? Call this command with \"true/false\" arg");
        } else if (argsParsed.get(0).toLowerCase().equals("true")) {
            if (cache.containsKey(source.getAuthor().getLongID())) {
                String temp = cache.get(source.getAuthor().getLongID());
                cache.remove(source.getAuthor().getLongID());
                executeYes(sender, source, temp);
            } else
                answerError(source, "You don't call this command");
        } else {
            if (cache.containsKey(source.getAuthor().getLongID())) {
                String temp = cache.get(source.getAuthor().getLongID());
                cache.remove(source.getAuthor().getLongID());
                executeNo(sender, source, temp);
            } else
                answerError(source, "You don't call this command");
        }
    }

    public abstract void executeYes(IUser sender, IMessage source, String args) throws Exception;

    public abstract void executeNo(IUser sender, IMessage source, String args) throws Exception;
}
