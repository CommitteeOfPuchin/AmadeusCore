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
import sx.blah.discord.handle.obj.IUser;

public class PrivatePermissionHandler {
    public final AmadeusCore core;
    public final AbstractDatabase database;

    private final HashMap<Long, String> USERS = new HashMap<>();
    private final HashMap<String, PermissionRole> PERMISSIONROLES = new HashMap<>();

    public final File FOLDER;
    public final File DEFAULT;
    public final File ADMIN;

    private final PermissionRole ROLEDEFAULT = new PermissionRole(null,
            Arrays.asList("default.help", "default.status", "default.permissions"));
    private final PermissionRole ROLEADMIN = new PermissionRole("default", Arrays.asList("default.permissions.*"));

    public PrivatePermissionHandler(AmadeusCore core) {
        this.core = core;
        database = core.databases.getDatabaseOrAddSQLite("permissions:private",
                FileHelper.filePermissionsDatabasePrivate(core));
        FOLDER = FileHelper.folderPermissionsPrivate(core).toFile();
        DEFAULT = FileHelper.filePermissionsRolePrivate(core, "default");
        ADMIN = FileHelper.filePermissionsRolePrivate(core, "admin");
    }

    private Action onCreate = new Action() {
        @Override
        public boolean done() {
            if (AmadeusUtils.existsOrCreateFile(DEFAULT))
                try {
                    JSONUtils.toJson(DEFAULT, ROLEDEFAULT, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (AmadeusUtils.existsOrCreateFile(ADMIN))
                try {
                    JSONUtils.toJson(ADMIN, ROLEADMIN, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return true;
        }
    };

    public void load() {
        PERMISSIONROLES.clear();
        if (AmadeusUtils.existsOrCreateFile(FOLDER, onCreate, null))
            for (File file : FOLDER.listFiles(FileHelper.ROLEEXTFILTER)) {
                String name = FilenameUtils.removeExtension(file.getName());
                try {
                    PERMISSIONROLES.put(name, JSONUtils.fromJson(file, PermissionRole.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void loadDatabase() {
        USERS.clear();
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users(userId INTEGER UNIQUE, permissionRole VARCHAR DEFAULT 'default', comment VARCHAR(30))");
        try {
            ResultSet result = database.executeQuery("SELECT userId, permissionRole FROM users");
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

    public String getPermissionRoleFromUser(long userId) {
        return USERS.getOrDefault(userId, "default");
    }

    public HashSet<String> getPermissions(IUser user) {
        HashSet<String> result = new HashSet<>();
        result.addAll(getPermissionsFromPermissionRole(getPermissionRoleFromUser(user.getLongID())));
        return result;
    }
}
