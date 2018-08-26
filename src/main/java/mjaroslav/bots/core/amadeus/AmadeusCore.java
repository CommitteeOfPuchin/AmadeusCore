package mjaroslav.bots.core.amadeus;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mjaroslav.bots.core.amadeus.auth.AuthHandler;
import mjaroslav.bots.core.amadeus.auth.DefaultAuthHandler;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.commands.CommandHandler;
import mjaroslav.bots.core.amadeus.commands.DefaultCommandHandler;
import mjaroslav.bots.core.amadeus.config.ConfigurationHandler;
import mjaroslav.bots.core.amadeus.config.DefaultConfiguration;
import mjaroslav.bots.core.amadeus.lang.DefaultLangHandler;
import mjaroslav.bots.core.amadeus.lang.LangHandler;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils.Action;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AmadeusCore {
    //
    // Handlers
    //
    private AuthHandler authHandler;
    private final HashMap<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
    private final HashMap<String, ConfigurationHandler> configs = new HashMap<String, ConfigurationHandler>();
    private LangHandler langs;

    //
    // Other
    //
    public final Logger log = LogManager.getLogger(getName());
    private IDiscordClient client;
    private boolean isReady = false;

    //
    // Defaults
    //
    public final DefaultConfiguration DEFAULTCONFIG = new DefaultConfiguration(this);
    
    // Bot (program) name, NO DISCORD APP NAME.
    public abstract String getName();
    // Dir for bot files (configs, langs, etc).
    public abstract long getDevId();
    // Developer's discord ID.
    public abstract File getFolder();
    
    /**
     * Just call it when your bot will be ready.
     * 
     * @return True on ready.
     */
    public boolean startBot() {
        try {
            client = new ClientBuilder().withToken(getAuthHandler().loadToken()).login();
            client.getDispatcher().registerListener(new EventHandler(this));
            AmadeusUtils.waitAction(3000, 20, new Action() {
                @Override
                public boolean done() {
                    return isReady;
                }
            });
            if (isReady) {
                registerConfigurationHandlers();
                registerCommandHandlers();
                registerCommands();
                loadAll();
                log.info(translate("bot.ready"));
            }
            return isReady;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //
    // Load methods, used in reload command
    //
    public void loadAll() {
        loadNames();
        loadLangs();
        loadPerms();
        loadConfigs();
        loadOthers();
    }

    public void loadLangs() {
        try {
            getLangHandler().loadLangs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfigs() {
        for (ConfigurationHandler handler : listOfConfigurationHandlers())
            try {
                handler.readConfig();
                handler.afterLoad();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void loadOthers() {
    }

    public void loadPerms() {
        for (CommandHandler handler : listOfCommandHandlers())
            if (handler.hasPermissionHandller())
                handler.getPermissionHandler().loadPermissions();
    }

    public void loadNames() {
        for (CommandHandler handler : listOfCommandHandlers())
            if (handler.hasNameHandller())
                handler.getNameHandler().loadNames();
    }

    public List<String> getPermissionsList(String handlerName) {
        ArrayList<String> result = new ArrayList<String>();
        CommandHandler handler = getCommandHandler(handlerName);
        if (handler != null) {
            result.add(handler.name + ".*");
            for (BaseCommand command : handler.getCommandList()) {
                result.add(handler.name + "." + command.name);
                result.add(handler.name + "." + command.name + ".*");
                for (String arg : command.getArgsList())
                    result.add(handler.name + "." + command.name + "." + arg);
            }
        }
        return result;
    }

    public List<String> getPermissionsList() {
        ArrayList<String> result = new ArrayList<String>();
        result.add("*"); // Admin
        result.add("***"); // Owner
        for (CommandHandler handler : listOfCommandHandlers()) {
            result.add(handler.name); // Handler
            result.add(handler.name + ".*"); // Admin handler
            for (BaseCommand command : handler.getCommandList())
                result.addAll(command.getPermissions());
        }
        return result;
    }

    //
    // Ñonfigurations
    //
    public void addConfigurationHandler(ConfigurationHandler handler) {
        configs.put(handler.name, handler);
    }

    public ConfigurationHandler getConfigurationHandler(String name) {
        return configs.getOrDefault(name, null);
    }

    public List<ConfigurationHandler> listOfConfigurationHandlers() {
        return new ArrayList<ConfigurationHandler>(configs.values());
    }

    public void registerConfigurationHandlers() {
        addConfigurationHandler(DEFAULTCONFIG);
    }

    //
    // Commands
    //
    public void addCommandHandler(CommandHandler handler) {
        commands.put(handler.name, handler);
    }

    public CommandHandler getCommandHandler(String name) {
        return commands.getOrDefault(name, null);
    }

    public List<CommandHandler> listOfCommandHandlers() {
        return new ArrayList<CommandHandler>(commands.values());
    }

    public void registerCommandHandlers() {
        addCommandHandler(new DefaultCommandHandler(this, "default"));
    }

    public void registerCommands() {
        for (CommandHandler handler : listOfCommandHandlers())
            handler.registerCommands();
    }

    //
    // Langs
    //
    public void setLangHandler(LangHandler handler) {
        langs = handler;
    }

    public LangHandler getLangHandler() {
        if (langs == null)
            langs = new DefaultLangHandler(this);
        return langs;
    }

    public String translate(String key, Object... objects) {
        return getLangHandler().translate(key, objects);
    }

    //
    // Auth
    //
    public AuthHandler getAuthHandler() {
        if (authHandler == null)
            authHandler = new DefaultAuthHandler(this);
        return authHandler;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public int getCommandCount() {
        int result = 0;
        for (CommandHandler handler : listOfCommandHandlers())
            result += handler.getCommandList().size();
        return result;
    }

    //
    // Discord
    //
    public IDiscordClient getClient() {
        return client;
    }

    public void sendMessage(long channelId, String text) {
        if (!StringUtils.isEmpty(text))
            client.getChannelByID(channelId).sendMessage(text);
    }

    public void sendMessage(IMessage source, String text) {
        sendMessage(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(),
                text);
    }

    public void sendMessage(long channelId, String text, EmbedObject embed) {
        if (embed != null && !StringUtils.isEmpty(text))
            client.getChannelByID(channelId).sendMessage(text, embed);
        else if (embed != null)
            client.getChannelByID(channelId).sendMessage(embed);
        else if (!StringUtils.isEmpty(text))
            client.getChannelByID(channelId).sendMessage(text);
    }

    public void sendMessage(IMessage source, String text, EmbedObject embed) {
        sendMessage(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(),
                text, embed);
    }

    public void sendMessage(long channelId, EmbedObject embed) {
        if (embed != null)
            client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendMessage(IMessage source, EmbedObject embed) {
        sendMessage(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(),
                embed);
    }

    public void sendError(long channelId, String text) {
        String title = String.format(":no_entry_sign: %s", translate("answer.error"));
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(text).withColor(0xFF0000).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendError(IMessage source, String text) {
        sendError(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(), text);
    }

    public void sendDone(long channelId, String text) {
        String title = String.format(":white_check_mark: %s", translate("answer.done"));
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(text).withColor(0x00FF00).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendDone(IMessage source, String text) {
        sendDone(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(), text);
    }

    public void sendWarn(long channelId, String text) {
        String title = String.format(":warning: %s", translate("answer.warn"));
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(text).withColor(0xFFFF00).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendWarn(IMessage source, String text) {
        sendWarn(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(), text);
    }

    public void sendError(long channelId, Exception e) {
        String title = ":no_entry_sign: Exception: \"" + e.getMessage() + "\"";
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String stackTrace = writer.toString();
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(stackTrace).withColor(0xFF0000).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendError(IMessage source, Exception e) {
        sendError(source.getChannel() != null ? source.getChannel().getLongID() : source.getAuthor().getLongID(), e);
    }

    public void disableBot() {
        isReady = false;
        if (client != null)
            client.logout();
    }

    public void onReady() {
    }

    public static class EventHandler {
        private final AmadeusCore core;

        public EventHandler(AmadeusCore core) {
            this.core = core;
        }

        @EventSubscriber
        public void onReady(ReadyEvent event) {
            core.onReady();
            core.isReady = true;
        }

        @EventSubscriber
        public void onMessage(MessageReceivedEvent event) {
            String from = "";
            if (event.getMessage().getChannel() != null)
                from = "[" + new String(event.getChannel().getGuild().getName().getBytes(), StandardCharsets.UTF_8)
                        + ":" + new String(event.getChannel().getName().getBytes(), StandardCharsets.UTF_8) + "] "
                        + event.getAuthor().getDisplayName(event.getChannel().getGuild()) + ": ";
            else
                from = "[PM] " + event.getAuthor() + ": ";
            core.log.info(from + event.getMessage().getContent());
            for (CommandHandler commandHandler : core.listOfCommandHandlers())
                if (commandHandler.executeCommand(event))
                    break;
        }
    }
}
