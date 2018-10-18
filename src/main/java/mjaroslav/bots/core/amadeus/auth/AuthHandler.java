package mjaroslav.bots.core.amadeus.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lib.FileHelper;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;

public class AuthHandler {
    public final AmadeusCore core;
    public final File FILE;

    public AuthHandler(AmadeusCore core) {
        this.core = core;
        FILE = FileHelper.fileBotToken();
    }

    public boolean saveToken(String token) {
        try {
            FILE.delete();
            FILE.createNewFile();
            BufferedWriter writer = Files.newBufferedWriter(FILE.toPath(), StandardCharsets.UTF_8);
            writer.write(token);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String loadToken() {
        if (AmadeusUtils.existsOrCreateFile(FILE)) {
            try {
                BufferedReader reader = Files.newBufferedReader(FILE.toPath(), StandardCharsets.UTF_8);
                String result = reader.readLine();
                reader.close();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
