package mjaroslav.bots.core.amadeus.commands;

import java.io.File;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class CommandNameHandler {
    public final AmadeusCore core;
    public final CommandHandler handler;

    public CommandNameHandler(AmadeusCore core, CommandHandler handler) {
        this.core = core;
        this.handler = handler;
    }

    public abstract List<String> getPrefixes();

    public abstract List<String> getNames(String key);

    public abstract void loadNames();

    public abstract List<String> getArgNames(String commandKey, String argKey);

    public File getFolder() {
        return core.info.getFolder().toPath().resolve("commands/" + handler.name).toFile();
    }
}
