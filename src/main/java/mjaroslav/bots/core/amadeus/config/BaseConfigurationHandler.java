package mjaroslav.bots.core.amadeus.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.gson.annotations.SerializedName;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.config.BaseConfigurationHandler.DefaultConfigurationHandler.DefaultConfigurationJSON;
import mjaroslav.bots.core.amadeus.lang.DefaultLangHandler;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public abstract class BaseConfigurationHandler<T> extends ConfigurationHandler {
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
    public void setValue(String fieldName, Object value) throws Exception {
        Class<?> clazz = reader.json.getClass();
        for (Field field : clazz.getFields()) {
            String name = field.getName();
            if (field.isAnnotationPresent(SerializedName.class))
                name = field.getAnnotation(SerializedName.class).value();
            if (fieldName.equals(name)) {
                int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isFinal(mods)) {
                    if (field.getType() == value.getClass())
                        field.set(reader.json, value);
                }
                break;
            }
        }
        writeConfig();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getValue(String fieldName, Class<? extends E> type) throws Exception {
        Class<?> clazz = reader.json.getClass();
        for (Field field : clazz.getFields()) {
            String name = field.getName();
            if (field.isAnnotationPresent(SerializedName.class))
                name = field.getAnnotation(SerializedName.class).value();
            if (fieldName.equals(name)) {
                int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isFinal(mods)) {
                    if (field.getType() == type) {
                        E value = (E) field.get(reader.json);
                        if (value != null)
                            return value;
                        else {
                            value = getDefaultValue(fieldName, type);
                            setValue(fieldName, value);
                            return value;
                        }
                    }
                }
                break;
            }
        }
        throw new NullPointerException(String.format("%s field not found!", fieldName));
    }

    @SuppressWarnings("unchecked")
    public <E> E getDefaultValue(String fieldName, Class<? extends E> type) throws Exception {
        Class<?> clazz = reader.defaults.getClass();
        for (Field field : clazz.getFields()) {
            String name = field.getName();
            if (field.isAnnotationPresent(SerializedName.class))
                name = field.getAnnotation(SerializedName.class).value();
            if (fieldName.equals(name)) {
                int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isFinal(mods)) {
                    if (field.getType() == type)
                        return (E) field.get(reader.defaults);
                }
                break;
            }
        }
        return null;
    }

    public static class DefaultConfigurationHandler extends BaseConfigurationHandler<DefaultConfigurationJSON> {
        public DefaultConfigurationHandler(AmadeusCore core, String name, boolean pretty) {
            super(core, name, new DefaultConfigurationJSON(), pretty);
        }

        @Override
        public void readConfig() throws Exception {
            super.readConfig();
            core.getLangHandler().setLang(getValue("language", String.class));
        }

        public static class DefaultConfigurationJSON {
            @SerializedName("language")
            public String lang = DefaultLangHandler.defaultLang;
        }
    }
}
