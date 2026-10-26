package gg.moonflower.etched.client.radio.history;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.moonflower.etched.core.Etched;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Names the player gave to radio stations, kept only on the client and used to show the history list.
 */
public final class StationNames {

    public static final int MAX_NAME_LENGTH = 40;
    private static final int MAX_NAMES = 200;
    private static final Gson GSON = new Gson();
    private static Map<String, String> names;

    private StationNames() {
    }

    private static Path file() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve(Etched.MOD_ID).resolve("radio-names.json");
    }

    private static synchronized Map<String, String> names() {
        if (names == null) {
            names = new LinkedHashMap<>();
            Path file = file();
            if (Files.exists(file)) {
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    json.entrySet().forEach(entry -> {
                        if (entry.getValue().isJsonPrimitive() && names.size() < MAX_NAMES) {
                            names.put(entry.getKey(), clean(entry.getValue().getAsString()));
                        }
                    });
                } catch (Exception e) {
                    Etched.LOGGER.warn("Could not read the station names", e);
                }
            }
        }
        return names;
    }

    private static String clean(String name) {
        String stripped = name.strip().replaceAll("\\p{Cntrl}", " ");
        return stripped.length() > MAX_NAME_LENGTH ? stripped.substring(0, MAX_NAME_LENGTH) : stripped;
    }

    /**
     * @param url A station address
     * @return The name the player gave the station, or <code>null</code> if it has none
     */
    public static synchronized @Nullable String get(String url) {
        String name = names().get(url.strip());
        return name == null || name.isEmpty() ? null : name;
    }

    /**
     * @param url A station address
     * @return The name of the station, or the address if it has no name
     */
    public static String label(String url) {
        String name = get(url);
        return name != null ? name : url;
    }

    /**
     * Names a station. A blank name removes the name.
     */
    public static synchronized void set(String url, String name) {
        Map<String, String> names = names();
        String cleaned = clean(name);
        String key = url.strip();
        if (cleaned.isEmpty()) {
            if (names.remove(key) == null) {
                return;
            }
        } else {
            if (cleaned.equals(names.get(key))) {
                return;
            }
            names.remove(key);
            names.put(key, cleaned);
            while (names.size() > MAX_NAMES) {
                names.remove(names.keySet().iterator().next());
            }
        }

        String json = GSON.toJson(names);
        Path file = file();
        Util.ioPool().execute(() -> {
            try {
                Files.createDirectories(file.getParent());
                try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    writer.write(json);
                }
            } catch (IOException e) {
                Etched.LOGGER.warn("Could not save the station names", e);
            }
        });
    }
}
