package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.database.DatabaseHandler;
import mjaroslav.bots.core.amadeus.lib.References;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class PermissionHandler {
    public static final AbstractDatabase database = DatabaseHandler.getDatabaseOrAddSQLite("permissions");
    public static final File FOLDER = new File(References.FOLDER_PERMISSIONS);
    private static final HashMap<Long, PermissionRole[]> STORAGE = new HashMap<>();
    private static final HashMap<String, String> PERMISSIONS = new HashMap<>();
    private static final HashMap<Long, String> ROLES = new HashMap<>();
    private static final HashMap<Long, String> USERS = new HashMap<>();
    private static final HashMap<Long, String> GUILDS = new HashMap<>();

    private static final PermissionRole[] DEFAULT_ROLES = new PermissionRole[] {
            new PermissionRole("default", Arrays.asList("default.help", "default.status", "default.permsinfo")),
            new PermissionRole("admin", "default", Arrays.asList("default.reload.*", "default.permsinfo.*"))};

    public static void loadPermissionRoles() {
        if (folderExists())
            for (File file : FOLDER.listFiles(JSONUtils.JSONEXTFILTER)) {
                try {
                    long id = Long.parseLong(FilenameUtils.removeExtension(file.getName()));
                    STORAGE.put(id, JSONUtils.fromJson(file, PermissionRole[].class));
                } catch (Exception e) {
                    continue;
                }
            }
    }

    public static void loadDatabase() {
        PERMISSIONS.clear();
        ROLES.clear();
        USERS.clear();
        GUILDS.clear();
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS permissions(permissionName VARCHAR UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS roles(roleId INTEGER UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users(userId INTEGER UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS guilds(guildId INTEGER UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        try {
            ResultSet result = database.executeQuery("SELECT permissionName, permissionRole FROM permissions");
            while (result.next())
                PERMISSIONS.put(result.getString("permissionsName"), result.getString("permissionRole"));
            result = database.executeQuery("SELECT roleId, permissionsRole FROM roles");
            while (result.next())
                ROLES.put(result.getLong("roleId"), result.getString("permissionsRole"));
            result = database.executeQuery("SELECT userId, permissionRole FROM users");
            while (result.next())
                USERS.put(result.getLong("userId"), result.getString("permissionRole"));
            result = database.executeQuery("SELECT guildId, permissionsRole FROM guilds");
            while (result.next())
                GUILDS.put(result.getLong("userId"), result.getString("permissionRole"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPermsByPermRole(String roleName) {
        HashSet<String> result = new HashSet<>();
        for (PermissionRole role : STORAGE.entrt)
            if (role.name.equals(roleName)) {
                result.addAll(role.permissions);
                result.addAll(getPermsByPermRole(role.parent));
            }
        return Arrays.asList(result.toArray(new String[] {}));
    }

    @Override
    public List<String> getPermsByPerms(Permissions permission) {
        return getPermsByPermRole(pperms.containsKey(permission.name()) ? pperms.get(permission.name()) : "default");
    }

    @Override
    public List<String> getPermsByRole(long roleId) {
        return getPermsByPermRole(proles.containsKey(roleId) ? proles.get(roleId) : "default");
    }

    @Override
    public List<String> getPermsByUser(long userId) {
        return getPermsByPermRole(pusers.containsKey(userId) ? pusers.get(userId) : "default");
    }

    @Override
    public List<String> getPermsByGuild(long guildId) {
        return getPermsByPermRole(pguilds.containsKey(guildId) ? pguilds.get(guildId) : "default");
    }

    @Override
    public List<String> getAllPermissions(IUser sender, IMessage source) {
        HashSet<String> permissions = new HashSet<>(getPermsByUser(sender.getLongID()));
        if (source.getChannel().getGuild() != null) {
            permissions.addAll(getPermsByGuild(source.getChannel().getGuild().getLongID()));
            for (IRole role : sender.getRolesForGuild(source.getChannel().getGuild()))
                permissions.addAll(getPermsByRole(role.getLongID()));
            for (Permissions perm : sender.getPermissionsForGuild(source.getChannel().getGuild()))
                permissions.addAll(getPermsByPerms(perm));
        }
        return new ArrayList<String>(permissions);
    }

    @Override
    public boolean hasPermission(IUser sender, IMessage source, String permission) {
        if (core.devMode)
            for (long id : core.info.getDevIds())
                if (id == sender.getLongID())
                    return true;
        return hasPermission(getAllPermissions(sender, source), permission);
    }

    private boolean hasPermission(List<String> permissions, String permission) {
        if (permissions.contains("*"))
            return true;
        String[] info = permission.split("\\.");
        if (info.length == 2
                && (permissions.contains(info[0] + ".*") || permissions.contains(info[0] + "." + info[1] + ".*")))
            return true;
        return permissions.contains(permission);
    }

    @Override
    public boolean canUseCommand(IUser sender, IMessage source, BaseCommand command, String arg) {
        return hasPermission(sender, source,
                AmadeusUtils.stringIsNotEmpty(arg) ? command.getArgPermission(arg) : command.getCommandPermission());
    }
    
    public static File getPersmissionsFile(long guildId) {
        return FOLDER.toPath().resolve(String.format(References.PATTERN_FILE_LANG, guildId)).toFile();
    }

    public static boolean folderExists() {
        if ((FOLDER.exists() && FOLDER.isDirectory()) || FOLDER.mkdirs())
            return true;
        return false;
    }
}
