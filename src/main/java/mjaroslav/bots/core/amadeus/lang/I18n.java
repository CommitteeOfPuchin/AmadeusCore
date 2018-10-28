package mjaroslav.bots.core.amadeus.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public class I18n {
    public static final String defaultLang = "english";

    private static final HashMap<String, String> EMPTYMAP_LANG = new HashMap<>();
    private static final HashMap<String, List<String>> EMPTYMAP_COMMANDS = new HashMap<>();

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

    public void loadDefaultLangs() {
        String[] langs = new String[] {};
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(FileHelper.streamDefaultLangList(), StandardCharsets.UTF_8));
            langs = reader.readLine().split(",");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String lang : langs) {
            if (!STORAGE_COMMANDS.containsKey(lang))
                STORAGE_COMMANDS.put(lang, new HashMap<>());
            try {
                STORAGE_COMMANDS.get(lang).putAll(AmadeusUtils.parseHashMapStringStringList(
                        new BufferedReader(new InputStreamReader(FileHelper.streamLanguageCommands(lang))),
                        "JAR:" + FileHelper.folderDefaultLanguages() + "/" + lang + "." + FileHelper.EXT_CMDS, false));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (!STORAGE_LANGS.containsKey(lang))
                STORAGE_LANGS.put(lang, new HashMap<>());
            try {
                STORAGE_LANGS.get(lang).putAll(AmadeusUtils.parseHashMapStringString(
                        new BufferedReader(
                                new InputStreamReader(FileHelper.streamLanguage(lang), StandardCharsets.UTF_8)),
                        "JAR:" + FileHelper.folderDefaultLanguages() + "/" + lang + "." + FileHelper.EXT_LANG, false));
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void loadLangs() {
        STORAGE_LANGS.clear();
        STORAGE_COMMANDS.clear();
        loadDefaultLangs();
        String langName;
        for (File file : FOLDER.listFiles(FileHelper.CMDS_EXT_FILTER)) {
            langName = FilenameUtils.removeExtension(file.getName());
            if (!STORAGE_COMMANDS.containsKey(langName))
                STORAGE_COMMANDS.put(langName, new HashMap<>());
            try {
                STORAGE_COMMANDS.get(langName)
                        .putAll(AmadeusUtils.parseHashMapStringStringList(
                                Files.newBufferedReader(FileHelper.fileLanguageCommandsCustom(core, langName).toPath()),
                                FileHelper.fileLanguageCommandsCustom(core, langName).getAbsolutePath(), false));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        for (File file : FOLDER.listFiles(FileHelper.LANG_EXT_FILTER)) {
            langName = FilenameUtils.removeExtension(file.getName());
            if (!STORAGE_LANGS.containsKey(langName))
                STORAGE_LANGS.put(langName, new HashMap<>());
            try {
                STORAGE_LANGS.get(langName).putAll(AmadeusUtils.parseHashMapStringString(
                        new BufferedReader(
                                new InputStreamReader(FileHelper.streamLanguage(langName), StandardCharsets.UTF_8)),
                        FileHelper.fileLanguageCustom(core, langName).getAbsolutePath(), false));
            } catch (IllegalArgumentException | IOException e) {
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
        result.add(argKey.split("\\.")[argKey.split("\\.").length - 1]);
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
                .getOrDefault(key, key), args).replace("\\n", "\n");
    }

    public List<String> getLangNames() {
        return new ArrayList<String>(STORAGE_LANGS.keySet());
    }

    public String getLangNameTranslated(String lang) {
        if (!STORAGE_LANGS.containsKey(lang))
            return lang;

        return (STORAGE_LANGS.get(lang).getOrDefault(KEY_FLAG_EMOJI, "").isEmpty() ? ""
                : STORAGE_LANGS.get(lang).getOrDefault(KEY_FLAG_EMOJI, "") + " ")
                + (STORAGE_LANGS.get(lang).getOrDefault(KEY_TRANSLATED_NAME, "").isEmpty() ? STORAGE_LANGS.get(lang)
                        : STORAGE_LANGS.get(lang).getOrDefault(KEY_TRANSLATED_NAME, "") + " [" + lang + "]");
    }

    public List<String> getLangNamesTranslated() {
        ArrayList<String> result = new ArrayList<>();
        for (String name : getLangNames())
            result.add(getLangNameTranslated(name));
        return result;
    }

    public int count() {
        return STORAGE_LANGS.size();
    }
}
