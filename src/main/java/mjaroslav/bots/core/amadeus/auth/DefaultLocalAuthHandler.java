package mjaroslav.bots.core.amadeus.auth;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.JSONUtils;

public class DefaultLocalAuthHandler extends AuthHandler {
    public DefaultLocalAuthHandler(AmadeusCore core) {
        super(core);
    }

    @Override
    public File getFolder() {
        return core.info.getFolder();
    }

    @Override
    public String loadToken() throws JSONException, IOException {
        File file = getFolder();
        if ((file.exists() && file.isDirectory())) {
            file = getFile();
            if ((file.exists() && file.isFile()))
                return JSONUtils.fromJson(file, JSONObject.class).getString("token");
        }
        return null;
    }

    @Override
    public boolean saveToken(String token) throws Exception {
        File file = getFolder();
        if ((file.exists() && file.isDirectory()) || file.mkdirs()) {
            file = getFile();
            if ((file.exists() && file.isFile()) || file.createNewFile()) {
                JSONUtils.toJson(file, new JSONObject().append("token", token), true);
                return true;
            }
        }
        return false;
    }
}
