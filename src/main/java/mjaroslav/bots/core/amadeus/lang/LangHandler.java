package mjaroslav.bots.core.amadeus.lang;

import java.io.File;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class LangHandler {
    public final AmadeusCore core;

    public LangHandler(AmadeusCore core) {
        this.core = core;
    }

    public abstract String translate(String key, Object... objects);

    public abstract void loadLangs() throws Exception;

    public abstract void setLang(String lang) throws Exception;

    public abstract List<String> getLangs();

    public File getFolder() {
        return core.getFolder().toPath().resolve("languages").toFile();
    }
}
