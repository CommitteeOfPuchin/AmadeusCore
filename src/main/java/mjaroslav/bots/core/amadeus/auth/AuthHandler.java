package mjaroslav.bots.core.amadeus.auth;

import java.io.File;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class AuthHandler {
    public final AmadeusCore core;

    public AuthHandler(AmadeusCore core) {
        this.core = core;
    }

    public abstract boolean saveToken(String token) throws Exception;

    public abstract String loadToken() throws Exception;

    public abstract File getFolder();

    public final File getFile() {
        return getFolder().toPath().resolve(core.info.getName() + ".json").toFile();
    }
}
