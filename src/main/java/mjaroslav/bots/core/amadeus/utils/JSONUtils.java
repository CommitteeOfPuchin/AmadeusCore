package mjaroslav.bots.core.amadeus.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mjaroslav.bots.core.amadeus.lib.References;

public class JSONUtils {
    public static final FilenameFilter JSONEXTFILTER = AmadeusUtils.getFilenameExtFilter(References.EXT_JSON);
    public static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
    public static final Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object oject, boolean pretty) {
        return pretty ? gsonPretty.toJson(oject) : gson.toJson(oject);
    }

    public static <T> T fromJson(File file, Class<T> clazz) throws IOException {
        Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
        T result = gson.fromJson(reader, clazz);
        reader.close();
        return result;
    }

    public static void toJson(File file, Object object, boolean pretty) throws IOException {
        Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
        if (pretty)
            gsonPretty.toJson(object, writer);
        else
            gson.toJson(object, writer);
        writer.close();
    }

    public static <T> T fromJson(InputStream stream, Class<T> clazz) throws IOException {
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        T result = gson.fromJson(reader, clazz);
        reader.close();
        return result;
    }

    public static void toJson(OutputStream stream, Object object, boolean pretty) throws IOException {
        Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        if (pretty)
            gsonPretty.toJson(object, writer);
        else
            gson.toJson(object, writer);
        writer.close();
    }
}
