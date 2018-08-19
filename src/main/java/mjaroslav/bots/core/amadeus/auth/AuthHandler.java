package mjaroslav.bots.core.amadeus.auth;

import java.io.File;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class AuthHandler {
	private final AmadeusCore core;

	public AuthHandler(AmadeusCore core) {
		this.core = core;
	}

	public abstract boolean saveToken(String token);

	public abstract String loadToken();

	public abstract File getFolder();

	public final File getFile() {
		return getFolder().toPath().resolve(core.getName() + ".json").toFile();
	}
}
