package mjaroslav.bots.core.amadeus.permissions;

import java.util.HashMap;
import java.util.HashSet;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class PermissionHandler {
    public final AmadeusCore core;

    private final HashMap<Long, GuildPermissionHandler> STORAGE = new HashMap<>();
    private final PrivatePermissionHandler PRIVATE;

    public PermissionHandler(AmadeusCore core) {
        this.core = core;
        PRIVATE = new PrivatePermissionHandler(core);
    }

    public void load() {
        STORAGE.clear();
        for (IGuild guild : core.getClient().getGuilds()) {
            STORAGE.put(guild.getLongID(), new GuildPermissionHandler(core, guild.getLongID()));
            STORAGE.get(guild.getLongID()).load();
        }
    }

    public HashSet<String> getPermissions(IGuild guild, IUser user) {
        HashSet<String> permissions = new HashSet<>();
        if (guild == null)
            permissions.addAll(PRIVATE.getPermissions(user));
        else {
            GuildPermissionHandler handler = STORAGE.get(guild.getLongID());
            if (handler != null)
                permissions.addAll(handler.getPermissions(guild, user));
        }
        return permissions;
    }

    public boolean canUseCommand(IGuild guild, IUser user, String permission) {
        HashSet<String> permissions = getPermissions(guild, user);
        String[] info = permission.split(".");
        if (permissions.contains("*"))
            return true;
        else if (info.length == 2) {
            if (permissions.contains(info[0] + ".*"))
                return true;
        } else if (info.length == 3) {
            if (permissions.contains(info[0] + ".*"))
                return true;
            if (permissions.contains(info[0] + "." + info[1] + ".*"))
                return true;
        }
        return permissions.contains(permission);
    }

    public boolean canUseCommand(IGuild guild, IUser user, BaseCommand command) {
        return canUseCommand(guild, user, command, "");
    }

    public boolean canUseCommand(IGuild guild, IUser user, BaseCommand command, String arg) {
        if (core.devMode)
            for (long id : core.info.getDevIds())
                if (id == user.getLongID())
                    return true;
        if (command.onlyOwner())
            return false;
        return AmadeusUtils.stringIsEmpty(arg) ? canUseCommand(guild, user, command.getCommandPermission())
                : canUseCommand(guild, user, command.getArgPermission(arg));
    }
}
