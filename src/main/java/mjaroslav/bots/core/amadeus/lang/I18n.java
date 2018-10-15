package mjaroslav.bots.core.amadeus.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.lib.References;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public class I18n {
    public static final String defaultLang = "english";
    public static final FilenameFilter LANGEXTFILTER = AmadeusUtils.getFilenameExtFilter(References.EXT_LANG);
    public static final File FOLDER = new File(References.FOLDER_LANGS);
    private static final HashMap<String, String> EMPTYMAP = new HashMap<>();
    private static final HashMap<String, HashMap<String, String>> STORAGE = new HashMap<>();

    public static final String KEY_TRANSLATED_NAME = "translated_name";
    public static final String KEY_FLAG_EMOJI = "flag_emoji";

    public static void loadLangs() {
        STORAGE.clear();
        if (folderExists())
            for (File file : FOLDER.listFiles(LANGEXTFILTER)) {
                String langName = FilenameUtils.removeExtension(file.getName());
                if (!STORAGE.containsKey(langName))
                    STORAGE.put(langName, new HashMap<>());
                try {
                    BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                    String line = reader.readLine();
                    int index = -1;
                    int linePos = 0;
                    while (line != null) {
                        index = line.indexOf("=");
                        if (index > 0)
                            STORAGE.get(langName).put(line.substring(0, index),
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

    public static String translate(String key, Object... args) {
        return translate(defaultLang, key, args);
    }

    public static String translate(String lang, String key, Object... args) {
        return String.format(
                STORAGE.getOrDefault(lang, STORAGE.getOrDefault(defaultLang, EMPTYMAP)).getOrDefault(key, key), args);
    }

    public static List<String> getLangNames() {
        return new ArrayList<String>(STORAGE.keySet());
    }

    public static List<String> getLangNamesTranslated() {
        ArrayList<String> result = new ArrayList<>();
        for (Entry<String, HashMap<String, String>> entry : STORAGE.entrySet())
            result.add((entry.getValue().getOrDefault(KEY_FLAG_EMOJI, "").isEmpty() ? ""
                    : entry.getValue().getOrDefault(KEY_FLAG_EMOJI, "") + " ")
                    + (entry.getValue().getOrDefault(KEY_TRANSLATED_NAME, "").isEmpty() ? entry.getKey()
                            : entry.getValue().getOrDefault(KEY_TRANSLATED_NAME, "") + " [" + entry.getKey() + "]"));
        return result;
    }

    public static File getLangFile(String lang) {
        return FOLDER.toPath().resolve(String.format(References.PATTERN_FILE_LANG, lang)).toFile();
    }

    public static boolean folderExists() {
        if ((FOLDER.exists() && FOLDER.isDirectory()) || FOLDER.mkdirs())
            return true;
        return false;
    }
}
