package mjaroslav.bots.core.amadeus.commands;

import java.util.*;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public class DefaultCommandNameHandler extends CommandNameHandler {
    public JSONReader<HashMap<String, List<String>>> reader = new JSONReader<>(new HashMap<String, List<String>>(),
            core.folder.toPath().resolve("commandnames.json").toFile(), true);

    public DefaultCommandNameHandler(AmadeusCore core) {
        super(core);
        reader.init();
    }

    @Override
    public List<String> getPrefixes() {
        ArrayList<String> result = new ArrayList<String>();
        if (reader.json.containsKey("prefix"))
            result.addAll(reader.json.get("prefix"));
        else {
            result.add("execute");
            reader.json.put("prefix", result);
            reader.write();
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
        if (reader.json.containsKey(key))
            result.addAll(reader.json.get(key));
        else {
            reader.json.put(key, Collections.emptyList());
            reader.write();
        }
        result.sort(sort);
        return result;
    }

    @Override
    public void loadNames() {
        reader.read();
    }
}
