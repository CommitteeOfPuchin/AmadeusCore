package mjaroslav.bots.core.amadeus.commands;

import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class BaseCommandDialogSure extends BaseCommand {
    public BaseCommandDialogSure(AmadeusCore core, CommandHandler handler, String name) {
        super(core, handler, name);
    }

    private final HashMap<Long, String> cache = new HashMap<Long, String>();

    @Override
    public final void execute(IUser sender, IMessage source, String args) throws Exception {
        if (!cache.containsKey(source.getAuthor().getLongID())) {
            if (isForce(source, args))
                executeConfirmed(sender, source, args);
            else {
                cache.put(source.getAuthor().getLongID(), args);
                core.sendWarn(source, core.langs.translate(source, "answer_sure"));
            }
            return;
        }
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (isYes(source, argsParsed.get(0))) {
            if (cache.containsKey(source.getAuthor().getLongID())) {
                String temp = cache.get(source.getAuthor().getLongID());
                cache.remove(source.getAuthor().getLongID());
                executeConfirmed(sender, source, temp);
            } else
                core.sendError(source, core.langs.translate(source, "answer_sure_noneed"));
        } else {
            if (cache.containsKey(source.getAuthor().getLongID())) {
                cache.remove(source.getAuthor().getLongID());
                core.sendCanceled(source);
            } else
                core.sendError(source, core.langs.translate(source, "answer_sure_noneed"));
        }
    }

    public abstract void executeConfirmed(IUser sender, IMessage source, String args) throws Exception;
}
