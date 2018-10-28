package mjaroslav.bots.core.amadeus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.lib.BotInfo;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage.Attachment;

public class AmadeusUtils {
    public static final Comparator<String> LENGTH_SORTER = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o2.length() - o1.length();
        }
    };

    public static BotInfo getBotInfo(AmadeusCore core) {
        try {
            BotInfo result = JSONUtils.fromJson(AmadeusUtils.class.getResourceAsStream("/bot.info"), BotInfo.class);
            result.core = core;
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, List<String>> parseHashMapStringStringList(BufferedReader reader, String fromName,
            boolean comments) throws IOException, IllegalArgumentException {
        HashMap<String, List<String>> result = comments ? new LinkedHashMap<>() : new HashMap<>();
        String line = reader.readLine();
        int linePos = 0;
        String key = null;
        while (line != null) {
            if (line.startsWith("#") || AmadeusUtils.stringIsEmpty(line)) {
                if (comments)
                    result.put("#" + linePos, new ArrayList<>(Arrays.asList(line)));
            } else if (AmadeusUtils.stringIsNotEmpty(line)) {
                if (!line.startsWith("-- ")) {
                    key = line.trim();
                } else if (key != null) {
                    if (line.length() > 3) {
                        if (!result.containsKey(key))
                            result.put(key, new ArrayList<>());
                        result.get(key).add(line.substring(3));
                    } else
                        throw new IllegalArgumentException(
                                String.format(fromName + ": error on line %s. Line can not be null", linePos, line));
                } else
                    throw new IllegalArgumentException(
                            String.format(fromName + ": error on line %s. Key can not be null", linePos, line));
            }
            line = reader.readLine();
        }
        reader.close();
        return result;
    }

    public static Map<String, String> parseHashMapStringString(BufferedReader reader, String fromName, boolean comments)
            throws IOException, IllegalArgumentException {
        HashMap<String, String> result = comments ? new LinkedHashMap<>() : new HashMap<>();
        String line = reader.readLine();
        int index = -1;
        int linePos = 0;
        while (line != null) {
            if (line.startsWith("#") || AmadeusUtils.stringIsEmpty(line)) {
                if (comments)
                    result.put("#" + linePos, line);
            } else {
                index = line.indexOf("=");
                if (index > 0)
                    result.put(line.substring(0, index), line.substring(index + 1, line.length()));
                else
                    throw new IllegalArgumentException(
                            String.format(fromName + ": error on line %s:%s", linePos, line));
            }
            index = -1;
            linePos++;
            line = reader.readLine();
        }
        reader.close();
        return result;
    }

    public static String formatChatLog(AmadeusCore core, MessageReceivedEvent event) {
        String result = core.optionChatLogFormat;
        result = result.replace("{messageId}", String.valueOf(event.getMessageID())).replace("{messageIdLn}",
                String.valueOf(event.getMessageID() + "\n"));
        if (!core.isPrivateMessage(event.getMessage())) {
            result = result.replace("{guild}", event.getGuild().getName() + " (" + event.getGuild().getLongID() + ")")
                    .replace("{guildLn}", event.getGuild().getName() + " (" + event.getGuild().getLongID() + ")\n");
            result = result
                    .replace("{channel}", event.getChannel().getName() + " (" + event.getChannel().getLongID() + ")")
                    .replace("{channelLn}",
                            event.getChannel().getName() + " (" + event.getChannel().getLongID() + ")\n");
        } else {
            result = result.replace("{guild}", "").replace("{guildLn}", "");
            result = result.replace("{channel}", "Private message").replace("{channelLn}", "Private message\n");
        }
        result = result
                .replace("{user}",
                        (core.isPrivateMessage(event.getMessage()) ? event.getAuthor().getName()
                                : event.getAuthor().getDisplayName(event.getGuild())) + " ("
                                + event.getAuthor().getLongID() + ")")
                .replace("{userLn}",
                        (core.isPrivateMessage(event.getMessage()) ? event.getAuthor().getName()
                                : event.getAuthor().getDisplayName(event.getGuild())) + " ("
                                + event.getAuthor().getLongID() + ")\n");
        result = result.replace("{text}", event.getMessage().getContent()).replace("{textLn}",
                event.getMessage().getContent() + "\n");
        StringBuilder attachments = new StringBuilder();
        for (int i = 0; i < event.getMessage().getAttachments().size(); i++) {
            Attachment attachment = event.getMessage().getAttachments().get(i);
            attachments.append(attachment.getFilename() + ":" + attachment.getFilesize() + " (" + attachment.getLongID()
                    + ") = " + attachment.getUrl() + (i >= event.getMessage().getAttachments().size() - 1 ? "" : "\n"));
        }
        result = result.replace("{attachments}", attachments.toString()).replace("{attachmentsLn}",
                attachments.toString() + "\n");
        return result;
    }

    public static boolean existsOrCreateFolder(File folder) {
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    public static boolean existsOrCreateFile(File file) {
        try {
            return (file.exists() && file.isFile()) || file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean existsOrCreateFolder(File folder, Action onCreate, Action onExists) {
        if (folder.exists() && folder.isDirectory()) {
            if (onExists != null)
                return onExists.done();
            return true;
        } else {
            if (folder.mkdirs()) {
                if (onCreate != null)
                    return onCreate.done();
                return true;
            }
        }
        return false;
    }

    public static boolean existsOrCreateFile(File file, Action onCreate, Action onExists) {
        if (file.exists() && file.isFile()) {
            if (onExists != null)
                return onExists.done();
            return true;
        } else {
            try {
                if (file.createNewFile()) {
                    if (onCreate != null)
                        return onCreate.done();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static FilenameFilter getFilenameExtFilter(String ext) {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return FilenameUtils.isExtension(name, ext);
            }
        };
    }

    public static boolean stringIsNotEmpty(String input) {
        return input != null && input.length() > 0;
    }

    public static boolean stringIsEmpty(String input) {
        return !stringIsNotEmpty(input);
    }

    /**
     * Always return string.
     *
     * @param input - String object.
     * @return Empty string if null.
     */
    public static String string(String input) {
        return stringIsEmpty(input) ? "" : input;
    }

    public static float[] toFloatArray(double[] input) {
        if (input != null) {
            float[] result = new float[input.length];
            for (int id = 0; id < input.length; id++)
                result[id] = (float) input[id];
            return result;
        }
        return new float[] {};
    }

    public static double[] toDoubleArray(float[] input) {
        if (input != null) {
            double[] result = new double[input.length];
            for (int id = 0; id < input.length; id++)
                result[id] = input[id];
            return result;
        }
        return new double[] {};
    }

    public static HashMap<String, String> parseArgsToMap(String args) throws Exception {
        HashMap<String, String> result = new HashMap<String, String>();
        String argsString = args;
        int index = -1;
        while (argsString.length() > 0) {
            index = argsString.indexOf("=");
            String key = "", value = "";
            if (index != -1) {
                key = argsString.substring(0, index);
                value = "";
                argsString = argsString.substring(index + 1);
                index = -1;
                if (argsString.startsWith("\"")) {
                    argsString = argsString.substring(1);
                    index = argsString.indexOf("\"");
                    if (index != -1) {
                        value = argsString.substring(0, index);
                        argsString = argsString.substring(index + 1);
                    }
                } else {
                    index = argsString.indexOf(" ");
                    if (index != -1) {
                        value = argsString.substring(0, index);
                        argsString = argsString.substring(index);
                    } else {
                        value = argsString;
                        argsString = "";
                    }
                }
                argsString = argsString.trim();
                index = -1;
                result.put(key, value);
            } else
                throw new Exception("Error on args parsing >" + argsString);
        }
        return result;
    }

    public static ArrayList<String> parseArgsToArray(String args) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        String argsString = args;
        int index = 0;
        while (argsString.length() > 0) {
            if (index != -1) {
                if (argsString.startsWith("\"")) {
                    argsString = argsString.substring(1);
                    index = argsString.indexOf("\"");
                    if (index != -1) {
                        result.add(argsString.substring(0, index));
                        argsString = argsString.substring(index + 1);
                    }
                } else if (argsString.startsWith(" "))
                    argsString = argsString.substring(1);
                else {
                    index = argsString.indexOf(" ");
                    if (index != -1) {
                        result.add(argsString.substring(0, index));
                        argsString = argsString.substring(index + 1);
                    } else {
                        result.add(argsString);
                        argsString = "";
                    }
                }
            } else
                throw new Exception("Error on args parsing >" + argsString);
        }
        return result;
    }

    public static String removePreifx(String text, AmadeusCore core, Iterable<String> prefixes, boolean checkSpace) {
        if (text.startsWith("<@" + core.client.getOurUser().getLongID() + ">")
                || text.startsWith("<@!" + core.client.getOurUser().getLongID() + ">"))
            return text.substring(text.indexOf(">") + 1).trim();
        for (String prefix : prefixes) {
            if (text.toLowerCase().startsWith(prefix)) {
                if (!checkSpace || text.substring(prefix.length()).startsWith(" "))
                    return text.substring(prefix.length()).trim();
            }
        }
        return text;
    }

    public static void waitAction(long checkTime, Action action) {
        long time = System.currentTimeMillis() + checkTime;
        while (!action.done() && System.currentTimeMillis() < time) {}
    }

    public static void waitAction(Action action) {
        while (!action.done()) {}
    }

    public static interface Action {
        public boolean done();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
