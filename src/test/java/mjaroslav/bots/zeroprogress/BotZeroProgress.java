package mjaroslav.bots.zeroprogress;

import mjaroslav.bots.core.amadeus.AmadeusCore;

public class BotZeroProgress extends AmadeusCore {
    public static final BotZeroProgress bot = new BotZeroProgress();

    private BotZeroProgress() {
        super("ZeroProgress Bot", "zero");
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
