package mjaroslav.bots.core.amadeus.database;

import java.io.File;
import java.util.HashMap;
import mjaroslav.bots.core.amadeus.lib.References;

public class DatabaseHandler {
    public static final File FOLDER = new File(References.FOLDER_DATABASES);
    private static final HashMap<String, AbstractDatabase> STORAGE = new HashMap<>();

    public static void addDatabase(AbstractDatabase database) {
        if (!STORAGE.containsKey(database.name))
            STORAGE.put(database.name, database);
    }

    public static void addDatabaseOrReplace(AbstractDatabase database) {
        if (STORAGE.containsKey(database.name))
            STORAGE.get(database.name).close();
        STORAGE.put(database.name, database);
    }

    public static AbstractDatabase getDatabaseOrAddSQLite(String name) {
        AbstractDatabase database = STORAGE.get(name);
        if (database == null) {
            database = new SQLiteDatabase(name);
            database.init();
            STORAGE.put(name, database);
        }
        return database;
    }

    public static AbstractDatabase getDatabase(String name) {
        return STORAGE.get(name);
    }
}
