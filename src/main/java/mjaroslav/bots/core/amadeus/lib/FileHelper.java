package mjaroslav.bots.core.amadeus.lib;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Path;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public class FileHelper {
    public static final String EXT_DATABASE = "db";
    public static final String EXT_LANG = "lang";
    public static final String EXT_JSON = "json";
    public static final String EXT_CMDS = "cmds";
    public static final String EXT_PRMS = "prms";

    public static final FilenameFilter LANG_EXT_FILTER = AmadeusUtils.getFilenameExtFilter(EXT_LANG);
    public static final FilenameFilter PRMS_EXT_FILTER = AmadeusUtils.getFilenameExtFilter(EXT_PRMS);
    public static final FilenameFilter CMDS_EXT_FILTER = AmadeusUtils.getFilenameExtFilter(EXT_CMDS);

    public static File fileBotToken() {
        return new File("bot.token");
    }

    public static Path folderBot(AmadeusCore core) {
        return core.info.getFolder().toPath();
    }

    public static String folderAssets() {
        return "./amadeus";
    }

    public static String folderDefaultLanguages() {
        return folderAssets() + "/languages";
    }

    public static Path folderDatabases(AmadeusCore core) {
        return folderBot(core).resolve("databases");
    }

    public static Path folderPermissions(AmadeusCore core) {
        return folderBot(core).resolve("permissions");
    }

    public static Path folderLanguages(AmadeusCore core) {
        return folderBot(core).resolve("languages");
    }

    public static Path folderConfigurations(AmadeusCore core) {
        return folderBot(core).resolve("configurations");
    }

    public static File fileDatabase(AmadeusCore core, String name) {
        return folderDatabases(core).resolve(name + ".db").toFile();
    }

    public static File filePermissionsDatabase(AmadeusCore core) {
        return folderDatabases(core).resolve("permissions.db").toFile();
    }

    public static InputStream streamDefaultLangList() {
        return AmadeusCore.class.getClassLoader().getResourceAsStream(folderDefaultLanguages() + "/langlist.txt");
    }

    public static InputStream streamPermissionsDefault() {
        return AmadeusCore.class.getClassLoader().getResourceAsStream(folderAssets() + "/default." + EXT_PRMS);
    }

    public static InputStream streamPermissionsPrivateDefault() {
        return AmadeusCore.class.getClassLoader().getResourceAsStream(folderAssets() + "/defaultprivate." + EXT_PRMS);
    }

    public static File filePermissions(AmadeusCore core, long guildId) {
        return folderPermissions(core).resolve(guildId + "." + EXT_PRMS).toFile();
    }

    public static File filePermissionsPrivate(AmadeusCore core) {
        return folderPermissions(core).resolve("private_messages." + EXT_PRMS).toFile();
    }

    public static File fileLanguagesDatabase(AmadeusCore core) {
        return folderDatabases(core).resolve("languages." + EXT_DATABASE).toFile();
    }

    public static File fileLanguageCommandsCustom(AmadeusCore core, String name) {
        return folderLanguages(core).resolve(name + "." + EXT_CMDS).toFile();
    }

    public static File fileLanguageCustom(AmadeusCore core, String name) {
        return folderLanguages(core).resolve(name + "." + EXT_LANG).toFile();
    }

    public static InputStream streamLanguageCommands(String name) {
        return AmadeusCore.class.getClassLoader()
                .getResourceAsStream(folderDefaultLanguages() + "/" + name + "." + EXT_CMDS);
    }

    public static InputStream streamLanguage(String name) {
        return AmadeusCore.class.getClassLoader()
                .getResourceAsStream(folderDefaultLanguages() + "/" + name + "." + EXT_LANG);
    }
}
