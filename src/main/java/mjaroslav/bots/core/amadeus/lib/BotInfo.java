package mjaroslav.bots.core.amadeus.lib;

import java.io.File;
import com.google.gson.annotations.SerializedName;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class BotInfo {
    public AmadeusCore core;

    @SerializedName("name")
    private String name;
    @SerializedName("dev_ids")
    private long[] devIds;
    @SerializedName("folder")
    private String folder;
    @SerializedName("version")
    private String version;
    @SerializedName("source_url")
    private String sourceUrl;
    @SerializedName("bot_site")
    private String botSite;
    @SerializedName("credits")
    private String credits;
    @SerializedName("description")
    private String desc;
    @SerializedName("invite")
    private String invite;

    public BotInfo() {}

    public BotInfo(AmadeusCore core, String name, long[] devIds, String folder) {
        this.core = core;
        this.name = name;
        this.devIds = devIds;
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public long[] getDevIds() {
        return devIds;
    }

    public File getFolder() {
        return new File(folder);
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public boolean hasSourceUrl() {
        return AmadeusUtils.stringIsNotEmpty(sourceUrl);
    }

    public String getDescription() {
        return desc;
    }

    public boolean hasDescription() {
        return AmadeusUtils.stringIsNotEmpty(desc);
    }

    public String getCredits() {
        return credits;
    }

    public boolean hasCredits() {
        return AmadeusUtils.stringIsNotEmpty(credits);
    }

    public String getInvite() {
        return invite;
    }

    public boolean hasInvite() {
        return AmadeusUtils.stringIsNotEmpty(invite);
    }

    public String getBotSite() {
        return botSite;
    }

    public boolean hasBotSite() {
        return AmadeusUtils.stringIsNotEmpty(botSite);
    }

    public String getVersion() {
        return version;
    }

    public boolean hasVersion() {
        return AmadeusUtils.stringIsNotEmpty(version);
    }

    public EmbedBuilder toEmbedBuilder(IGuild guild, IUser user) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName(References.LIB_NAME + " > " + getName());
        builder.appendField(core.langs.translate(guild, user, "bot_amadeus_version"), References.LIB_VERSION, true);
        if (hasVersion())
            builder.appendField(core.langs.translate(guild, user, "bot_version"), getVersion(), true);
        if (hasDescription())
            builder.appendField(core.langs.translate(guild, user, "bot_description"), getDescription(), true);
        if (hasCredits())
            builder.appendField(core.langs.translate(guild, user, "bot_credits"), getCredits(), true);
        if (hasSourceUrl())
            builder.appendField(core.langs.translate(guild, user, "bot_source"), getSourceUrl(), true);
        if (hasBotSite())
            builder.appendField(core.langs.translate(guild, user, "bot_site"), getBotSite(), true);
        StringBuilder sbuilder = new StringBuilder();
        for (long id : devIds) {
            IUser u = core.client.getUserByID(id);
            if (u != null)
                sbuilder.append(u.mention(true) + " ");
        }
        builder.appendField(core.langs.translate(guild, user, "bot_devs"), sbuilder.toString().trim(), false);
        return builder;
    }

    public boolean valid() {
        return AmadeusUtils.stringIsNotEmpty(name) && devIds != null && devIds.length > 0 && folder != null;
    }
}
