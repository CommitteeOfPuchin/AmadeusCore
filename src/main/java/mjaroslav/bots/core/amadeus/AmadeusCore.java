package mjaroslav.bots.core.amadeus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mjaroslav.bots.core.amadeus.commands.CommandHandler;
import mjaroslav.bots.core.amadeus.commands.DefaultCommandHandler;
import mjaroslav.bots.core.amadeus.config.ConfigurationHandler;
import mjaroslav.bots.core.amadeus.config.DefaultConfiguration;
import mjaroslav.bots.core.amadeus.database.DatabaseHandler;
import mjaroslav.bots.core.amadeus.lang.I18n;
import mjaroslav.bots.core.amadeus.lang.LangHandler;
import mjaroslav.bots.core.amadeus.lib.BotInfo;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import mjaroslav.bots.core.amadeus.permissions.PermissionHandler;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils.Action;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.AttachmentPartEntry;
import sx.blah.discord.util.EmbedBuilder;

public abstract class AmadeusCore {
    private final HashMap<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
    private final HashMap<String, ConfigurationHandler> configs = new HashMap<String, ConfigurationHandler>();

    public final I18n i18n;
    public final LangHandler langs;
    public final DatabaseHandler databases;
    public final PermissionHandler permissions;

    public final Logger log;
    public IDiscordClient client;

    private boolean ready = false;

    public boolean optionDevMode = false;
    public boolean optionHideInvite = true;
    public boolean optionLogChat = true;
    public String optionChatLogFormat = "{guildLn}{channelLn}{userLn}{text}";
    public List<String> optionPrefixes = Arrays.asList("execute");

    public final DefaultConfiguration DEFAULTCONFIG;

    public BotInfo info;

    public AmadeusCore() throws Exception {
        info = AmadeusUtils.getBotInfo(this);
        if (info == null || !info.valid())
            throw new IllegalArgumentException("Error in 'name', 'dev_ids' or 'folder' field!");
        info.core = this;
        log = LogManager.getLogger(info.getName());
        DEFAULTCONFIG = new DefaultConfiguration(this);
        databases = new DatabaseHandler(this);
        i18n = new I18n(this);
        langs = new LangHandler(this);
        permissions = new PermissionHandler(this);
    }

    public AmadeusCore(String name, long[] devIds, String folder) {
        info = new BotInfo(this, name, devIds, folder);
        log = LogManager.getLogger(name);
        DEFAULTCONFIG = new DefaultConfiguration(this);
        i18n = new I18n(this);
        langs = new LangHandler(this);
        databases = new DatabaseHandler(this);
        permissions = new PermissionHandler(this);
    }

    public boolean startBot() {
        if (AmadeusUtils.existsOrCreateFile(FileHelper.fileBotToken())) {
            try {
                BufferedReader reader = Files.newBufferedReader(FileHelper.fileBotToken().toPath(),
                        StandardCharsets.UTF_8);
                String token = reader.readLine();
                reader.close();
                client = new ClientBuilder().withToken(token).login();
                client.getDispatcher().registerListener(new EventHandler(this));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        AmadeusUtils.waitAction(360000L, new Action() {
            @Override
            public synchronized boolean done() {
                return ready;
            }
        });
        if (ready) {
            registerConfigurationHandlers();
            registerCommandHandlers();
            registerCommands();
            loadAll();
            log.info(i18n.translate("bot.ready"));
        }
        return ready;
    }

    public void loadAll() {
        loadLangs();
        loadPerms();
        loadConfigs();
        loadOthers();
    }

    public void loadLangs() {
        langs.load();
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

    public void loadOthers() {}

    public void loadPerms() {
        permissions.load();
    }

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

    public int getCommandCount() {
        int result = 0;
        for (CommandHandler handler : listOfCommandHandlers())
            result += handler.getCommandList().size();
        return result;
    }

    public boolean isPrivateMessage(IMessage message) {
        return message.getGuild() == null;
    }

    public long getIDforAnswer(IMessage message) {
        return isPrivateMessage(message) ? message.getAuthor().getLongID() : message.getChannel().getLongID();
    }

    public IMessage answerMessage(IMessage message, Object... sendInfo) {
        return sendMessage(getIDforAnswer(message), sendInfo);
    }

    public IMessage sendMessage(long sendToId, Object... sendInfo) {
        IChannel channel = client.getChannelByID(sendToId);
        if (channel != null) {
            String text = null;
            EmbedObject embed = null;
            InputStream fileStream = null;
            File file = null;
            String fileName = "unknown";
            ArrayList<File> files = null;
            boolean tts = false;
            int i = 0;
            if (sendInfo.length > 0)
                if (sendInfo[0] instanceof String) {
                    text = (String) sendInfo[0];
                    i++;
                }
            for (; i < sendInfo.length; i++) {
                if (sendInfo[i] instanceof Boolean)
                    tts = (boolean) sendInfo[i];
                else if (sendInfo[i] instanceof EmbedObject)
                    embed = (EmbedObject) sendInfo[i];
                else if (sendInfo[i] instanceof File) {
                    file = (File) sendInfo[i];
                    if (files == null)
                        files = new ArrayList<>();
                    files.add((File) sendInfo[i]);
                    if (i + 1 < sendInfo.length && sendInfo[i + 1] instanceof String) {
                        fileName = (String) sendInfo[i + 1];
                        i++;
                    }
                } else if (sendInfo[i] instanceof List<?>) {
                    if (files == null)
                        files = new ArrayList<>();
                    List<?> list = (List<?>) sendInfo[i];
                    for (Object object1 : list)
                        if (object1 instanceof File)
                            files.add((File) object1);
                } else if (sendInfo[i] instanceof File[]) {
                    if (files == null)
                        files = new ArrayList<>();
                    files.addAll(Arrays.asList((File[]) sendInfo[i]));
                } else if (sendInfo[i] instanceof Path) {
                    file = ((Path) sendInfo[i]).toFile();
                    if (files == null)
                        files = new ArrayList<>();
                    files.add(((Path) sendInfo[i]).toFile());
                    if (i + 1 < sendInfo.length && sendInfo[i + 1] instanceof String) {
                        fileName = (String) sendInfo[i + 1];
                        i++;
                    }
                } else if (sendInfo[i] instanceof InputStream) {
                    fileStream = (InputStream) sendInfo[i];
                    if (i + 1 < sendInfo.length && sendInfo[i + 1] instanceof String) {
                        fileName = (String) sendInfo[i + 1];
                        i++;
                    }
                } else if (sendInfo[i] instanceof URL) {
                    try {
                        fileStream = ((URL) sendInfo[i]).openStream();
                        if (i + 1 < sendInfo.length && sendInfo[i + 1] instanceof String) {
                            fileName = (String) sendInfo[i + 1];
                            i++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                if (file == null && fileStream != null)
                    Files.copy(fileStream, Files.createTempFile(fileName, ""));
                if (AmadeusUtils.stringIsNotEmpty(text))
                    if (files != null && files.size() > 1)
                        if (embed != null)
                            return channel.sendFiles(text, tts, embed,
                                    AttachmentPartEntry.from(files.toArray(new File[] {})));
                        else
                            return channel.sendFiles(text, tts, AttachmentPartEntry.from(files.toArray(new File[] {})));
                    else if (file != null)
                        if (embed != null)
                            return channel.sendFile(text, tts, Files.newInputStream(file.toPath()), fileName, embed);
                        else
                            return channel.sendFile(text, tts, Files.newInputStream(file.toPath()), fileName);
                    else if (embed != null)
                        return channel.sendMessage(text, embed, tts);
                    else
                        return channel.sendMessage(text, tts);
                else if (files != null && files.size() > 1)
                    if (embed != null)
                        return channel.sendFiles(embed, files.toArray(new File[] {}));
                    else
                        return channel.sendFiles(files.toArray(new File[] {}));
                else if (file != null)
                    if (embed != null)
                        return channel.sendFile(embed, file);
                    else
                        return channel.sendFile(file);
                else
                    return channel.sendMessage(embed);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sendError(IMessage source, String text) {
        answerMessage(source,
                new EmbedBuilder()
                        .withTitle(String.format(":no_entry_sign: %s", langs.translate(source, "answer.error")))
                        .withDesc(text).withColor(0xFF0000).build());
    }

    public void sendDone(IMessage source, String text) {
        answerMessage(source,
                new EmbedBuilder()
                        .withTitle(String.format(":white_check_mark: %s", langs.translate(source, "answer.done")))
                        .withDesc(text).withColor(0x00FF00).build());
    }

    public void sendWarn(IMessage source, String text) {
        answerMessage(source,
                new EmbedBuilder().withTitle(String.format(":warning: %s", langs.translate(source, "answer.warn")))
                        .withDesc(text).withColor(0xFFFF00).build());
    }

    public void sendError(IMessage source, Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String stackTrace = writer.toString();
        answerMessage(source, new EmbedBuilder().withTitle(":no_entry_sign: Exception: \"" + e.getMessage() + "\"")
                .withDesc(stackTrace).withColor(0xFF0000).build());
    }

    public void disableBot() {
        ready = false;
        if (client != null)
            client.logout();
    }

    public void onReady() {}

    public static class EventHandler {
        private final AmadeusCore core;

        public EventHandler(AmadeusCore core) {
            this.core = core;
        }

        @EventSubscriber
        public void onReady(ReadyEvent event) {
            core.onReady();
            core.ready = true;
        }

        @EventSubscriber
        public void onMessage(MessageReceivedEvent event) {
            core.log.info(AmadeusUtils.formatChatLog(core, event));
            for (CommandHandler commandHandler : core.listOfCommandHandlers())
                if (commandHandler.executeCommand(event))
                    break;
        }
    }
}
