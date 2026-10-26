package gg.moonflower.etched.api.util;

import gg.moonflower.etched.api.sound.download.SoundDownloadSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Reads radio station playlists in the PLS and M3U formats and picks the first station that answers.
 * <p>Nested playlists, relative addresses and ordered fallbacks follow the approach of re-etched.
 */
public final class StationPlaylist {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_DEPTH = 3;
    private static final int MAX_CANDIDATES = 16;

    private StationPlaylist() {
    }

    /**
     * @param url An address
     * @return Whether the address points at a station playlist rather than audio
     */
    public static boolean isPlaylist(String url) {
        String path = path(url);
        return path.endsWith(".pls") || path.endsWith(".m3u");
    }

    private static String path(String url) {
        try {
            String path = new URI(url).getPath();
            return path == null ? "" : path.toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Parses the entries of a playlist in order.
     *
     * @param base    The address the playlist was loaded from, used for relative entries
     * @param content The text of the playlist
     * @return The absolute entry addresses
     */
    public static List<String> parse(String base, String content) {
        List<String> raw = new ArrayList<>();
        String[] lines = content.split("\\R");
        if (!lines[0].isEmpty() && lines[0].strip().equalsIgnoreCase("[playlist]") || content.toLowerCase(Locale.ROOT).contains("[playlist]")) {
            Map<Integer, String> files = new TreeMap<>();
            for (String line : lines) {
                String trimmed = line.strip();
                if (trimmed.length() > 4 && trimmed.regionMatches(true, 0, "File", 0, 4)) {
                    int equals = trimmed.indexOf('=');
                    if (equals > 4) {
                        try {
                            files.put(Integer.parseInt(trimmed.substring(4, equals).strip()), trimmed.substring(equals + 1).strip());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            raw.addAll(files.values());
        } else {
            for (String line : lines) {
                String trimmed = line.strip();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    raw.add(trimmed);
                }
            }
        }

        List<String> entries = new ArrayList<>();
        URI baseUri = URI.create(base);
        for (String entry : raw) {
            try {
                URI resolved = baseUri.resolve(entry.replace(" ", "%20"));
                String scheme = resolved.getScheme();
                if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                    entries.add(resolved.toString());
                }
            } catch (Exception e) {
                LOGGER.warn("Ignoring invalid playlist entry: {}", entry);
            }
        }
        return entries;
    }

    private static void collect(String url, int depth, List<String> out) throws IOException {
        if (out.size() >= MAX_CANDIDATES) {
            return;
        }
        if (!isPlaylist(url)) {
            out.add(url);
            return;
        }
        if (depth >= MAX_DEPTH) {
            return;
        }
        String content;
        try {
            HttpURLConnection connection = NetworkPolicy.connect(new URI(url).toURL(), null, "GET", SoundDownloadSource.getDownloadHeaders());
            if (connection.getResponseCode() != 200) {
                LOGGER.warn("Playlist {} answered {}", url, connection.getResponseCode());
                return;
            }
            content = BoundedStreams.readString(connection.getInputStream(), BoundedStreams.MAX_PLAYLIST_BYTES);
        } catch (IOException e) {
            LOGGER.warn("Could not read playlist {}", url, e);
            return;
        } catch (Exception e) {
            throw new IOException(e);
        }
        for (String entry : parse(url, content)) {
            collect(entry, depth + 1, out);
        }
    }

    /**
     * Expands a playlist address into the audio addresses it lists, in order.
     *
     * @param url The playlist address
     * @return The station addresses, possibly empty
     * @throws IOException If the address is invalid
     */
    public static List<String> expand(String url) throws IOException {
        List<String> out = new ArrayList<>();
        collect(url, 0, out);
        return out;
    }

    /**
     * Picks the first address that answers with a success code.
     *
     * @param candidates The addresses in order of preference
     * @return The first working address
     * @throws IOException If none of them answer
     */
    public static String firstReachable(List<String> candidates) throws IOException {
        IOException last = null;
        for (String candidate : candidates) {
            try {
                HttpURLConnection connection = NetworkPolicy.connect(new URI(candidate).toURL(), null, "GET", SoundDownloadSource.getDownloadHeaders());
                try {
                    if (connection.getResponseCode() == 200) {
                        return candidate;
                    }
                    last = new IOException(candidate + " answered " + connection.getResponseCode());
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                last = e instanceof IOException io ? io : new IOException(e);
            }
            LOGGER.warn("Station {} is not reachable, trying the next entry", candidate);
        }
        throw last != null ? last : new IOException("The playlist has no stations");
    }
}
