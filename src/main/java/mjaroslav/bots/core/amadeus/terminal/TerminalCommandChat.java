package mjaroslav.bots.core.amadeus.terminal;

import java.util.ArrayList;
import java.util.Arrays;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IChannel;

public class TerminalCommandChat extends BaseTerminalCommand {
    public TerminalCommandChat(AmadeusCore core, TerminalCommandHandler handler) {
        super(core, handler, "chat");
    }

    @Override
    public void execute(String args) throws Exception {
        long to = -1;
        ArrayList<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (argsParsed.size() > 0) {
            try {
                to = Long.parseLong(argsParsed.get(0));
            } catch (Exception e) {

            }
            IChannel channel = core.getClient().getChannelByID(to);
            if (channel != null) {
                String text = AmadeusUtils.removePreifx(args, core, Arrays.asList(String.valueOf(to)), true);
                core.sendMessage(to, "FROM CONSOLE:\n" + text, null);
                answer("done");
            } else
                answer("Channel not found");
        } else
            answer("Bad args");
    }
}
