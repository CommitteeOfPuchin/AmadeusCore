package mjaroslav.bots.core.amadeus.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;

public class I18n {
    public static final String defaultLang = "english";

    public static final HashMap<String, String> EMPTYMAP_LANG = new HashMap<>();
    public static final HashMap<String, List<String>> EMPTYMAP_COMMANDS = new HashMap<>();

    public static final String KEY_TRANSLATED_NAME = "translated_name";
    public static final String KEY_FLAG_EMOJI = "flag_emoji";

    public final AmadeusCore core;
    public final File FOLDER;
    private final HashMap<String, HashMap<String, String>> STORAGE_LANGS = new HashMap<>();
    private final HashMap<String, HashMap<String, List<String>>> STORAGE_COMMANDS = new HashMap<>();

    public I18n(AmadeusCore core) {
        this.core = core;
        FOLDER = FileHelper.folderLanguages(core).toFile();
    }

    @SuppressWarnings("unchecked")
    public void loadLangs() {
        STORAGE_LANGS.clear();
        STORAGE_COMMANDS.clear();
        for (File file : FOLDER.listFiles(FileHelper.LANGEXTFILTER)) {
            String langName = FilenameUtils.removeExtension(file.getName());
            if (!STORAGE_COMMANDS.containsKey(langName))
                STORAGE_COMMANDS.put(langName, new HashMap<>());
            if (AmadeusUtils.existsOrCreateFile(FileHelper.fileLanguageCommands(core, langName)))
                try {
                    STORAGE_COMMANDS.put(langName, JSONUtils.fromJson(FileHelper.fileLanguageCommands(core, langName),
                            EMPTYMAP_COMMANDS.getClass()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if (!STORAGE_LANGS.containsKey(langName))
                STORAGE_LANGS.put(langName, new HashMap<>());
            try {
                BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                String line = reader.readLine();
                int index = -1;
                int linePos = 0;
                while (line != null) {
                    index = line.indexOf("=");
                    if (index > 0)
                        STORAGE_LANGS.get(langName).put(line.substring(0, index),
                                line.substring(index + 1, line.length()));
                    else
                        throw new IllegalArgumentException(
                                String.format(langName + ": error on line %s:%s", linePos, line));
                    index = -1;
                    linePos++;
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getNamesCustom(String key) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(key);
        result.addAll(STORAGE_COMMANDS.getOrDefault(defaultLang, EMPTYMAP_COMMANDS).getOrDefault(key,
                Collections.emptyList()));
        result.sort(AmadeusUtils.LENGTH_SORTER);
        return result;
    }

    public List<String> getNamesCustom(String lang, String key) {
        ArrayList<String> result = new ArrayList<String>(getNamesCustom(key));
        result.addAll(
                STORAGE_COMMANDS.getOrDefault(lang, EMPTYMAP_COMMANDS).getOrDefault(key, Collections.emptyList()));
        result.sort(AmadeusUtils.LENGTH_SORTER);
        return result;
    }

    public List<String> getNames(String handlerKey, String commandKey) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(commandKey);
        result.addAll(STORAGE_COMMANDS.getOrDefault(defaultLang, EMPTYMAP_COMMANDS)
                .getOrDefault(handlerKey + "." + commandKey, Collections.emptyList()));
        result.sort(AmadeusUtils.LENGTH_SORTER);
        return result;
    }

    public List<String> getNames(String lang, String handlerKey, String commandKey) {
        ArrayList<String> result = new ArrayList<String>(getNames(handlerKey, commandKey));
        result.addAll(STORAGE_COMMANDS.getOrDefault(lang, EMPTYMAP_COMMANDS).getOrDefault(handlerKey + "." + commandKey,
                Collections.emptyList()));
        result.sort(AmadeusUtils.LENGTH_SORTER);
        return result;
    }

    public List<String> getNamesArg(String handlerKey, String commandKey, String argKey) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(argKey);
        result.addAll(STORAGE_COMMANDS.getOrDefault(defaultLang, EMPTYMAP_COMMANDS)
                .getOrDefault(handlerKey + "." + commandKey + "." + argKey, Collections.emptyList()));
        result.sort(AmadeusUtils.LENGTH_SORTER);
        return result;
    }

    public List<String> getNamesArg(String lang, String handlerKey, String commandKey, String argKey) {
        ArrayList<String> result = new ArrayList<String>(getNamesArg(handlerKey, commandKey, argKey));
        result.addAll(STORAGE_COMMANDS.getOrDefault(lang, EMPTYMAP_COMMANDS)
                .getOrDefault(handlerKey + "." + commandKey + "." + argKey, Collections.emptyList()));
        result.sort(AmadeusUtils.LENGTH_SORTER);
        return result;
    }

    public String translate(String key, Object... args) {
        return translate(defaultLang, key, args);
    }

    public String translate(String lang, String key, Object... args) {
        return String.format(STORAGE_LANGS.getOrDefault(lang, STORAGE_LANGS.getOrDefault(defaultLang, EMPTYMAP_LANG))
                .getOrDefault(key, key), args);
    }

    public List<String> getLangNames() {
        return new ArrayList<String>(STORAGE_LANGS.keySet());
    }

    public List<String> getLangNamesTranslated() {
        ArrayList<String> result = new ArrayList<>();
        for (Entry<String, HashMap<String, String>> entry : STORAGE_LANGS.entrySet())
            result.add((entry.getValue().getOrDefault(KEY_FLAG_EMOJI, "").isEmpty() ? ""
                    : entry.getValue().getOrDefault(KEY_FLAG_EMOJI, "") + " ")
                    + (entry.getValue().getOrDefault(KEY_TRANSLATED_NAME, "").isEmpty() ? entry.getKey()
                            : entry.getValue().getOrDefault(KEY_TRANSLATED_NAME, "") + " [" + entry.getKey() + "]"));
        return result;
    }

    public int count() {
        return STORAGE_LANGS.size();
    }
}
