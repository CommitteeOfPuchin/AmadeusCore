package mjaroslav.bots.core.amadeus.database;

import java.io.File;
import java.util.HashMap;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lib.FileHelper;

public class DatabaseHandler {
    private final HashMap<String, AbstractDatabase> STORAGE = new HashMap<>();

    public final AmadeusCore core;

    public DatabaseHandler(AmadeusCore core) {
        this.core = core;
    }

    public void addDatabase(AbstractDatabase database) {
        if (!STORAGE.containsKey(database.name))
            STORAGE.put(database.name, database);
    }

    public void addDatabaseOrReplace(AbstractDatabase database) {
        if (STORAGE.containsKey(database.name))
            STORAGE.get(database.name).close();
        STORAGE.put(database.name, database);
        database.init();
    }

    public AbstractDatabase getDatabaseOrAddSQLite(String name, File file) {
        AbstractDatabase database = STORAGE.get(name);
        if (database == null) {
            database = new SQLiteDatabase(name, file != null ? file : FileHelper.fileDatabase(core, name));
            database.init();
            STORAGE.put(name, database);
        }
        return database;
    }

    public AbstractDatabase getDatabase(String name) {
        return STORAGE.get(name);
    }
    
    public int count() {
        return STORAGE.size();
    }
}
