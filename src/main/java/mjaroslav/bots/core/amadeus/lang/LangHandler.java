package mjaroslav.bots.core.amadeus.lang;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class LangHandler {
    public final AmadeusCore core;
    public final AbstractDatabase database;
    public final HashMap<Long, String> USERS = new HashMap<>();
    public final HashMap<Long, String> CHANNELS = new HashMap<>();
    public final HashMap<Long, String> GUILDS = new HashMap<>();

    public LangHandler(AmadeusCore core) {
        this.core = core;
        database = core.databases.getDatabaseOrAddSQLite("languages", FileHelper.fileLanguagesDatabase(core));
    }

    public void load() {
        core.i18n.loadLangs();
        loadDatabase();
    }

    public void loadDatabase() {
        database.executeUpdate("CREATE TABLE IF NOT EXISTS users(userId INTEGER UNIQUE, lang VARCHAR NOT NULL DEFAULT '"
                + I18n.defaultLang + "')");
        database.executeUpdate("CREATE TABLE IF NOT EXISTS roles(roleId INTEGER UNIQUE, lang VARCHAR NOT NULL DEFAULT '"
                + I18n.defaultLang + "')");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS channels(channelId INTEGER UNIQUE, lang VARCHAR NOT NULL DEFAULT '"
                        + I18n.defaultLang + "')");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS guilds(guildId INTEGER UNIQUE, lang VARCHAR NOT NULL DEFAULT '"
                        + I18n.defaultLang + "')");
        USERS.clear();
        CHANNELS.clear();
        GUILDS.clear();
        try {
            ResultSet result = database.executeQuery("SELECT userId, lang FROM users");
            while (result.next())
                USERS.put(result.getLong("userId"), result.getString("lang"));
            result = database.executeQuery("SELECT channelId, lang FROM channels");
            while (result.next())
                CHANNELS.put(result.getLong("channelId"), result.getString("lang"));
            result = database.executeQuery("SELECT guildId, lang FROM guilds");
            while (result.next())
                GUILDS.put(result.getLong("guildId"), result.getString("lang"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPreifxes(IMessage message) {
        List<String> result = core.i18n.getNamesCustom(getLang(message), "prefix");
        result.add(core.optionMainPrefix);
        return result;
    }

    public List<String> getPreifxes(IGuild guild, IChannel channel, IUser user) {
        List<String> result = core.i18n.getNamesCustom(getLang(guild, channel, user), "prefix");
        result.add(core.optionMainPrefix);
        return result;
    }

    public List<String> getPreifxes(long guildId, long channelId, long userId) {
        List<String> result = core.i18n.getNamesCustom(getLang(guildId, channelId, userId), "prefix");
        result.add(core.optionMainPrefix);
        return result;
    }

    public List<String> getNamesCustom(IMessage message, String key) {
        return core.i18n.getNamesCustom(getLang(message), key);
    }

    public List<String> getNamesCustom(IGuild guild, IChannel channel, IUser user, String key) {
        return core.i18n.getNamesCustom(getLang(guild, channel, user), key);
    }

    public List<String> getNamesCustom(long guildId, long channelId, long userId, String key) {
        return core.i18n.getNamesCustom(getLang(guildId, channelId, userId), key);
    }

    public List<String> getNames(IGuild guild, IChannel channel, IUser user, String handlerKey, String commandKey) {
        return core.i18n.getNames(getLang(guild, channel, user), handlerKey, commandKey);
    }

    public List<String> getNames(long guildId, long channelId, long userId, String handlerKey, String commandKey) {
        return core.i18n.getNames(getLang(guildId, channelId, userId), handlerKey, commandKey);
    }

    public List<String> getNamesArg(IGuild guild, IChannel channel, IUser user, String handlerKey, String commandKey,
            String argKey) {
        return core.i18n.getNamesArg(getLang(guild, channel, user), handlerKey, commandKey, argKey);
    }

    public List<String> getNamesArg(long guildId, long channelId, long userId, String handlerKey, String commandKey,
            String argKey) {
        return core.i18n.getNamesArg(getLang(guildId, channelId, userId), handlerKey, commandKey, argKey);
    }

    public List<String> getNames(IGuild guild, IChannel channel, IUser user, BaseCommand command) {
        return core.i18n.getNames(getLang(guild, channel, user), command.handler.name, command.name);
    }

    public List<String> getNames(long guildId, long channelId, long userId, BaseCommand command) {
        return core.i18n.getNames(getLang(guildId, channelId, userId), command.handler.name, command.name);
    }

    public List<String> getNamesArg(IGuild guild, IChannel channel, IUser user, BaseCommand command, String argKey) {
        return core.i18n.getNamesArg(getLang(guild, channel, user), command.handler.name, command.name, argKey);
    }

    public List<String> getNamesArg(long guildId, long channelId, long userId, BaseCommand command, String argKey) {
        return core.i18n.getNamesArg(getLang(guildId, channelId, userId), command.handler.name, command.name, argKey);
    }

    public List<String> getNames(IMessage message, String handlerKey, String commandKey) {
        return core.i18n.getNames(getLang(message), handlerKey, commandKey);
    }

    public List<String> getNamesArg(IMessage message, String handlerKey, String commandKey, String argKey) {
        return core.i18n.getNamesArg(getLang(message), handlerKey, commandKey, argKey);
    }

    public List<String> getNames(IMessage message, BaseCommand command) {
        return core.i18n.getNames(getLang(message), command.handler.name, command.name);
    }

    public List<String> getNamesArg(IMessage message, BaseCommand command, String argKey) {
        return core.i18n.getNamesArg(getLang(message), command.handler.name, command.name, argKey);
    }

    public String getLang(long guildId, long channelId, long userId) {
        String lang = I18n.defaultLang;
        if (GUILDS.containsKey(guildId))
            lang = GUILDS.get(guildId);
        if (CHANNELS.containsKey(channelId))
            lang = CHANNELS.get(channelId);
        if (USERS.containsKey(userId))
            lang = USERS.get(userId);
        return lang;
    }

    public String getLang(IMessage message) {
        return getLang(message.getGuild(), core.isPrivateMessage(message) ? null : message.getChannel(),
                message.getAuthor());
    }

    public String getLang(IGuild guild, IChannel channel, IUser user) {
        return getLang(guild != null ? guild.getLongID() : -1L, channel != null ? channel.getLongID() : -1,
                user != null ? user.getLongID() : -1L);
    }

    public String translate(long guildId, long channelId, long userId, String key, Object... args) {
        return core.i18n.translate(getLang(guildId, channelId, userId), key, args);
    }

    public String translate(IGuild guild, IChannel channel, IUser user, String key, Object... args) {
        return core.i18n.translate(getLang(guild, channel, user), key, args);
    }

    public String translate(IMessage message, String key, Object... args) {
        return core.i18n.translate(getLang(message), key, args);
    }

    public void resetLangFromGuild(long guildId) {
        database.executeUpdate("DELETE FROM guilds WHERE guildId = " + guildId);
    }

    public void resetLangFromGuild(IGuild guild) {
        if (guild != null)
            resetLangFromGuild(guild.getLongID());
    }

    public void setLangToGuild(long guildId, String key) {
        resetLangFromGuild(guildId);
        GUILDS.remove(guildId);
        database.executeUpdate(String.format("INSERT INTO guilds(guildId, lang) VALUES(%s, '%s')", guildId, key));
        GUILDS.put(guildId, key);
    }

    public void setLangToGuild(IGuild guild, String key) {
        if (guild != null)
            setLangToGuild(guild.getLongID(), key);
    }

    public void resetLangFromChannel(long channelId) {
        database.executeUpdate("DELETE FROM channels WHERE channelId = " + channelId);
    }

    public void resetLangFromChannel(IChannel channel) {
        if (channel != null)
            resetLangFromGuild(channel.getLongID());
    }

    public void setLangToChannel(long channelId, String key) {
        CHANNELS.remove(channelId);
        database.executeUpdate(String.format("INSERT INTO channels(channelId, lang) VALUES(%s, '%s')", channelId, key));
        CHANNELS.put(channelId, key);
    }

    public void setLangToChannel(IChannel channel, String key) {
        if (channel != null)
            setLangToChannel(channel.getLongID(), key);
    }

    public void resetLangFromUser(long userId) {
        database.executeUpdate("DELETE FROM users WHERE userId = " + userId);
    }

    public void resetLangFromUser(IUser user) {
        if (user != null)
            resetLangFromGuild(user.getLongID());
    }

    public void setLangToUser(long userId, String key) {
        resetLangFromUser(userId);
        USERS.remove(userId);
        USERS.put(userId, key);
        database.executeUpdate(String.format("INSERT INTO users(userId, lang) VALUES(%s, '%s')", userId, key));
    }

    public void setLangToUser(IUser user, String key) {
        if (user != null)
            setLangToUser(user.getLongID(), key);
    }
}
