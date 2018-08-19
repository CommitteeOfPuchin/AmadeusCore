package mjaroslav.bots.core.amadeus;

import java.io.File;
import java.nio.file.Path;

import mjaroslav.bots.core.amadeus.auth.AuthHandler;
import mjaroslav.bots.core.amadeus.auth.DefaultAuthHandler;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class AmadeusCore {
    public final String name;
    public IDiscordClient client;
    public AuthHandler authHandler;
    private boolean isReady = false;
    public final Path folder;

    public AmadeusCore(String name, String folder) {
        this.name = name;
        this.folder = new File(folder).toPath();
    }

    public boolean auth() {
        try {
            client = new ClientBuilder().withToken(getAuthHandler().loadToken()).login();
            client.getDispatcher().registerListener(new EventHandler(this));
            int counter = 0;
            while (!isReady && counter < 20) {
                Thread.sleep(3000);
                counter++;
            }
            return isReady;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void disableBot() {
        if (client != null)
            client.logout();
    }

    public AuthHandler getAuthHandler() {
        if (authHandler == null)
            authHandler = new DefaultAuthHandler(this);
        return authHandler;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public static class EventHandler {
        private final AmadeusCore core;

        public EventHandler(AmadeusCore core) {
            this.core = core;
        }

        @EventSubscriber
        public void onReady(ReadyEvent event) {
            core.isReady = true;
        }

        @EventSubscriber
        public void onMessage(MessageReceivedEvent event) {

        }
    }
}
