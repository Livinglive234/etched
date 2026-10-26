package gg.moonflower.etched.client.radio.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.moonflower.etched.api.util.BoundedStreams;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Keeps the recent radio stations of each world or server, and saves them to a file. Saving replaces the file in one step and keeps a backup,
 * and what is read from the file is checked.
 * <p>
 * The idea of a per world and per server history comes from Re-Etched by oogabooga-dev, https://github.com/oogabooga-dev/re-etched
 */
final class RadioHistoryStore {

    static final int MAX_STATIONS = 20;
    static final int MAX_URL_LENGTH = 2048;
    static final int MAX_CONTEXTS = 64;
    private static final int SCHEMA_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file;
    private final Executor executor;
    private final Logger logger;
    private final Map<String, Context> contexts = new LinkedHashMap<>();
    private final Object writeLock = new Object();

    RadioHistoryStore(Path file, Executor executor, Logger logger) {
        this.file = file;
        this.executor = executor;
        this.logger = logger;
        if (!this.read(file) && !this.read(this.backup())) {
            this.contexts.clear();
        }
    }

    static boolean isValidUrl(@Nullable String url) {
        if (url == null || url.isBlank() || url.length() > MAX_URL_LENGTH || !url.equals(url.strip())) {
            return false;
        }
        try {
            String scheme = new URI(url).getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (Exception e) {
            return false;
        }
    }

    private Path backup() {
        return this.file.resolveSibling(this.file.getFileName() + ".bak");
    }

    synchronized boolean record(String contextKey, String url) {
        if (!contextKey.matches("(?:mp|sp):[0-9a-f]{64}") || !isValidUrl(url)) {
            return false;
        }

        Context context = this.contexts.computeIfAbsent(contextKey, key -> new Context());
        if (!context.stations.isEmpty() && context.stations.getFirst().equals(url)) {
            return false;
        }

        context.stations.remove(url);
        context.stations.addFirst(url);
        while (context.stations.size() > MAX_STATIONS) {
            context.stations.removeLast();
        }
        context.modified = System.currentTimeMillis();
        while (this.contexts.size() > MAX_CONTEXTS) {
            this.contexts.entrySet().stream().min(Comparator.comparingLong(entry -> entry.getValue().modified)).map(Map.Entry::getKey).ifPresent(this.contexts::remove);
        }
        this.save();
        return true;
    }

    synchronized List<String> entries(String contextKey) {
        Context context = this.contexts.get(contextKey);
        return context == null ? List.of() : List.copyOf(context.stations);
    }

    synchronized boolean clear(String contextKey) {
        if (this.contexts.remove(contextKey) != null) {
            this.save();
            return true;
        }
        return false;
    }

    private boolean read(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }

        try (InputStream stream = BoundedStreams.limit(Files.newInputStream(path), BoundedStreams.MAX_PLAYLIST_BYTES)) {
            JsonObject json = JsonParser.parseString(new String(stream.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (json.get("version").getAsInt() > SCHEMA_VERSION) {
                return false;
            }

            Map<String, Context> read = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("contexts").entrySet()) {
                if (!entry.getKey().matches("(?:mp|sp):[0-9a-f]{64}") || read.size() >= MAX_CONTEXTS) {
                    continue;
                }

                JsonObject object = entry.getValue().getAsJsonObject();
                Context context = new Context();
                context.modified = Math.max(0L, object.get("modified").getAsLong());
                for (JsonElement station : object.getAsJsonArray("stations")) {
                    String url = station.getAsString();
                    if (isValidUrl(url) && !context.stations.contains(url) && context.stations.size() < MAX_STATIONS) {
                        context.stations.add(url);
                    }
                }
                if (!context.stations.isEmpty()) {
                    read.put(entry.getKey(), context);
                }
            }

            this.contexts.clear();
            this.contexts.putAll(read);
            return true;
        } catch (Exception e) {
            this.logger.warn("Ignoring invalid radio history {}", path, e);
            return false;
        }
    }

    private void save() {
        JsonObject json = new JsonObject();
        json.addProperty("version", SCHEMA_VERSION);
        JsonObject contexts = new JsonObject();
        this.contexts.forEach((key, context) -> {
            JsonObject object = new JsonObject();
            object.addProperty("modified", context.modified);
            JsonArray stations = new JsonArray();
            context.stations.forEach(stations::add);
            object.add("stations", stations);
            contexts.add(key, object);
        });
        json.add("contexts", contexts);
        String text = GSON.toJson(json);

        this.executor.execute(() -> {
            synchronized (this.writeLock) {
                try {
                    Files.createDirectories(this.file.getParent());
                    Path temporary = this.file.resolveSibling(this.file.getFileName() + ".tmp");
                    Files.writeString(temporary, text, StandardCharsets.UTF_8);
                    if (Files.isRegularFile(this.file)) {
                        Files.copy(this.file, this.backup(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    try {
                        Files.move(temporary, this.file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    } catch (AtomicMoveNotSupportedException e) {
                        Files.move(temporary, this.file, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    this.logger.warn("Failed to save the radio history", e);
                }
            }
        });
    }

    private static final class Context {

        private final LinkedList<String> stations = new LinkedList<>();
        private long modified;
    }
}
