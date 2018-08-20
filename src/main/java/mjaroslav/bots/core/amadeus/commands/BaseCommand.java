package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class BaseCommand {
    public final AmadeusCore core;
    public final CommandHandler handler;

    public BaseCommand(AmadeusCore core, CommandHandler handler) {
        this.core = core;
        this.handler = handler;
    }

    public abstract String getName();

    public abstract void execute(IUser sender, IMessage source, String args) throws Exception;

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
        return "<Not found>";
    }
}
