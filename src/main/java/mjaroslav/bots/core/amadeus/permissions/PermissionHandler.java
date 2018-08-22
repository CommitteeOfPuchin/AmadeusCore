package mjaroslav.bots.core.amadeus.permissions;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.commands.CommandHandler;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public abstract class PermissionHandler {
    public final AmadeusCore core;
    public final CommandHandler handler;

    public PermissionHandler(AmadeusCore core, CommandHandler handler) {
        this.core = core;
        this.handler = handler;
    }

    public abstract void loadPermissions();

    public abstract String getDefault();

    public abstract String getAdmin();

    public abstract String getOwner();

    public abstract CommandPermission get(String name);

    public abstract boolean hasPermission(IUser sender, IMessage source, String name);

    public abstract List<CommandPermission> get(List<String> names);

    public abstract List<CommandPermission> getAll();
    
    public abstract boolean canUseCommand(IUser sender, IMessage source, BaseCommand command);

    public abstract boolean canUse(IUser sender, IMessage source, String perm);

    public static class CommandPermission {
        public CommandPermission() {
        }

        public CommandPermission(boolean owner, boolean admin, int priority) {
            this.owner = owner;
            this.admin = admin;
            this.priority = priority;
        }

        public CommandPermission(boolean owner, boolean admin, int priority, ArrayList<Long> users,
                ArrayList<Long> guilds, ArrayList<Long> channels, ArrayList<Long> roles) {
            this.owner = owner;
            this.admin = admin;
            this.priority = priority;
            allowedUsers = users;
            allowedGuilds = guilds;
            allowedChannels = channels;
            allowedRoles = roles;
        }

        @SerializedName("owner")
        public boolean owner = false;
        @SerializedName("admin")
        public boolean admin = false;
        @SerializedName("priority")
        public int priority = 0;
        @SerializedName("users")
        public ArrayList<Long> allowedUsers = new ArrayList<Long>();
        @SerializedName("guilds")
        public ArrayList<Long> allowedGuilds = new ArrayList<Long>();
        @SerializedName("channels")
        public ArrayList<Long> allowedChannels = new ArrayList<Long>();
        @SerializedName("roles")
        public ArrayList<Long> allowedRoles = new ArrayList<Long>();
    }

    public abstract boolean hasPermission(IUser sender, IMessage source, CommandPermission permission);
}
