package mjaroslav.bots.core.amadeus.commands;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class BaseCommand {
    public final AmadeusCore core;

    public BaseCommand(AmadeusCore core) {
        this.core = core;
    }

    public abstract String getName();

    public abstract void execute(IUser sender, IMessage source, String args) throws Exception;

    public static void sendDone(IMessage source, String message) {

    }

    public static void sendError(IMessage source, String message) {

    }

    public static void sendWarn(IMessage source, String message) {

    }
}
