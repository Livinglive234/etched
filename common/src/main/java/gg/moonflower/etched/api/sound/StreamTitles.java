package gg.moonflower.etched.api.sound;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the title that live radio streams last reported, by station address.
 */
public final class StreamTitles {

    private static final Map<String, String> TITLES = new ConcurrentHashMap<>();
    private static final Map<String, String> ALIASES = new ConcurrentHashMap<>();

    private StreamTitles() {
    }

    /**
     * Makes titles reported for one address also show for another, such as the station inside a playlist.
     */
    public static void alias(String playlist, String station) {
        ALIASES.put(station, playlist);
    }

    public static void set(String url, String title) {
        TITLES.put(url, title);
        String alias = ALIASES.get(url);
        if (alias != null) {
            TITLES.put(alias, title);
        }
    }

    public static @Nullable String get(String url) {
        return TITLES.get(url);
    }

    public static void clear(String url) {
        TITLES.remove(url);
    }
}
