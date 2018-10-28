package mjaroslav.bots.core.amadeus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.AmadeusUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandLangs extends BaseCommand {
    public CommandLangs(AmadeusCore core, CommandHandler handler) {
        super(core, handler, "langs");
    }

    @Override
    public void execute(IUser sender, IMessage source, String args) throws Exception {
        if (AmadeusUtils.stringIsNotEmpty(args)) {
            ArrayList<String> argsParsed = AmadeusUtils.parseArgsToArray(args);
            if (hasArg(source, "list", argsParsed)) {
                StringBuilder builder = new StringBuilder();
                builder.append(core.langs.translate(source, "langs_list") + "\n\n");
                for (String lang : core.i18n.getLangNamesTranslated())
                    builder.append(lang + "\n");
                core.sendDone(source, builder.toString());
            } else if (hasArg(source, "set", argsParsed)) {
                String lang = argValue(source, "set", argsParsed);
                if (lang != null) {
                    if (core.i18n.getLangNames().contains(lang)) {
                        if (hasArg(source, "set.guild", argsParsed)) {
                            if (!canUseArg(source, "set.guild"))
                                return;
                            if (!core.isPrivateMessage(source)) {
                                core.langs.setLangToGuild(source.getGuild(), lang);
                                core.sendDone(source, core.langs.translate(source, "langs_set_guild",
                                        core.i18n.getLangNameTranslated(lang), source.getGuild().getName()));
                            } else
                                core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                        } else {
                            core.langs.setLangToUser(sender, lang);
                            core.sendDone(source,
                                    core.langs.translate(source, "langs_set", core.i18n.getLangNameTranslated(lang)));
                        }
                    } else
                        core.sendError(source, core.langs.translate(source, "langs_not_found", lang));
                } else
                    core.sendError(source, core.langs.translate(source, "answer_bad_args"));
            } else
                core.sendError(source, core.langs.translate(source, "answer_bad_args"));
        } else {
            core.sendDone(source, core.langs.translate(source, "langs_current",
                    "\n" + core.i18n.getLangNameTranslated(core.langs.getLang(source))));
        }
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("set", "list", "set.guild");
    }
}
