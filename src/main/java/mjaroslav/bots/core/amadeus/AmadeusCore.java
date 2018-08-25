package mjaroslav.bots.core.amadeus;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mjaroslav.bots.core.amadeus.auth.AuthHandler;
import mjaroslav.bots.core.amadeus.auth.DefaultAuthHandler;
import mjaroslav.bots.core.amadeus.commands.*;
import mjaroslav.bots.core.amadeus.config.BaseConfigurationHandler.DefaultConfigurationHandler;
import mjaroslav.bots.core.amadeus.config.ConfigurationHandler;
import mjaroslav.bots.core.amadeus.lang.DefaultLangHandler;
import mjaroslav.bots.core.amadeus.lang.LangHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AmadeusCore {
    public final String name;
    private IDiscordClient client;
    private AuthHandler authHandler;
    public final ArrayList<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
    public final ArrayList<ConfigurationHandler<?>> configurationHandlers = new ArrayList<ConfigurationHandler<?>>();
    private LangHandler langHandler;
    private boolean isReady = false;
    public final File folder;
    public final long devId;
    public final Logger log;

    public AmadeusCore(String name, String folder, long devId) {
        this.name = name;
        this.folder = new File(folder);
        this.devId = devId;
        log = LogManager.getLogger(name);
    }

    public boolean initBot() {
        try {
            client = new ClientBuilder().withToken(getAuthHandler().loadToken()).login();
            client.getDispatcher().registerListener(new EventHandler(this));
            int counter = 0;
            while (!isReady && counter < 20) {
                Thread.sleep(3000);
                counter++;
            }
            if (isReady) {
                getLangHandler().loadLangs();
                registerConfigurationHandlers();
                for (ConfigurationHandler<?> configurationHandler : configurationHandlers)
                    configurationHandler.readConfig();
                registerCommandHandlers();
                registerCommands();
            }
            return isReady;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getPermissionsList(String handlerName) {
        ArrayList<String> result = new ArrayList<String>();
        CommandHandler handler = getCommandHanler(handlerName);
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
        for (CommandHandler handler : commandHandlers) {
            result.add(handler.name); // Handler
            result.add(handler.name + ".*"); // Admin handler
            for (BaseCommand command : handler.getCommandList())
                result.addAll(command.getPermissions());
        }
        return result;
    }

    public void registerCommandHandlers() {
        commandHandlers.add(new DefaultCommandHandler(this));
    }

    public void registerConfigurationHandlers() {
        configurationHandlers.add(new DefaultConfigurationHandler(this, "default", true));
    }

    public int getCommandCount() {
        int result = 0;
        for (CommandHandler handler : commandHandlers)
            result += handler.getCommandList().size();
        return result;
    }

    public ConfigurationHandler<?> getConfigurationHandler(String name) {
        for (ConfigurationHandler<?> configurationHandler : configurationHandlers)
            if (configurationHandler.name.equals(name))
                return configurationHandler;
        return null;
    }

    public CommandHandler getCommandHanler(String name) {
        for (CommandHandler commandHandler : commandHandlers)
            if (commandHandler.name.equals(name))
                return commandHandler;
        return null;
    }

    public void registerCommand(String handlerName, Class<? extends BaseCommand> command) throws Exception {
        CommandHandler commandHandler = getCommandHanler(handlerName);
        if (commandHandler != null)
            commandHandler.registerCommand(
                    command.getConstructor(AmadeusCore.class, CommandHandler.class).newInstance(this, commandHandler));
    }

    public void registerCommand(Class<? extends BaseCommand> command) throws Exception {
        registerCommand("default", command);
    }

    public void registerCommands() throws Exception {
        registerCommand(CommandHelp.class);
        registerCommand(CommandExit.class);
        registerCommand(CommandReload.class);
        registerCommand(CommandStatus.class);
    }

    public void sendMessage(long channelId, String text) {
        if (!StringUtils.isEmpty(text))
            client.getChannelByID(channelId).sendMessage(text);
    }

    public void sendMessage(long channelId, String text, EmbedObject embed) {
        if (embed != null && !StringUtils.isEmpty(text))
            client.getChannelByID(channelId).sendMessage(text, embed);
        else if (embed != null)
            client.getChannelByID(channelId).sendMessage(embed);
        else if (!StringUtils.isEmpty(text))
            client.getChannelByID(channelId).sendMessage(text);
    }

    public void sendMessage(long channelId, EmbedObject embed) {
        if (embed != null)
            client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendError(long channelId, String text) {
        String title = String.format(":no_entry_sign: %s", translate("answer.error"));
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(text).withColor(0xFF0000).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendDone(long channelId, String text) {
        String title = String.format(":white_check_mark: %s", translate("answer.done"));
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(text).withColor(0x00FF00).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendWarn(long channelId, String text) {
        String title = String.format(":warning: %s", translate("answer.warn"));
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(text).withColor(0xFFFF00).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void sendError(long channelId, Exception e) {
        String title = ":no_entry_sign: Exception: \"" + e.getMessage() + "\"";
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String stackTrace = writer.toString();
        EmbedObject embed = new EmbedBuilder().withTitle(title).withDesc(stackTrace).withColor(0xFF0000).build();
        client.getChannelByID(channelId).sendMessage(embed);
    }

    public void disableBot() {
        isReady = false;
        if (client != null)
            client.logout();
    }

    public String translate(String key, Object... objects) {
        return getLangHandler().translate(key, objects);
    }

    public LangHandler getLangHandler() {
        if (langHandler == null)
            langHandler = new DefaultLangHandler(this);
        return langHandler;
    }

    public void setLangHandler(LangHandler langHandler) {
        this.langHandler = langHandler;
    }

    public AuthHandler getAuthHandler() {
        if (authHandler == null)
            authHandler = new DefaultAuthHandler(this);
        return authHandler;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public IDiscordClient getClient() {
        return client;
    }

    public ArrayList<CommandHandler> getCommandHandlers() {
        return commandHandlers;
    }

    public ArrayList<ConfigurationHandler<?>> getConfigurationHandlers() {
        return configurationHandlers;
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
            core.log.info("Bot ready!");
            core.isReady = true;
            core.onReady();
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
            for (CommandHandler commandHandler : core.getCommandHandlers())
                if (commandHandler.executeCommand(event))
                    break;
        }
    }
}
