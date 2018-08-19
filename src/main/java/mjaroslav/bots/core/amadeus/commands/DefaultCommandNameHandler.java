package mjaroslav.bots.core.amadeus.commands;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import mjaroslav.bots.core.amadeus.AmadeusCore;
import mjaroslav.bots.core.amadeus.utils.JSONReader;

public class DefaultCommandNameHandler extends CommandNameHandler {
    public JSONReader<HashMap<String, List<String>>> reader = new JSONReader<>(new HashMap<String, List<String>>(),  new File(""), true);
    public DefaultCommandNameHandler(AmadeusCore core) {
        super(core);
    }

    @Override
    public List<String> getNames(String key) {
        return null;
    }
}
