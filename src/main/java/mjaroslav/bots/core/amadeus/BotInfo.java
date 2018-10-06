package mjaroslav.bots.core.amadeus;

import java.io.File;
import com.google.gson.annotations.SerializedName;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
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
    @SerializedName("logo_url")
    private String logoUrl;
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public boolean hasLogoUrl() {
        return AmadeusUtils.stringIsNotEmpty(logoUrl);
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

    public EmbedBuilder toEmbedBuilder() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withAuthorName(AmadeusInfo.NAME + " > " + getName());
        builder.appendField(core.translate("core.amadeusversion"), AmadeusInfo.VERSION, true);
        if (hasVersion())
            builder.appendField(core.translate("core.botversion"), getVersion(), true);
        if (hasDescription())
            builder.appendField(core.translate("core.botdescription"), getDescription(), true);
        if (hasCredits())
            builder.appendField(core.translate("core.botcredits"), getCredits(), true);
        if (hasSourceUrl())
            builder.appendField(core.translate("core.botsource"), getSourceUrl(), true);
        if (hasBotSite())
            builder.appendField(core.translate("core.botsite"), getBotSite(), true);
        StringBuilder sbuilder = new StringBuilder();
        for (long id : devIds) {
            IUser user = core.getClient().getUserByID(id);
            if (user != null)
                sbuilder.append(user.mention(true) + " ");
        }
        builder.appendField(core.translate("core.devs"), sbuilder.toString().trim(), false);
        builder.withThumbnail(
                hasLogoUrl() ? getLogoUrl() : core.getClient().getOurUser().getAvatarURL().replace("webp", "png"));
        return builder;
    }

    public boolean valid() {
        return AmadeusUtils.stringIsNotEmpty(name) && devIds != null && devIds.length > 0 && folder != null;
    }
}
