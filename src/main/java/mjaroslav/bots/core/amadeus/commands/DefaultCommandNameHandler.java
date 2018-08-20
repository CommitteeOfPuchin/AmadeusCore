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
        return reader.json.getOrDefault("prefix", Arrays.asList("<bot>"));
    }

    private static final Comparator<String> sort = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o2.length() - o1.length();
        }
    };

    @Override
    public List<String> getNames(String key) {
        ArrayList<String> result = new ArrayList<String>(reader.json.getOrDefault(key, Arrays.asList(key)));
        result.add(key);
        result.sort(sort);
        return result;
    }

    @Override
    public void loadNames() {
        reader.read();
    }
}
