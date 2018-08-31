package mjaroslav.bots.core.amadeus.auth;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.google.gson.annotations.SerializedName;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils.Action;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;

public class DefaultUserHomeAuthHandler extends AuthHandler {
    public static final String TOKENPLACEHOLDER = "ENTER TOKEN HERE AND CLOSE";

    public DefaultUserHomeAuthHandler(AmadeusCore core) {
        super(core);
    }

    @Override
    public String loadToken() {
        try {
            if (getFolder().exists() && getFolder().isDirectory()) {
                File dir = getFolder();
                if (dir.exists() && dir.isDirectory()) {
                    File tokenFile = getFile();
                    if (tokenFile.exists() && tokenFile.isFile()) {
                        String result = JSONUtils.fromJson(tokenFile, TokenObject.class).token;
                        if (result.equals(TOKENPLACEHOLDER))
                            return waitToken();
                        else
                            return result;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return waitToken();
    }

    @Override
    public boolean saveToken(String token) {
        try {
            if ((getFolder().exists() && getFolder().isDirectory()) || getFolder().mkdirs()) {
                File dir = getFolder();
                if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
                    File tokenFile = getFile();
                    if ((tokenFile.exists() && tokenFile.isFile()) || tokenFile.createNewFile()) {
                        JSONUtils.toJson(tokenFile, new TokenObject(token), false);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String waitToken() {
        File dir = getFolder();
        File file = getFile();
        try {
            if (((dir.exists() && dir.isDirectory()) || dir.mkdirs())
                    && ((file.exists() && file.isFile()) || file.createNewFile()) && Desktop.isDesktopSupported()) {
                TokenObject token = JSONUtils.fromJson(file, TokenObject.class);
                if (token == null || AmadeusUtils.stringIsEmpty(token.token)) {
                    token = new TokenObject();
                    JSONUtils.toJson(file, token, true);
                }
                Desktop.getDesktop().open(file);
                AmadeusUtils.waitAction(360000L, new Action() {
                    @Override
                    public boolean done() {
                        try {
                            return !JSONUtils.fromJson(file, TokenObject.class).token.equals(TOKENPLACEHOLDER);
                        } catch (IOException e) {
                            return true;
                        }
                    }
                });
                return JSONUtils.fromJson(file, TokenObject.class).token;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File getFolder() {
        return new File(System.getProperty("user.home") + "/.mjapies/amadeuscore");
    }

    public static class TokenObject {
        @SerializedName("token")
        public String token;

        public TokenObject() {
            token = TOKENPLACEHOLDER;
        }

        public TokenObject(String token) {
            this.token = token;
        }
    }

}
