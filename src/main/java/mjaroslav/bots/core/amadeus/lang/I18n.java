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
import mjaroslav.bots.core.amadeus.lib.References;

public class I18n {
    private static String defaultLang = "english";
    private String currentLang = defaultLang;

    public static final File FOLDER = new File(References.FOLDER_LANGS);

    private static final HashMap<String, String> DEFAULT = new HashMap<>();
    private final HashMap<String, String> CURRENT = new HashMap<>();

    public I18n() {
        updateDefaultStorage();
    }

    public I18n(String currentLang) {
        this.currentLang = currentLang;
        updateCurrentStorage();
    }

    public static void updateDefaultStorage() {
        DEFAULT.clear();
        File file = getLangFile(defaultLang);
        if (!(file.exists() && file.isFile()))
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (file.exists() && file.isFile())
            try {
                BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                String line = reader.readLine();
                int index = -1;
                int linePos = 0;
                while (line != null) {
                    index = line.indexOf("=");
                    if (index > 0)
                        DEFAULT.put(line.substring(0, index), line.substring(index + 1, line.length()));
                    else
                        throw new IllegalArgumentException(
                                String.format(defaultLang + ": error on line %s:%s", linePos, line));
                    index = -1;
                    linePos++;
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void updateCurrentStorage() {
        CURRENT.clear();
        File file = getLangFile(currentLang);
        if (file.exists() && file.isFile())
            try {
                BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                String line = reader.readLine();
                int index = -1;
                int linePos = 0;
                while (line != null) {
                    index = line.indexOf("=");
                    if (index > 0)
                        CURRENT.put(line.substring(0, index), line.substring(index + 1, line.length()));
                    else
                        throw new IllegalArgumentException(
                                String.format(currentLang + ": error on line %s:%s", linePos, line));
                    index = -1;
                    linePos++;
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String currentLang() {
        return CURRENT.getOrDefault("locate", currentLang);
    }

    public void setLanguage(String key) {
        currentLang = key;
        updateCurrentStorage();
    }

    public static void setDefaultLanguage(String key) {
        defaultLang = key;
        updateDefaultStorage();
    }

    public String translate(String key, Object... args) {
        return String.format(CURRENT.getOrDefault(key, DEFAULT.getOrDefault(key, key)), args);
    }

    public static List<File> getLangFiles() {
        if (!folderExists())
            return Collections.emptyList();
        ArrayList<File> result = new ArrayList<>();
        for (File file : FOLDER.listFiles())
            if (file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(References.EXT_LANG))
                result.add(file);
        return result;
    }

    public static List<String> getLangNames() {
        ArrayList<String> result = new ArrayList<>();
        for (File file : getLangFiles())
            result.add(file.getName().substring(0, file.getName().length() - References.EXT_LANG.length() - 1));
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
