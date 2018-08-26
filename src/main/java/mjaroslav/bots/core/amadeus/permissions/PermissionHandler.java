package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.commands.CommandHandler;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class PermissionHandler {
    public final AmadeusCore core;
    public final CommandHandler handler;

    public PermissionHandler(AmadeusCore core, CommandHandler handler) {
        this.core = core;
        this.handler = handler;
    }

    public abstract void loadPermissions();

    public abstract String getDefault();

    public abstract String getAdmin();

    public abstract String getOwner();

    public abstract List<String> get(String name);

    public abstract boolean hasPermission(IUser sender, IMessage source, String name);

    public abstract boolean canUseCommand(IUser sender, IMessage source, BaseCommand command, String arg);

    public abstract boolean canUse(IUser sender, IMessage source, String perm);
    
    public abstract List<String> getUserPermissions(IUser sender, IMessage source);
    
    public File getFolder() {
        return core.getFolder().toPath().resolve("permissions/" + handler.name).toFile();
    }
}
