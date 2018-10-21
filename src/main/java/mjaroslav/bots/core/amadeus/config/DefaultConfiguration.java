package mjaroslav.bots.core.amadeus.config;

import java.util.Arrays;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

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
        core.optionPrefixes = Arrays.asList(getStringList("prefixes", new String[] {"execute"}));
        core.optionPrefixes.sort(AmadeusUtils.LENGTH_SORTER);
    }
}
