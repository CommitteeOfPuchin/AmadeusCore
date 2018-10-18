package mjaroslav.bots.core.amadeus.commands;

import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class BaseCommandDialogYesNo extends BaseCommand {
    public BaseCommandDialogYesNo(AmadeusCore core, CommandHandler handler, String name) {
        super(core, handler, name);
    }

    private final HashMap<Long, String> cache = new HashMap<Long, String>();

    @Override
    public final void execute(IUser sender, IMessage source, String args) throws Exception {
        if (!cache.containsKey(source.getAuthor().getLongID())) {
            if (isForce(args))
                executeYes(sender, source, args);
            else {
                cache.put(source.getAuthor().getLongID(), args);
                answerWarn(source, core.translate(source.getGuild(), sender, "answer.dialogyesno"));
            }
            return;
        }
        List<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (isYes(argsParsed.get(0))) {
            if (cache.containsKey(source.getAuthor().getLongID())) {
                String temp = cache.get(source.getAuthor().getLongID());
                cache.remove(source.getAuthor().getLongID());
                executeYes(sender, source, temp);
            } else
                answerError(source, core.translate(source.getGuild(), sender, "answer.dialowyesno.noneed"));
        } else {
            if (cache.containsKey(source.getAuthor().getLongID())) {
                String temp = cache.get(source.getAuthor().getLongID());
                cache.remove(source.getAuthor().getLongID());
                executeNo(sender, source, temp);
            } else
                answerError(source, core.translate(source.getGuild(), sender, "answer.dialowyesno.noneed"));
        }
    }

    public abstract void executeYes(IUser sender, IMessage source, String args) throws Exception;

    public abstract void executeNo(IUser sender, IMessage source, String args) throws Exception;
}
