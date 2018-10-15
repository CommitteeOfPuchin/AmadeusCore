package mjaroslav.bots.core.amadeus.database;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import mjaroslav.bots.core.amadeus.lib.References;

public abstract class AbstractDatabase {
    public final String name;

    private boolean ready = false;

    public AbstractDatabase(String name) {
        this.name = name;
    }

    public abstract boolean initDatabase();

    public final void init() {
        if (!ready && initDatabase())
            ready = true;
    }

    public abstract void executeUpdate(String request);

    public abstract ResultSet executeQuery(String request);

    public abstract Connection getConnection();

    public abstract Statement getStatement();

    public abstract void close();

    public File getFile() {
        return DatabaseHandler.FOLDER.toPath().resolve(String.format(References.FOLDER_DATABASES, name)).toFile();
    }

    public final boolean isReady() {
        return ready;
    }
}
