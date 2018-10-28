package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class CommandInfo extends BaseCommand {
    public CommandInfo(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "info");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        StringBuilder answer = new StringBuilder();
        ArrayList<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (!argsParsed.isEmpty()) {
            if (hasArgAt(source, "discord", 0, argsParsed)) {
                if (!canUseArg(source, "discord"))
                    return;
                if (hasArgAt(source, "discord.perms", 1, argsParsed)) {
                    if (!canUseArg(source, "discord.perms"))
                        return;
                    if (hasArgAt(source, "list", 2, argsParsed))
                        for (Permissions permission : Permissions.values())
                            answer.append("`" + permission.name() + "` ");
                    else {
                        for (Permissions permission : core.client.getOurUser()
                                .getPermissionsForGuild(source.getGuild()))
                            answer.append("`" + permission.name() + "` ");
                    }
                } else if (hasArgAt(source, "discord.emoji", 1, argsParsed)) {
                    if (!canUseArg(source, "discord.emoji"))
                        return;
                    IGuild guild = core.argGuild(argValue(source, "discord.emoji", argsParsed));
                    if (guild == null)
                        if (!core.isPrivateMessage(source))
                            guild = source.getGuild();
                        else {
                            core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                            return;
                        }
                    for (IEmoji emoji : guild.getEmojis())
                        if (!emoji.isAnimated())
                            answer.append("<:" + emoji.getName() + ":" + emoji.getStringID() + "> ");
                } else if (hasArgAt(source, "discord.roles", 1, argsParsed)) {
                    if (!canUseArg(source, "discord.roles"))
                        return;
                    if (hasArgAt(source, "list", 2, argsParsed))
                        for (IRole role : source.getGuild().getRoles())
                            answer.append(role.getName() + " - " + role.getLongID() + "\n");
                    else {
                        for (IRole role : core.client.getOurUser().getRolesForGuild(source.getGuild()))
                            answer.append(role.getName() + " - " + role.getLongID() + "\n");
                    }
                }
            }
        }
        core.sendDone(source, answer.toString());
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("discord", "discord.perms", "discord.emoji", "discord.roles");
    }
}
