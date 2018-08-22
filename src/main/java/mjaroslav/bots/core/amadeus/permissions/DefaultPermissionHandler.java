package mjaroslav.bots.core.amadeus.permissions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.commands.CommandHandler;
import mjaroslav.bots.core.amadeus.utils.JSONReader;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class DefaultPermissionHandler extends PermissionHandler {
    public final JSONReader<HashMap<String, CommandPermission>> reader = new JSONReader<HashMap<String, CommandPermission>>(
            new HashMap<String, CommandPermission>(), core.folder.toPath().resolve("permissions.json").toFile(), true);

    public static final CommandPermission DEFAULT = new CommandPermission(false, false, 0);
    public static final CommandPermission ADMIN = new CommandPermission(false, true, 100);
    public static final CommandPermission OWNER = new CommandPermission(true, false, 1000);

    private static final Comparator<CommandPermission> sort = new Comparator<CommandPermission>() {
        @Override
        public int compare(CommandPermission o1, CommandPermission o2) {
            return o2.priority - o1.priority;
        }
    };

    public DefaultPermissionHandler(AmadeusCore core, CommandHandler handler) {
        super(core, handler);
        reader.init();
        boolean flag = false;
        if (!reader.json.containsKey(getOwner())) {
            reader.json.put(getOwner(), OWNER);
            flag = true;
        }
        if (!reader.json.containsKey(getAdmin())) {
            reader.json.put(getAdmin(), ADMIN);
            flag = true;
        }
        if (!reader.json.containsKey(getDefault())) {
            reader.json.put(getDefault(), DEFAULT);
            flag = true;
        }
        if (flag)
            reader.write();
    }

    @Override
    public boolean hasPermission(IUser sender, IMessage source, String name) {
        return hasPermission(sender, source, get(name));
    }

    @Override
    public boolean hasPermission(IUser sender, IMessage source, CommandPermission permission) {
        if (permission.owner)
            return permission.allowedUsers.contains(sender.getLongID());
        if (permission.admin)
            return source.getChannel() != null
                    ? sender.getPermissionsForGuild(source.getChannel().getGuild()).contains(Permissions.ADMINISTRATOR)
                    : true;
        if (source.getChannel() != null && !permission.allowedRoles.isEmpty()) {
            boolean flag = false;
            for (IRole role : sender.getRolesForGuild(source.getChannel().getGuild()))
                if (permission.allowedRoles.contains(role.getLongID())) {
                    flag = true;
                    break;
                }
            if (!flag)
                return false;
        }
        if (source.getChannel() != null && ((!permission.allowedChannels.isEmpty()
                && !permission.allowedChannels.contains(source.getChannel().getLongID()))
                || (!permission.allowedGuilds.isEmpty()
                        && !permission.allowedGuilds.contains(source.getChannel().getGuild().getLongID()))))
            return false;
        return permission.allowedUsers.isEmpty() || permission.allowedUsers.contains(sender.getLongID());
    }

    @Override
    public String getOwner() {
        return "owner";
    }

    @Override
    public void loadPermissions() {
        reader.read();
    }

    @Override
    public String getDefault() {
        return "user";
    }

    @Override
    public String getAdmin() {
        return "admin";
    }

    @Override
    public boolean canUseCommand(IUser sender, IMessage source, BaseCommand command) {
        return canUse(sender, source, command.getPermissions());
    }

    @Override
    public CommandPermission get(String name) {
        if (reader.json.containsKey(name))
            return reader.json.get(name);
        else {
            reader.json.put(name, ADMIN);
            reader.write();
        }
        return ADMIN;
    }

    @Override
    public List<CommandPermission> getAll() {
        ArrayList<CommandPermission> result = new ArrayList<CommandPermission>(reader.json.values());
        result.sort(sort);
        return result;
    }

    @Override
    public List<CommandPermission> get(List<String> names) {
        ArrayList<CommandPermission> result = new ArrayList<CommandPermission>();
        for (String name : names) {
            CommandPermission permission = get(name);
            if (permission != null)
                result.add(permission);
        }
        result.sort(sort);
        return result;
    }

    @Override
    public boolean canUse(IUser sender, IMessage source, String perm) {
        if (!perm.isEmpty()) {
            CommandPermission min = get(perm);
            CommandPermission main = null;
            for (CommandPermission permission : getAll())
                if (hasPermission(sender, source, permission)) {
                    main = permission;
                    break;
                }
            return main != null ? min.priority <= main.priority : false;
        }
        return true;
    }
}
