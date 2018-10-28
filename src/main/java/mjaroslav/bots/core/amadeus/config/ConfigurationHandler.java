package mjaroslav.bots.core.amadeus.config;

import java.io.File;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lib.FileHelper;

public abstract class ConfigurationHandler {
    public final AmadeusCore core;
    public final String name;

    public ConfigurationHandler(AmadeusCore core, String name) {
        this.core = core;
        this.name = name;
    }

    public abstract void readConfig() throws Exception;

    public abstract void afterLoad() throws Exception;

    public abstract void writeConfig() throws Exception;

    public File getFile() {
        return getFolder().toPath().resolve(name + "." + getExt()).toFile();
    }

    public String getExt() {
        return "json";
    }

    public File getFolder() {
        return FileHelper.folderConfigurations(core).toFile();
    }
}
