package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.database.DatabaseHandler;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.JSONReader;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class DefaultPermissionHandler extends PermissionHandler {
    private DatabaseHandler handler = core.getDatabaseHandler("default");
    public final JSONReader<PermissionRole[]> readerRoles = new JSONReader<>(new PermissionRole[] {}, getRoleFile(),
            true);
    private final HashMap<String, String> pperms = new HashMap<String, String>();
    private final HashMap<Long, String> proles = new HashMap<Long, String>();
    private final HashMap<Long, String> pusers = new HashMap<Long, String>();
    private final HashMap<Long, String> pguilds = new HashMap<Long, String>();

    public DefaultPermissionHandler(AmadeusCore core) {
        super(core);
        if (!handler.isReady())
            core.setPermissionHandler(new DefaultPermissionHandler(core));
        else {
            PermissionRole defaultPermissions = new PermissionRole("default",
                    Arrays.asList("default.help.*", "default.status", "default.permsinfo"));
            PermissionRole adminPermissions = new PermissionRole("admin", "default",
                    Arrays.asList("default.permsinfo.*", "default.reload.*"));
            readerRoles.defaults = new PermissionRole[] {defaultPermissions, adminPermissions};
            readerRoles.init();
        }
    }

    @Override
    public void loadPermissions() {
        try {
            readerRoles.read();
            pperms.clear();
            proles.clear();
            pusers.clear();
            pguilds.clear();
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pperms(permName VARCHAR UNIQUE, prole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS proles(roleId INTEGER UNIQUE, prole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pusers(userId INTEGER UNIQUE, prole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pguilds(guildId INTEGER UNIQUE, prole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            ResultSet result = handler.executeQuery("SELECT permName, prole FROM pperms;");
            while (result.next())
                pperms.put(result.getString("permName"), result.getString("prole"));
            result = handler.executeQuery("SELECT roleId, prole FROM proles;");
            while (result.next())
                proles.put(result.getLong("roleId"), result.getString("prole"));
            result = handler.executeQuery("SELECT userId, prole FROM pusers;");
            while (result.next())
                pusers.put(result.getLong("userId"), result.getString("prole"));
            result = handler.executeQuery("SELECT guildId, prole FROM pguilds;");
            while (result.next())
                pguilds.put(result.getLong("userId"), result.getString("prole"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getPermsByPermRole(String roleName) {
        HashSet<String> result = new HashSet<>();
        for (PermissionRole role : readerRoles.json)
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

    public File getRoleFile() {
        return getFolder().toPath().resolve("permission_roles.json").toFile();
    }
}
