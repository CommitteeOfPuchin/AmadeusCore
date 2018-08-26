package mjaroslav.bots.core.amadeus.config;

import com.google.gson.annotations.SerializedName;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lang.DefaultLangHandler;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public class DefaultConfiguration extends ConfigurationHandler {
    public final JSONReader<JSONDefaultConfiguration> reader = new JSONReader<JSONDefaultConfiguration>(
            new JSONDefaultConfiguration(), getFile(), true);

    public DefaultConfiguration(AmadeusCore core) {
        super(core, "default");
        reader.init();
    }

    @Override
    public void readConfig() throws Exception {
        reader.read();
    }

    @Override
    public void writeConfig() throws Exception {
        reader.write();
    }

    @Override
    public void afterLoad() throws Exception {
        core.getLangHandler().setLang(reader.json.lang);
    }

    public static class JSONDefaultConfiguration {
        @SerializedName("language")
        public String lang = DefaultLangHandler.defaultLang;
    }
}
