package mjaroslav.bots.core.amadeus.permissions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.database.DatabaseHandler;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class DefaultSQLitePermissionHandler extends PermissionHandler {
    private DatabaseHandler handler;
    private final HashMap<String, String> byPerms = new HashMap<String, String>();
    private final HashMap<Long, String> byRoles = new HashMap<Long, String>();
    private final HashMap<Long, String> byUsers = new HashMap<Long, String>();

    public DefaultSQLitePermissionHandler(AmadeusCore core) {
        super(core);
        handler = core.getDatabaseHandler("default");
        if (!handler.isReady())
            core.setPermissionHandler(new DefaultPermissionHandler(core));
    }

    @Override
    public void loadPermissions() {
        try {
            byPerms.clear();
            byRoles.clear();
            byUsers.clear();
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS byPerms(permName VARCHAR UNIQUE, role VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS byRoles(roleId INTEGER UNIQUE, role VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            handler.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS byUsers(userId INTEGER UNIQUE, role VARCHAR DEFAULT 'default', comment VARCHAR(30))");
            ResultSet result = handler.executeQuery("SELECT permName, role FROM byPerms;");
            while (result.next())
                byPerms.put(result.getString("permName"), result.getString("role"));
            result = handler.executeQuery("SELECT roleId, role FROM byRoles;");
            while (result.next())
                byRoles.put(result.getLong("roleId"), result.getString("role"));
            result = handler.executeQuery("SELECT userId, role FROM byUsers;");
            while (result.next())
                byUsers.put(result.getLong("userId"), result.getString("role"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getRolePermissions(String roleName) {
        return null;
    }

    @Override
    public boolean hasPermission(IUser sender, IMessage source, String permission) {
        return false;
    }

    @Override
    public boolean canUseCommand(IUser sender, IMessage source, BaseCommand command, String arg) {
        return false;
    }

    @Override
    public List<String> getUserPermissions(IUser sender, IMessage source) {
        return null;
    }
}
