package mjaroslav.bots.core.amadeus.commands;

import java.util.*;
import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public class DefaultCommandNameHandler extends CommandNameHandler {
    public JSONReader<HashMap<String, List<String>>> readerNames = new JSONReader<HashMap<String, List<String>>>(
            new HashMap<String, List<String>>(), getFolder().toPath().resolve("names.json").toFile(), true);
    public JSONReader<HashMap<String, List<String>>> readerArgs = new JSONReader<HashMap<String, List<String>>>(
            new HashMap<String, List<String>>(), getFolder().toPath().resolve("args.json").toFile(), true);

    public DefaultCommandNameHandler(AmadeusCore core, CommandHandler handler) {
        super(core, handler);
        readerNames.init();
        readerArgs.init();
    }

    @Override
    public List<String> getPrefixes() {
        ArrayList<String> result = new ArrayList<String>();
        if (readerNames.json.containsKey("prefix")) {
            result.addAll(readerNames.json.get("prefix"));
        } else {
            result.add("execute");
            readerNames.json.put("prefix", result);
            readerNames.write();
        }
        return result;
    }

    private static final Comparator<String> sort = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o2.length() - o1.length();
        }
    };

    @Override
    public List<String> getNames(String key) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(key);
        if (readerNames.json.containsKey(key))
            result.addAll(readerNames.json.get(key));
        else {
            readerNames.json.put(key, Collections.emptyList());
            readerNames.write();
        }
        result.sort(sort);
        return result;
    }

    @Override
    public void loadNames() {
        readerNames.read();
        readerArgs.read();
    }

    @Override
    public List<String> getArgNames(String commandKey, String key) {
        ArrayList<String> result = new ArrayList<String>();
        result.add(key);
        if (readerArgs.json.containsKey(commandKey + "." + key))
            result.addAll(readerArgs.json.get(commandKey + "." + key));
        else {
            readerArgs.json.put(commandKey + "." + key, Collections.emptyList());
            readerArgs.write();
        }
        result.sort(sort);
        return result;
    }
}
