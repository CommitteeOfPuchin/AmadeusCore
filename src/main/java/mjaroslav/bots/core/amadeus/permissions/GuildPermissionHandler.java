package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils.Action;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class GuildPermissionHandler {
    public final AmadeusCore core;
    public final long guildId;
    public final AbstractDatabase database;

    private final HashMap<Long, String> USERS = new HashMap<>();
    private final HashMap<Long, String> ROLES = new HashMap<>();
    private final HashMap<String, String> PERMISSIONS = new HashMap<>();
    private final HashMap<String, PermissionRole> PERMISSIONROLES = new HashMap<>();

    public final File FOLDER;
    public final File DEFAULT;
    public final File ADMIN;

    private final PermissionRole ROLEDEFAULT = new PermissionRole(null,
            Arrays.asList("default.help", "default.status", "default.permissions"));
    private final PermissionRole ROLEADMIN = new PermissionRole("default", Arrays.asList("default.permissions.*"));

    public GuildPermissionHandler(AmadeusCore core, long guildId) {
        this.core = core;
        this.guildId = guildId;
        database = core.databases.getDatabaseOrAddSQLite("permissions:" + guildId,
                FileHelper.filePermissionsDatabase(core, guildId));
        FOLDER = FileHelper.folderPermissionsGuild(core, guildId).toFile();
        DEFAULT = FileHelper.filePermissionsRole(core, guildId, "default");
        ADMIN = FileHelper.filePermissionsRole(core, guildId, "admin");
    }

    public void load() {
        PERMISSIONROLES.clear();
        if (AmadeusUtils.existsOrCreateFolder(FOLDER)) {
            if (AmadeusUtils.existsOrCreateFile(DEFAULT, new Action() {
                @Override
                public boolean done() {
                    try {
                        JSONUtils.toJson(DEFAULT, ROLEDEFAULT, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }, null))
                ;
            if (AmadeusUtils.existsOrCreateFile(ADMIN, new Action() {
                @Override
                public boolean done() {
                    try {
                        JSONUtils.toJson(ADMIN, ROLEADMIN, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }, null))
                ;
            for (File file : FOLDER.listFiles(FileHelper.ROLEEXTFILTER)) {
                String name = FilenameUtils.removeExtension(file.getName());
                try {
                    PERMISSIONROLES.put(name, JSONUtils.fromJson(file, PermissionRole.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        loadDatabase();
    }

    public void loadDatabase() {
        ROLES.clear();
        USERS.clear();
        PERMISSIONS.clear();
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS permissions(permissionName VARCHAR UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS roles(roleId INTEGER UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users(userId INTEGER UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        try {
            ResultSet result = database.executeQuery("SELECT permissionName, permissionRole FROM permissions");
            while (result.next()) {
                PERMISSIONS.put(result.getString("permissionName"), result.getString("permissionRole"));
            }
            result = database.executeQuery("SELECT roleId, permissionRole FROM roles");
            while (result.next()) {
                ROLES.put(result.getLong("roleId"), result.getString("permissionRole"));
            }
            result = database.executeQuery("SELECT userId, permissionRole FROM users");
            while (result.next()) {
                USERS.put(result.getLong("userId"), result.getString("permissionRole"));
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public HashSet<String> getPermissionsFromPermissionRole(String role) {
        HashSet<String> result = new HashSet<>();
        result.addAll(PERMISSIONROLES.get("default").permissions);
        if (AmadeusUtils.stringIsNotEmpty(role) && PERMISSIONROLES.containsKey(role)) {
            result.addAll(PERMISSIONROLES.get(role).permissions);
            result.addAll(getPermissionsFromPermissionRole(PERMISSIONROLES.get(role).parent));
        }
        return result;
    }

    public String getPermissionRoleFromRole(long roleId) {
        return ROLES.getOrDefault(roleId, "default");
    }

    public String getPermissionRoleFromPermission(Permissions permission) {
        return PERMISSIONS.getOrDefault(permission.name(), "default");
    }

    public String getPermissionRoleFromUser(long userId) {
        return USERS.getOrDefault(userId, "default");
    }

    public HashSet<String> getPermissions(IGuild guild, IUser user) {
        HashSet<String> result = new HashSet<>();
        result.addAll(getPermissionsFromPermissionRole(getPermissionRoleFromUser(user.getLongID())));
        for (IRole role : user.getRolesForGuild(guild))
            result.addAll(getPermissionsFromPermissionRole(getPermissionRoleFromRole(role.getLongID())));
        for (Permissions permission : user.getPermissionsForGuild(guild))
            result.addAll(getPermissionsFromPermissionRole(getPermissionRoleFromPermission(permission)));
        return result;
    }
}
