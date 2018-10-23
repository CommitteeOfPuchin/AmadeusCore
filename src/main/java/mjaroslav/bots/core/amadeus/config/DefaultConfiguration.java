package mjaroslav.bots.core.amadeus.config;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public class DefaultConfiguration extends PropertiesConfigurationHandler {
    public DefaultConfiguration(AmadeusCore core) {
        super(core, "default", true);
    }

    @Override
    public void afterLoad() throws Exception {
        core.optionDevMode = getBoolean("devmode", false);
        core.optionHideInvite = getBoolean("hideinvite", true);
        core.optionLogChat = getBoolean("logchat", true);
        core.optionChatLogFormat = getString("chatlogformat", "{guildLn}{channelLn}{userLn}{text}");
        core.optionMainPrefix = getString("mainprefix", "execute");
    }
}
