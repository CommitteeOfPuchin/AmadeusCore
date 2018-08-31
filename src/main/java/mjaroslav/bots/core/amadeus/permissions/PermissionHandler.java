package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class PermissionHandler {
    public final AmadeusCore core;

    public PermissionHandler(AmadeusCore core) {
        this.core = core;
    }

    public abstract void loadPermissions();

    public abstract List<String> getRolePermissions(String roleName);

    public abstract boolean hasPermission(IUser sender, IMessage source, String permission);

    public abstract boolean canUseCommand(IUser sender, IMessage source, BaseCommand command, String arg);

    public abstract List<String> getUserPermissions(IUser sender, IMessage source);

    public File getFolder() {
        return core.info.getFolder().toPath().resolve("permissions").toFile();
    }
}
