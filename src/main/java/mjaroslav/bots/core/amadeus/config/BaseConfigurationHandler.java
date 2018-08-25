package mjaroslav.bots.core.amadeus.config;

import com.google.gson.annotations.SerializedName;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.config.BaseConfigurationHandler.DefaultConfigurationHandler.DefaultConfigurationJSON;
import mjaroslav.bots.core.amadeus.lang.DefaultLangHandler;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public abstract class BaseConfigurationHandler<T> extends ConfigurationHandler<T> {
    public final JSONReader<T> reader;

    public BaseConfigurationHandler(AmadeusCore core, String name, T defaultObject, boolean pretty) {
        super(core, name);
        reader = new JSONReader<T>(defaultObject, getFolder().toPath().resolve(name + ".json").toFile(), pretty);
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
    public T getConfig() {
        return reader.json;
    }

    public static class DefaultConfigurationHandler extends BaseConfigurationHandler<DefaultConfigurationJSON> {
        public DefaultConfigurationHandler(AmadeusCore core, String name, boolean pretty) {
            super(core, name, new DefaultConfigurationJSON(), pretty);
        }

        @Override
        public void readConfig() throws Exception {
            super.readConfig();
            core.getLangHandler().setLang(getConfig().lang);
        }

        public static class DefaultConfigurationJSON {
            @SerializedName("language")
            public String lang = DefaultLangHandler.defaultLang;
        }
    }
}
