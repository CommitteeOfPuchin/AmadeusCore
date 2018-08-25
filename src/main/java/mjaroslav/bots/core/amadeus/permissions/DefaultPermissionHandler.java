package mjaroslav.bots.core.amadeus.permissions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.commands.CommandHandler;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.JSONReader;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class DefaultPermissionHandler extends PermissionHandler {
    public final JSONReader<HashMap<String, List<String>>> readerList = new JSONReader<HashMap<String, List<String>>>(
            new HashMap<String, List<String>>(), core.folder.toPath().resolve("permissionslist.json").toFile(), true);
    public final JSONReader<HashMap<String, String>> readerMap = new JSONReader<HashMap<String, String>>(
            new HashMap<String, String>(), core.folder.toPath().resolve("permissionsmap.json").toFile(), true);

    public static final String FORMATUSER = "user:%s";
    public static final String FORMATROLE = "role:%s";

    public static final List<String> DEFAULT = Arrays.asList("default.help");
    public static final List<String> ADMIN = Arrays.asList("*");
    public static final List<String> OWNER = Arrays.asList("***");

    public DefaultPermissionHandler(AmadeusCore core, CommandHandler handler) {
        super(core, handler);
        readerList.defaults.put(getAdmin(), ADMIN);
        readerList.defaults.put(getOwner(), OWNER);
        readerList.defaults.put(getDefault(), DEFAULT);
        readerList.init();
        readerMap.defaults.put(String.format(FORMATUSER, core.devId), getOwner());
        readerMap.init();
    }

    @Override
    public boolean canUse(IUser sender, IMessage source, String perm) {
        String[] permInfo = perm.split("\\.");
        if (hasPermission(sender, source, "***"))
            return true;
        if (permInfo.length > 0)
            if (permInfo.length == 2) {
                if (hasPermission(sender, source, permInfo[0] + ".*"))
                    return true;
            } else if (permInfo.length == 3) {
                if (hasPermission(sender, source, permInfo[0] + "." + permInfo[1] + ".*"))
                    return true;
            }
        return hasPermission(sender, source, perm);
    }

    @Override
    public boolean canUseCommand(IUser sender, IMessage source, BaseCommand command, String arg) {
        boolean flag = false;
        if (!command.onlyOwner() && source.getChannel() != null
                && sender.getPermissionsForGuild(source.getGuild()).contains(Permissions.ADMINISTRATOR))
            flag = true;
        return flag || (AmadeusUtils.stringIsNotEmpty(arg) ? canUse(sender, source, command.getArgPermission(arg))
                : canUse(sender, source, command.getCommandPermission()));
    }

    @Override
    public List<String> get(String name) {
        return readerList.json.getOrDefault(name, readerList.json.getOrDefault(getDefault(), Collections.emptyList()));
    }

    @Override
    public String getAdmin() {
        return "admin";
    }

    @Override
    public String getDefault() {
        return "default";
    }

    @Override
    public String getOwner() {
        return "owner";
    }

    @Override
    public boolean hasPermission(IUser sender, IMessage source, String name) {
        List<String> perms = get(readerMap.json.get(String.format(FORMATUSER, sender.getLongID())));
        if (perms.contains(name))
            return true;
        if (source.getChannel() != null)
            for (IRole role : sender.getRolesForGuild(source.getGuild())) {
                perms = get(readerMap.json.get(String.format(FORMATROLE, role.getLongID())));
                if (perms.contains(name))
                    return true;
            }
        return false;
    }

    @Override
    public void loadPermissions() {
        readerList.read();
        readerMap.read();
    }
}
