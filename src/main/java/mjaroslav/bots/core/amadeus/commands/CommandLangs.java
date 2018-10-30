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
                if (!canUseArg(source, "list"))
                    return;
                StringBuilder builder = new StringBuilder();
                builder.append(core.langs.translate(source, "langs_list") + "\n\n");
                for (String lang : core.i18n.getLangNamesTranslated())
                    builder.append(lang + "\n");
                core.sendDone(source, builder.toString());
            } else if (hasArg(source, "set", argsParsed)) {
                String lang = argValue(source, "set", argsParsed);
                if (lang != null) {
                    if (core.i18n.getLangNames().contains(lang)) {
                        if (hasArg(source, "set.channel", argsParsed)) {
                            if (!canUseArg(source, "set.channel"))
                                return;
                            if (!core.isPrivateMessage(source)) {
                                core.langs.setLangToChannel(source.getChannel(), lang);
                                core.sendDone(source, core.langs.translate(source, "langs_set_channel",
                                        core.i18n.getLangNameTranslated(lang), source.getChannel().mention()));
                            } else
                                core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                        } else if (hasArg(source, "set.guild", argsParsed)) {
                            if (!canUseArg(source, "set.guild"))
                                return;
                            if (!core.isPrivateMessage(source)) {
                                core.langs.setLangToGuild(source.getGuild(), lang);
                                core.sendDone(source, core.langs.translate(source, "langs_set_guild",
                                        core.i18n.getLangNameTranslated(lang), source.getGuild().getName()));
                            } else
                                core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                        } else {
                            if (!canUseArg(source, "set"))
                                return;
                            core.langs.setLangToUser(sender, lang);
                            core.sendDone(source,
                                    core.langs.translate(source, "langs_set", core.i18n.getLangNameTranslated(lang)));
                        }
                    } else
                        core.sendError(source, core.langs.translate(source, "langs_not_found", lang));
                } else
                    core.sendError(source, core.langs.translate(source, "answer_bad_args"));
            } else if (hasArg(source, "reset", argsParsed)) {
                if (hasArg(source, "reset.channel", argsParsed)) {
                    if (!canUseArg(source, "reset.channel"))
                        return;
                    if (!core.isPrivateMessage(source)) {
                        core.langs.resetLangFromChannel(source.getChannel());
                        core.sendDone(source,
                                core.langs.translate(source, "langs_set_channel",
                                        core.i18n.getLangNameTranslated(core.langs.getLang(source)),
                                        source.getChannel().mention()));
                    } else
                        core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                } else if (hasArg(source, "reset.guild", argsParsed)) {
                    if (!canUseArg(source, "reset.guild"))
                        return;
                    if (!core.isPrivateMessage(source)) {
                        core.langs.resetLangFromGuild(source.getGuild());
                        core.sendDone(source,
                                core.langs.translate(source, "langs_set_guild",
                                        core.i18n.getLangNameTranslated(core.langs.getLang(source)),
                                        source.getGuild().getName()));
                    } else
                        core.sendError(source, core.langs.translate(source, "answer_no_pm"));
                } else {
                    if (!canUseArg(source, "set"))
                        return;
                    core.langs.resetLangFromUser(sender);
                    core.sendDone(source, core.langs.translate(source, "langs_set",
                            core.i18n.getLangNameTranslated(core.langs.getLang(source))));
                }
            } else
                core.sendError(source, core.langs.translate(source, "answer_bad_args"));
        } else {
            if (core.isPrivateMessage(source))
                core.sendDone(source, core.langs.translate(source, "langs_current",
                        core.i18n.getLangNameTranslated(core.langs.getLang(source))));
            else {
                core.sendDone(source, core.langs.translate(source, "langs_current_guild",
                        core.i18n.getLangNameTranslated(core.langs.getLang(source)), source.getChannel().mention(),
                        core.i18n.getLangNameTranslated(core.langs.getLang(null, source.getChannel(), null)),
                        source.getGuild().getName(),
                        core.i18n.getLangNameTranslated(core.langs.getLang(source.getGuild(), null, null))));
            }
        }
    }

    @Override
    public List<String> getArgsList() {
        return Arrays.asList("set", "list", "set.guild", "set.channel", "reset.guild", "reset.channel", "reset");
    }
}
