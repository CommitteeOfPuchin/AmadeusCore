package mjaroslav.bots.core.amadeus.lang;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import sx.blah.discord.handle.obj.IGuild;
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

    public String translate(long userId, long guildId, String key, Object... args) {
        String lang = I18n.defaultLang;
        if (GUILDS.containsKey(guildId))
            lang = GUILDS.get(guildId);
        if (USERS.containsKey(userId))
            lang = USERS.get(userId);
        return core.i18n.translate(lang, key, args);
    }

    public String translate(IUser user, IGuild guild, String key, Object... args) {
        return translate(user != null ? user.getLongID() : -1L, guild != null ? guild.getLongID() : -1L, key, args);
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
