package mjaroslav.bots.core.amadeus.database;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class DatabaseHandler {
    public final String name;
    public final AmadeusCore core;

    private boolean ready = false;

    public DatabaseHandler(String name, AmadeusCore core) {
        this.name = name;
        this.core = core;
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

    public File getFolder() {
        return core.info.getFolder().toPath().resolve("databases").toFile();
    }

    public File getFile() {
        return getFolder().toPath().resolve(name + "." + getExt()).toFile();
    }

    public String getExt() {
        return "db";
    }
    
    public final boolean isReady() {
        return ready;
    }
}
