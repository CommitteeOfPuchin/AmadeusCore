package mjaroslav.bots.core.amadeus.lang;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;
import mjaroslav.bots.core.amadeus.database.DatabaseHandler;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class LangHandler {
    public static final AbstractDatabase database = DatabaseHandler.getDatabaseOrAddSQLite("languages");
    public static final HashMap<Long, String> USERS = new HashMap<>();
    public static final HashMap<Long, String> GUILDS = new HashMap<>();

    public static void load() {
        I18n.loadLangs();
        loadDatabase();
    }

    public static void loadDatabase() {
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

    public static String translate(long userId, long guildId, String key, Object... args) {
        String lang = I18n.defaultLang;
        if (GUILDS.containsKey(guildId))
            lang = GUILDS.get(guildId);
        if (USERS.containsKey(userId))
            lang = USERS.get(userId);
        return I18n.translate(lang, key, args);
    }

    public static String translate(IUser user, IGuild guild, String key, Object... args) {
        return translate(user != null ? user.getLongID() : -1L, guild != null ? guild.getLongID() : -1L, key, args);
    }

    public static void setLangToGuild(long guildId, String key) {
        database.executeUpdate("DELETE FROM guilds WHERE guildId = " + guildId);
        database.executeUpdate(String.format("INSERT INTO guilds(guildId, lang) VALUES(%s, %s)", guildId, key));
    }

    public static void setLangToGuild(IGuild guild, String key) {
        database.executeUpdate("DELETE FROM guilds WHERE guildId = " + guild.getLongID());
        database.executeUpdate(String.format("INSERT INTO guilds(guildId, lang, comment) VALUES(%s, %s, %s)",
                guild.getLongID(), key, guild.getName()));
    }

    public static void setLangToUser(long userId, String key) {
        database.executeUpdate("DELETE FROM users WHERE userId = " + userId);
        database.executeUpdate(String.format("INSERT INTO users(userId, lang) VALUES(%s, %s)", userId, key));
    }

    public static void setLangToUser(IUser user, String key) {
        database.executeUpdate("DELETE FROM users WHERE userId = " + user.getLongID());
        database.executeUpdate(String.format("INSERT INTO users(userId, lang, comment) VALUES(%s, %s, %s)",
                user.getLongID(), key, user.getName()));
    }
}
