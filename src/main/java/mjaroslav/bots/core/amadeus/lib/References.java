package mjaroslav.bots.core.amadeus.lib;

public class References {
    public static final String LIB_NAME = "AmadeusCore";
    public static final String LIB_TYPE = "alpha";
    public static final String LIB_VERSION = "2.0.0.0" + "-" + LIB_TYPE;

    public static final String FOLDER_MAIN = "assets";
    public static final String FOLDER_LANGS = FOLDER_MAIN + "/langs";
    public static final String FOLDER_DATABASES = FOLDER_MAIN + "/databases";

    public static final String FILE_BOTINFO = FOLDER_MAIN + "/bot.info";
    public static final String FILE_TOKEN = FOLDER_MAIN + "/bot.token";
    
    public static final String EXT_DATABASE = "db";
    public static final String EXT_LANG = "lang";
    
    public static final String PATTERN_FILE_LANG = "%s." + EXT_LANG;
    public static final String PATTERN_FILE_DATABASE = "%s." + EXT_DATABASE;
}
