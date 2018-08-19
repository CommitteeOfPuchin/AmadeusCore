package mjaroslav.bots.zeroprogress;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public class BotTest extends AmadeusCore {
    public static final BotTest bot = new BotTest();

    private BotTest() {
        super("Test Bot");
    }

    public static void main(String... args) {
        if (bot.auth()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bot.disableBot();
        }
    }
}
