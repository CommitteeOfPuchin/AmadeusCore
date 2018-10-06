package mjaroslav.bots.core.amadeus.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import mjaroslav.bots.core.amadeus.AmadeusCore;

public abstract class PropertiesConfigurationHandler extends ConfigurationHandler {
    private boolean clearOnRead = false;

    private LinkedHashMap<String, ConfigProperty> map = new LinkedHashMap<String, ConfigProperty>();

    public PropertiesConfigurationHandler(AmadeusCore core, String name, boolean clearOnRead) {
        super(core, name);
        this.clearOnRead = clearOnRead;
        File file = getFolder();
        if ((file.exists() && file.isDirectory()) || file.mkdirs()) {
            file = getFile();
            if (!(file.exists() && file.isFile()))
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void readConfig() throws Exception {
        if (clearOnRead)
            map.clear();
        BufferedReader reader = Files.newBufferedReader(getFile().toPath(), StandardCharsets.UTF_8);
        String line = reader.readLine();
        int index = -1;
        int linePos = 0;
        while (line != null) {
            if (!line.startsWith("#")) {
                index = line.indexOf("=");
                if (index > 0)
                    map.put(line.substring(0, index), new ConfigProperty(line.substring(index + 1, line.length())));
                else
                    throw new IllegalArgumentException(String.format("Error on line %s: %s", linePos, line));
            } else
                map.put("#" + linePos, new ConfigProperty(line));
            index = -1;
            linePos++;
            line = reader.readLine();
        }
        reader.close();
    }

    @Override
    public abstract void afterLoad() throws Exception;

    @Override
    public String getExt() {
        return "properties";
    }

    @Override
    public void writeConfig() throws Exception {
        File file = getFile();
        file.delete();
        file.createNewFile();
        BufferedWriter writer = Files.newBufferedWriter(getFile().toPath(), StandardCharsets.UTF_8);
        for (Entry<String, ConfigProperty> entry : map.entrySet()) {
            if (entry.getKey().startsWith("#"))
                writer.write(entry.getValue().value);
            else
                writer.write(entry.getKey() + "=" + entry.getValue().value);
            writer.newLine();
        }
        writer.close();
    }

    public String getString(String key, String defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getString();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getInt();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getLong();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public float getString(String key, float defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getFloat();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getDouble();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getBoolean();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public String[] getStringList(String key, String[] defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getStringList();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public int[] getIntList(String key, int[] defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getIntList();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public long[] getLongList(String key, long[] defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getLongList();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public float[] getStringList(String key, float[] defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getFloatList();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public double[] getDoubleList(String key, double[] defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getDoubleList();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public boolean[] getBooleanList(String key, boolean[] defaultValue) {
        if (map.containsKey(key))
            return map.get(key).getBooleanList();
        else {
            map.put(key, new ConfigProperty(defaultValue));
            try {
                writeConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static class ConfigProperty {
        public String value;

        public ConfigProperty() {}

        public ConfigProperty(String value) {
            this.value = value;
        }

        public ConfigProperty(int value) {
            setInt(value);
        }

        public ConfigProperty(long value) {
            setLong(value);
        }

        public ConfigProperty(float value) {
            setFloat(value);
        }

        public ConfigProperty(double value) {
            setDouble(value);
        }

        public ConfigProperty(boolean value) {
            setBoolean(value);
        }

        public ConfigProperty(String[] value) {
            setStringList(value);
        }

        public ConfigProperty(int[] value) {
            setIntList(value);
        }

        public ConfigProperty(long[] value) {
            setLongList(value);
        }

        public ConfigProperty(float[] value) {
            setFloatList(value);
        }

        public ConfigProperty(double[] value) {
            setDoubleList(value);
        }

        public ConfigProperty(boolean[] value) {
            setBooleanList(value);
        }

        public String getString() {
            return value;
        }

        public int getInt() {
            return Integer.valueOf(value);
        }

        public long getLong() {
            return Long.valueOf(value);
        }

        public float getFloat() {
            return Float.valueOf(value);
        }

        public double getDouble() {
            return Double.valueOf(value);
        }

        public boolean getBoolean() {
            return Boolean.valueOf(value);
        }

        public String[] getStringList() {
            return value.split("|");
        }

        public int[] getIntList() {
            String[] temp = value.split(",");
            int[] result = new int[temp.length];
            for (int id = 0; id < temp.length; id++)
                result[id] = Integer.valueOf(temp[id].trim());
            return result;
        }

        public long[] getLongList() {
            String[] temp = value.split(",");
            long[] result = new long[temp.length];
            for (int id = 0; id < temp.length; id++)
                result[id] = Long.valueOf(temp[id].trim());
            return result;
        }

        public float[] getFloatList() {
            String[] temp = value.split(",");
            float[] result = new float[temp.length];
            for (int id = 0; id < temp.length; id++)
                result[id] = Float.valueOf(temp[id].trim());
            return result;
        }

        public double[] getDoubleList() {
            String[] temp = value.split(",");
            double[] result = new double[temp.length];
            for (int id = 0; id < temp.length; id++)
                result[id] = Double.valueOf(temp[id].trim());
            return result;
        }

        public boolean[] getBooleanList() {
            String[] temp = value.split(",");
            boolean[] result = new boolean[temp.length];
            for (int id = 0; id < temp.length; id++)
                result[id] = Boolean.valueOf(temp[id].trim());
            return result;
        }

        public void setString(String value) {
            this.value = value;
        }

        public void setInt(int value) {
            this.value = String.valueOf(value);
        }

        public void setLong(long value) {
            this.value = String.valueOf(value);
        }

        public void setFloat(float value) {
            this.value = String.valueOf(value);
        }

        public void setDouble(double value) {
            this.value = String.valueOf(value);
        }

        public void setBoolean(boolean value) {
            this.value = String.valueOf(value);
        }

        public void setStringList(String[] value) {
            this.value = String.join("|", value);
        }

        public void setIntList(int[] value) {
            String[] temp = new String[value.length];
            for (int id = 0; id < value.length; id++)
                temp[id] = String.valueOf(value[id]);
            this.value = String.join(", ", temp);
        }

        public void setLongList(long[] value) {
            String[] temp = new String[value.length];
            for (int id = 0; id < value.length; id++)
                temp[id] = String.valueOf(value[id]);
            this.value = String.join(", ", temp);
        }

        public void setFloatList(float[] value) {
            String[] temp = new String[value.length];
            for (int id = 0; id < value.length; id++)
                temp[id] = String.valueOf(value[id]);
            this.value = String.join(", ", temp);
        }

        public void setDoubleList(double[] value) {
            String[] temp = new String[value.length];
            for (int id = 0; id < value.length; id++)
                temp[id] = String.valueOf(value[id]);
            this.value = String.join(", ", temp);
        }

        public void setBooleanList(boolean[] value) {
            String[] temp = new String[value.length];
            for (int id = 0; id < value.length; id++)
                temp[id] = String.valueOf(value[id]);
            this.value = String.join(", ", temp);
        }
    }
}
