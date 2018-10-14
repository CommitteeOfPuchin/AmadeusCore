package mjaroslav.bots.core.amadeus.lang;

import java.util.HashMap;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.database.AbstractDatabase;

public class LangHandler {
    public final AmadeusCore core;
    public static AbstractDatabase handler;
    public static final I18n GLOBAL =  new I18n();
    public static final HashMap<Long, I18n> USERS = new HashMap<>();
    public static final HashMap<Long, I18n> GUILDS = new HashMap<>();
    
    public LangHandler(AmadeusCore core) {
        this.core = core;
        handler = core.getDatabaseHandler("default");
    }
}
