package mjaroslav.bots.core.amadeus.database;

import java.io.File;
import java.util.HashMap;
import mjaroslav.bots.core.amadeus.lib.References;

public class DatabaseHandler {
    public static final File FOLDER = new File(References.FOLDER_DATABASES);
    private static final HashMap<String, AbstractDatabase> storage = new HashMap<>();
    
    public void addDatabaseHandler(AbstractDatabase handler) {
        if (storage.containsKey(handler.name))
            storage.get(handler.name).close();
        storage.put(handler.name, handler);
    }

    public AbstractDatabase getDatabaseHandler(String name) {
        AbstractDatabase handler = storage.get(name);
        if (handler == null) {
            handler = new Database(name);
            handler.init();
            storage.put(name, handler);
        }
        return handler;
    }
}
