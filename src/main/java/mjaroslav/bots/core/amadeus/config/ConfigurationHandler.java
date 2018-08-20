package mjaroslav.bots.core.amadeus.config;

import java.io.File;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class ConfigurationHandler {
    public final AmadeusCore core;

    public final String name;
    
    public ConfigurationHandler(AmadeusCore core, String name) {
        this.core = core;
        this.name = name;
    }

    public abstract void readConfig() throws Exception;

    public abstract void writeConfig() throws Exception;

    public abstract void setValue(String fieldName, Object value) throws Exception;

    public abstract <E> E getValue(String fieldName, Class<? extends E> type) throws Exception;

    public File getFolder() {
        return core.folder.toPath().resolve("configurations").toFile();
    }
}
