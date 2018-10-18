package mjaroslav.bots.core.amadeus.config;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public class DefaultConfiguration extends PropertiesConfigurationHandler {
    public DefaultConfiguration(AmadeusCore core) {
        super(core, "default", true);
    }

    @Override
    public void afterLoad() throws Exception {
        core.devMode = getBoolean("devmode", false);
        core.hideInvite = getBoolean("hideinvite", true);
    }
}
