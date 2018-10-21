package mjaroslav.bots.core.amadeus.lang;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.commands.BaseCommand;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class LangHandler {
    public final AmadeusCore core;
    public final AbstractDatabase database;
    public final HashMap<Long, String> USERS = new HashMap<>();
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
                + I18n.defaultLang + "', comment VARCHAR(30))");
        database.executeUpdate(
                "CREATE TABLE IF NOT EXISTS guilds(guildId INTEGER UNIQUE, lang VARCHAR NOT NULL DEFAULT '"
                        + I18n.defaultLang + "', comment VARCHAR(30))");
        USERS.clear();
        try {
            ResultSet result = database.executeQuery("SELECT userId, lang FROM users");
            while (result.next())
                USERS.put(result.getLong("userId"), result.getString("lang"));
            result = database.executeQuery("SELECT guildId, lang FROM guilds");
            while (result.next())
                GUILDS.put(result.getLong("guildId"), result.getString("lang"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getNamesCustom(IMessage message, String key) {
        return core.i18n.getNamesCustom(getLang(message), key);
    }
    
    public List<String> getNamesCustom(IGuild guild, IUser user, String key) {
        return core.i18n.getNamesCustom(getLang(guild, user), key);
    }

    public List<String> getNamesCustom(long guildId, long userId, String key) {
        return core.i18n.getNamesCustom(getLang(guildId, userId), key);
    }

    public List<String> getNames(IGuild guild, IUser user, String handlerKey, String commandKey) {
        return core.i18n.getNames(getLang(guild, user), handlerKey, commandKey);
    }

    public List<String> getNames(long guildId, long userId, String handlerKey, String commandKey) {
        return core.i18n.getNames(getLang(guildId, userId), handlerKey, commandKey);
    }

    public List<String> getNamesArg(IGuild guild, IUser user, String handlerKey, String commandKey, String argKey) {
        return core.i18n.getNamesArg(getLang(guild, user), handlerKey, commandKey, argKey);
    }

    public List<String> getNamesArg(long guildId, long userId, String handlerKey, String commandKey, String argKey) {
        return core.i18n.getNamesArg(getLang(guildId, userId), handlerKey, commandKey, argKey);
    }

    public List<String> getNames(IGuild guild, IUser user, BaseCommand command) {
        return core.i18n.getNames(getLang(guild, user), command.handler.name, command.name);
    }

    public List<String> getNames(long guildId, long userId, BaseCommand command) {
        return core.i18n.getNames(getLang(guildId, userId), command.handler.name, command.name);
    }

    public List<String> getNamesArg(IGuild guild, IUser user, BaseCommand command, String argKey) {
        return core.i18n.getNamesArg(getLang(guild, user), command.handler.name, command.name, argKey);
    }

    public List<String> getNamesArg(long guildId, long userId, BaseCommand command, String argKey) {
        return core.i18n.getNamesArg(getLang(guildId, userId), command.handler.name, command.name, argKey);
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

    public String getLang(long guildId, long userId) {
        String lang = I18n.defaultLang;
        if (GUILDS.containsKey(guildId))
            lang = GUILDS.get(guildId);
        if (USERS.containsKey(userId))
            lang = USERS.get(userId);
        return lang;
    }

    public String getLang(IMessage message) {
        return getLang(message.getGuild(), message.getAuthor());
    }

    public String getLang(IGuild guild, IUser user) {
        return getLang(guild != null ? guild.getLongID() : -1L, user != null ? user.getLongID() : -1L);
    }

    public String translate(long guildId, long userId, String key, Object... args) {
        return core.i18n.translate(getLang(guildId, userId), key, args);
    }

    public String translate(IGuild guild, IUser user, String key, Object... args) {
        return core.i18n.translate(getLang(guild, user), key, args);
    }

    public String translate(IMessage message, String key, Object... args) {
        return core.i18n.translate(getLang(message), key, args);
    }

    public void setLangToGuild(long guildId, String key) {
        database.executeUpdate("DELETE FROM guilds WHERE guildId = " + guildId);
        if (!key.equals(I18n.defaultLang))
            database.executeUpdate(String.format("INSERT INTO guilds(guildId, lang) VALUES(%s, %s)", guildId, key));
    }

    public void setLangToGuild(IGuild guild, String key) {
        database.executeUpdate("DELETE FROM guilds WHERE guildId = " + guild.getLongID());
        if (!key.equals(I18n.defaultLang))
            database.executeUpdate(String.format("INSERT INTO guilds(guildId, lang, comment) VALUES(%s, %s, %s)",
                    guild.getLongID(), key, guild.getName()));
    }

    public void setLangToUser(long userId, String key) {
        database.executeUpdate("DELETE FROM users WHERE userId = " + userId);
        if (!key.equals(I18n.defaultLang))
            database.executeUpdate(String.format("INSERT INTO users(userId, lang) VALUES(%s, %s)", userId, key));
    }

    public void setLangToUser(IUser user, String key) {
        database.executeUpdate("DELETE FROM users WHERE userId = " + user.getLongID());
        if (!key.equals(I18n.defaultLang))
            database.executeUpdate(String.format("INSERT INTO users(userId, lang, comment) VALUES(%s, %s, %s)",
                    user.getLongID(), key, user.getName()));
    }
}
