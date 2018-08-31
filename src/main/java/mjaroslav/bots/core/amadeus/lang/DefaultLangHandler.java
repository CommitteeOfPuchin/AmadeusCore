package mjaroslav.bots.core.amadeus.lang;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public class DefaultLangHandler extends LangHandler {
    private static final HashMap<String, JSONReader<HashMap<String, String>>> langs = new HashMap<String, JSONReader<HashMap<String, String>>>();

    private static String currentLang;

    public DefaultLangHandler(AmadeusCore core) {
        super(core);
        currentLang = "english";
        File file = getFolder();
        if (!file.isDirectory() || !file.exists())
            file.mkdirs();
    }

    @Override
    public String translate(String key, Object... objects) {
        JSONReader<HashMap<String, String>> reader = langs.getOrDefault(currentLang, langs.get(defaultLang));
        if (reader != null) {
            if (reader.json.containsKey(key)) {
                return String.format(reader.json.get(key), objects);
            } else {
                reader.json.put(key, key);
                reader.write();
            }
        }
        return key;
    }

    @Override
    public void loadLangs() throws Exception {
        langs.clear();
        for (File file : getFolder().listFiles()) {
            String name = file.getName();
            if (file.exists() && file.isFile() && name.toLowerCase().endsWith(".json")) {
                name = name.substring(0, name.lastIndexOf(".")).toLowerCase();
                JSONReader<HashMap<String, String>> reader = new JSONReader<HashMap<String, String>>(
                        new HashMap<String, String>(), file, true);
                reader.init();
                langs.put(name, reader);
            }
        }
        create(currentLang);
        create(defaultLang);
    }

    private void create(String lang) {
        if (!langs.containsKey(lang)) {
            JSONReader<HashMap<String, String>> reader = new JSONReader<HashMap<String, String>>(
                    new HashMap<String, String>(), getFolder().toPath().resolve(currentLang + ".json").toFile(), true);
            reader.init();
            langs.put(lang, reader);
        }
    }

    @Override
    public void setLang(String newLang) throws Exception {
        for (String lang : getLangs()) {
            if (lang.toLowerCase().equals(newLang)) {
                currentLang = newLang;
                return;
            }
        }
        throw new IOException(String.format("&s lang not found!", newLang));
    }

    @Override
    public List<String> getLangs() {
        return Arrays.asList(langs.keySet().toArray(new String[] {}));
    }
}
