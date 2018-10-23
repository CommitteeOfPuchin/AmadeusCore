package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class PermissionHandler {
    public final AmadeusCore core;
    public final AbstractDatabase database;

    private final HashMap<Long, HashMap<Long, String>> USERS = new HashMap<>();
    private final HashMap<Long, HashMap<Long, String>> ROLES = new HashMap<>();
    private final HashMap<Long, HashMap<String, String>> PERMISSIONS = new HashMap<>();
    private final HashMap<Long, PermissionInfo> PERMISSIONROLES = new HashMap<>();
    private final HashMap<Long, String> USERS_PRIVATE = new HashMap<>();
    private final PermissionInfo PERMISSIONROLES_PRIVATE;

    public final File FOLDER;

    public PermissionHandler(AmadeusCore core) {
        this.core = core;
        PERMISSIONROLES_PRIVATE = new PermissionInfo(core.getDefaultPermissionsPrivate());
        FOLDER = FileHelper.folderPermissions(core).toFile();
        database = core.databases.getDatabaseOrAddSQLite("permissions", FileHelper.filePermissionsDatabase(core));
    }

    public void load() {
        PERMISSIONROLES.clear();
        for (IGuild guild : core.client.getGuilds()) {
            PERMISSIONROLES.put(guild.getLongID(), new PermissionInfo(core.getDefaultPermissions()));
            PERMISSIONROLES.get(guild.getLongID()).createFile(FileHelper.filePermissionsRole(core, guild.getLongID()));
        }
        PERMISSIONROLES_PRIVATE.createFile(FileHelper.filePermissionsRolePrivate(core));
        if (AmadeusUtils.existsOrCreateFolder(FOLDER))
            for (File file : FOLDER.listFiles(FileHelper.ROLEEXTFILTER))
                try {
                    if (FilenameUtils.removeExtension(file.getName()).equals("private_messages"))
                        PERMISSIONROLES_PRIVATE.set(JSONUtils.fromJson(file, PermissionInfo.class));
                    else if (PERMISSIONROLES.containsKey(Long.parseLong(FilenameUtils.removeExtension(file.getName()))))
                        PERMISSIONROLES.put(Long.parseLong(FilenameUtils.removeExtension(file.getName())),
                                JSONUtils.fromJson(file, PermissionInfo.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        loadDatabase();
    }

    public void loadDatabase() {
        ROLES.clear();
        USERS.clear();
        PERMISSIONS.clear();
        USERS_PRIVATE.clear();
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS permissions(guildId INTEGER, permissionName VARCHAR, permissionRole VARCHAR)");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS roles(guildId INTEGER, roleId INTEGER, permissionRole VARCHAR)");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users(guildId INTEGER, userId INTEGER, permissionRole VARCHAR)");
        database.executeUpdate("CREATE TABLE IF NOT EXISTS usersPrivate(userId INTEGER, permissionRole VARCHAR)");
        try {
            ResultSet result = database.executeQuery("SELECT guildId, permissionName, permissionRole FROM permissions");
            while (result.next()) {
                if (!PERMISSIONS.containsKey(result.getLong("guildId")))
                    PERMISSIONS.put(result.getLong("guildId"), new HashMap<>());
                PERMISSIONS.get(result.getLong("guildId")).put(result.getString("permissionName"),
                        result.getString("permissionRole"));
            }
            result = database.executeQuery("SELECT guildId, roleId, permissionRole FROM roles");
            while (result.next()) {
                if (!ROLES.containsKey(result.getLong("guildId")))
                    ROLES.put(result.getLong("guildId"), new HashMap<>());
                ROLES.get(result.getLong("guildId")).put(result.getLong("roleId"), result.getString("permissionRole"));
            }
            result = database.executeQuery("SELECT guildId, userId, permissionRole FROM users");
            while (result.next()) {
                if (!USERS.containsKey(result.getLong("guildId")))
                    USERS.put(result.getLong("guildId"), new HashMap<>());
                USERS.get(result.getLong("guildId")).put(result.getLong("userId"), result.getString("permissionRole"));
            }
            result = database.executeQuery("SELECT userId, permissionRole FROM usersPrivate");
            while (result.next())
                USERS_PRIVATE.put(result.getLong("userId"), result.getString("permissionRole"));
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public HashSet<String> getPermissionsFromPermissionRole(IGuild guild, String role) {
        if (guild != null)
            return PERMISSIONROLES.get(guild.getLongID()).getPermission(role);
        else
            return PERMISSIONROLES_PRIVATE.getPermission(role);
    }

    public String getPermissionRoleFromRole(IRole role) {
        if (role != null && ROLES.containsKey(role.getGuild().getLongID()))
            return ROLES.get(role.getGuild().getLongID()).getOrDefault(role.getLongID(), "default");
        else
            return "default";
    }

    public String getPermissionRoleFromPermission(IGuild guild, Permissions permission) {
        if (guild != null && PERMISSIONS.containsKey(guild.getLongID()))
            return PERMISSIONS.get(guild.getLongID()).getOrDefault(permission.name(), "default");
        else
            return "default";
    }

    public String getPermissionRoleFromUser(IGuild guild, IUser user) {
        if (guild != null && USERS.containsKey(guild.getLongID()))
            return USERS.get(guild.getLongID()).getOrDefault(user.getLongID(), "default");
        else
            return USERS_PRIVATE.getOrDefault(user.getLongID(), "default");
    }

    public HashSet<String> getPermissions(IGuild guild, IUser user) {
        HashSet<String> result = new HashSet<>();
        result.addAll(getPermissionsFromPermissionRole(guild, getPermissionRoleFromUser(guild, user)));
        for (IRole role : user.getRolesForGuild(guild))
            result.addAll(getPermissionsFromPermissionRole(guild, getPermissionRoleFromRole(role)));
        for (Permissions permission : user.getPermissionsForGuild(guild))
            result.addAll(getPermissionsFromPermissionRole(guild, getPermissionRoleFromPermission(guild, permission)));
        return result;
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
        if (core.optionDevMode)
            for (long id : core.info.getDevIds())
                if (id == user.getLongID())
                    return true;
        if (command.onlyOwner())
            return false;
        return AmadeusUtils.stringIsEmpty(arg) ? canUseCommand(guild, user, command.getCommandPermission())
                : canUseCommand(guild, user, command.getArgPermission(arg));
    }
}
