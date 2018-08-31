package mjaroslav.bots.core.amadeus.config;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lang.LangHandler;

public class DefaultConfiguration extends PropertiesConfigurationHandler {
    public DefaultConfiguration(AmadeusCore core) {
        super(core, "default", true);
    }

    @Override
    public void afterLoad() throws Exception {
        core.getLangHandler().setLang(getString("language", LangHandler.defaultLang));
        core.devMode = getBoolean("devmode", false);
        core.hideInvite = getBoolean("hideinvite", true);
    }
}
