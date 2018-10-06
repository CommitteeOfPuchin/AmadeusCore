package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class CommandBotInfo extends BaseCommand {
    public CommandBotInfo(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "botinfo");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        StringBuilder answer = new StringBuilder();
        ArrayList<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
        if (!argsParsed.isEmpty()) {
            if (hasArgAt("discord", 0, argsParsed)) {
                if (hasArgAt("perms", 1, argsParsed)) {
                    if (hasArgAt("list", 2, argsParsed))
                        for (Permissions permission : Permissions.values())
                            answer.append("`" + permission.name() + "` ");
                    else {
                        for (Permissions permission : core.getClient().getOurUser()
                                .getPermissionsForGuild(source.getGuild()))
                            answer.append("`" + permission.name() + "` ");
                    }
                } else if (hasArgAt("emoji", 1, argsParsed)) {
                    long id = source.getGuild().getLongID();
                    if (argsParsed.size() > 2)
                        id = Long.parseLong(argsParsed.get(2));
                    for (IEmoji emoji : core.getClient().getGuildByID(id).getEmojis())
                        if (!emoji.isAnimated())
                            answer.append("<:" + emoji.getName() + ":" + emoji.getStringID() + "> ");
                } else if (hasArgAt("roles", 1, argsParsed)) {
                    if (hasArgAt("list", 2, argsParsed))
                        for (IRole role : source.getGuild().getRoles())
                            answer.append(role.getName() + " - " + role.getLongID() + "\n");
                    else {
                        for (IRole role : core.getClient().getOurUser().getRolesForGuild(source.getGuild()))
                            answer.append(role.getName() + " - " + role.getLongID() + "\n");
                    }
                }
            }
        }
        answerDone(source, answer.toString());
    }

    @Override
    public boolean onlyOwner() {
        return true;
    }
}
