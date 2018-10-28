package mjaroslav.bots.core.amadeus.config;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public class DefaultConfiguration extends PropertiesConfigurationHandler {
    public DefaultConfiguration(AmadeusCore core) {
        super(core, "default", true);
    }

    @Override
    public void afterLoad() throws Exception {
        core.optionDevMode = getBoolean("devMode", false);
        core.optionHideInvite = getBoolean("hideInvite", true);
        core.optionLogChat = getBoolean("logChat", false);
        core.optionChatLogFormat = getString("chatLogFormat", "{guildLn}{channelLn}{userLn}{text}");
        core.optionMainPrefix = getString("mainPrefix", "execute");
    }
}
