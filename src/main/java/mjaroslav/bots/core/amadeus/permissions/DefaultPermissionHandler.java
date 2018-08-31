package mjaroslav.bots.core.amadeus.permissions;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.JSONReader;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class DefaultPermissionHandler extends PermissionHandler {
    public final JSONReader<PermissionRoleJSON[]> readerList = new JSONReader<PermissionRoleJSON[]>(
            new PermissionRoleJSON[] {}, getList(), true);
    public final JSONReader<GuildPermissionsJSON[]> readerMap = new JSONReader<GuildPermissionsJSON[]>(
            new GuildPermissionsJSON[] {}, getMap(), true);

    public DefaultPermissionHandler(AmadeusCore core) {
        super(core);
        PermissionRoleJSON defaultPermissions = new PermissionRoleJSON();
        defaultPermissions.permissions.addAll(Arrays.asList("default.help.*", "default.status", "default.permsinfo"));
        defaultPermissions.name = "default";
        PermissionRoleJSON adminPermissions = new PermissionRoleJSON();
        adminPermissions.permissions.addAll(Arrays.asList("default.permsinfo.*", "default.reload.*"));
        adminPermissions.parents.add("default");
        adminPermissions.name = "admin";
        readerList.defaults = new PermissionRoleJSON[] { defaultPermissions, adminPermissions };
        GuildPermissionsJSON defaultGuild = new GuildPermissionsJSON();
        defaultGuild.defaultRole = "default";
        defaultGuild.discordPermissions.put(Permissions.ADMINISTRATOR.name(), "admin");
        defaultGuild.comment = "Default permission set";
        defaultGuild.name = "default";
        GuildPermissionsJSON pmGuild = new GuildPermissionsJSON();
        pmGuild.defaultRole = "default";
        pmGuild.comment = "PM permission set";
        pmGuild.name = "pm";
        readerMap.defaults = new GuildPermissionsJSON[] { pmGuild, defaultGuild };
        readerList.init();
        readerMap.init();
    }

    @Override
    public void loadPermissions() {
        readerList.read();
        readerMap.read();
    }

    @Override
    public List<String> getRolePermissions(String roleName) {
        ArrayList<String> result = new ArrayList<String>();
        PermissionRoleJSON main = getRole(roleName);
        if (main != null) {
            result.addAll(main.permissions);
            for (String parentName : main.parents)
                result.addAll(getRolePermissions(parentName));
        }
        return result;
    }

    @Override
    public boolean hasPermission(IUser sender, IMessage source, String permission) {
        if (core.devMode)
            for (long id : core.info.getDevIds())
                if (id == sender.getLongID())
                    return true;
        List<String> permissions = getUserPermissions(sender, source);
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

    @Override
    public List<String> getUserPermissions(IUser sender, IMessage source) {
        HashSet<String> result = new HashSet<String>();
        GuildPermissionsJSON guild = null;
        if (source.getChannel() != null) {
            guild = getGuild(String.valueOf(source.getChannel().getGuild().getLongID()));
            if (guild == null)
                guild = getGuild("default");
            EnumSet<Permissions> discordPermissions = sender.getPermissionsForGuild(source.getChannel().getGuild());
            for (Entry<String, String> entry : guild.discordPermissions.entrySet())
                if (discordPermissions.contains(Permissions.valueOf(entry.getKey())))
                    result.addAll(getRolePermissions(entry.getValue()));
            if (guild.users.containsKey(String.valueOf(sender.getLongID())))
                result.addAll(getRolePermissions(guild.users.get(String.valueOf(sender.getLongID())).role));
            for (IRole discordRole : sender.getRolesForGuild(source.getChannel().getGuild()))
                if (guild.roles.containsKey(String.valueOf(discordRole.getLongID())))
                    result.addAll(getRolePermissions(guild.users.get(String.valueOf(discordRole.getLongID())).role));
        } else
            guild = getGuild("pm");
        result.addAll(guild.permissions);
        result.addAll(getRolePermissions(guild.defaultRole));
        if (result.contains("*")) {
            result.clear();
            result.add("*");
        }
        return new ArrayList<String>(result);
    }

    public PermissionRoleJSON getRole(String name) {
        for (PermissionRoleJSON role : readerList.json)
            if (role.name.equals(name))
                return role;
        return null;
    }

    public GuildPermissionsJSON getGuild(String name) {
        for (GuildPermissionsJSON guild : readerMap.json)
            if (guild.name.equals(name))
                return guild;
        return null;
    }

    public File getList() {
        return getFolder().toPath().resolve("list.json").toFile();
    }

    public File getMap() {
        return getFolder().toPath().resolve("map.json").toFile();
    }

    public static class PermissionRoleJSON {
        @SerializedName("name")
        public String name = "";
        @SerializedName("parents")
        public ArrayList<String> parents = new ArrayList<String>();
        @SerializedName("permissions")
        public ArrayList<String> permissions = new ArrayList<String>();
    }

    public static class GuildPermissionsJSON {
        @SerializedName("name")
        public String name = "";
        @SerializedName("comment")
        public String comment = "";
        @SerializedName("default_role")
        public String defaultRole = "";
        @SerializedName("roles")
        public HashMap<String, PermissionJSON> roles = new HashMap<String, PermissionJSON>();
        @SerializedName("users")
        public HashMap<String, PermissionJSON> users = new HashMap<String, PermissionJSON>();
        @SerializedName("discord_permissions")
        public HashMap<String, String> discordPermissions = new HashMap<String, String>();
        @SerializedName("permissions")
        public ArrayList<String> permissions = new ArrayList<String>();

        public static class PermissionJSON {
            @SerializedName("comment")
            public String comment = "";
            @SerializedName("role")
            public String role = "";
        }
    }
}
