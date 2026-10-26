package gg.moonflower.etched.client.radio.history;

import gg.moonflower.etched.core.Etched;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Remembers the last radio stations the player used in each world or on each server. The history only exists on the client and the worlds and
 * servers are stored as hashes, so the file does not say where the stations were used.
 * <p>
 * The idea of a per world and per server history comes from Re-Etched by oogabooga-dev, https://github.com/oogabooga-dev/re-etched
 */
public final class RadioHistory {

    public static final int MAX_STATIONS = RadioHistoryStore.MAX_STATIONS;
    private static final long PENDING_MILLIS = 15000L;
    private static final Object LOCK = new Object();
    private static RadioHistoryStore store;
    private static String pendingUrl;
    private static long pendingTime;

    private RadioHistory() {
    }

    private static RadioHistoryStore store() {
        synchronized (LOCK) {
            if (store == null) {
                Path file = Minecraft.getInstance().gameDirectory.toPath().resolve(Etched.MOD_ID).resolve("radio-history.json");
                store = new RadioHistoryStore(file, Util.ioPool(), Etched.LOGGER);
            }
            return store;
        }
    }

    /**
     * @return The key of the world or server the player is in, or nothing if there is none
     */
    public static Optional<String> currentContext() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.hasSingleplayerServer() && minecraft.getSingleplayerServer() != null) {
            Path world = minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize();
            Path name = world.getFileName();
            return Optional.of("sp:" + hash("singleplayer\0" + (name != null ? name : world)));
        }

        ServerData server = minecraft.getCurrentServer();
        if (server == null || server.ip == null || server.ip.isBlank()) {
            return Optional.empty();
        }
        ServerAddress address = ServerAddress.parseString(server.ip.trim());
        if (address.getHost() == null || address.getHost().isBlank()) {
            return Optional.empty();
        }
        return Optional.of("mp:" + hash("multiplayer\0" + address.getHost().toLowerCase(Locale.ROOT) + "\0" + address.getPort()));
    }

    /**
     * Notes that the player asked a radio to use a station. The station is only added to the history once the server confirms it with
     * {@link #confirm(String)}.
     */
    public static void submitted(String url) {
        String station = url.strip();
        synchronized (LOCK) {
            pendingUrl = RadioHistoryStore.isValidUrl(station) ? station : null;
            pendingTime = System.currentTimeMillis();
        }
    }

    /**
     * Adds the station to the history if the player just asked a radio to use it. Called when a radio of the world sends its URL.
     */
    public static void confirm(@Nullable String url) {
        synchronized (LOCK) {
            if (pendingUrl == null || !pendingUrl.equals(url) || System.currentTimeMillis() - pendingTime > PENDING_MILLIS) {
                return;
            }
            pendingUrl = null;
        }
        currentContext().ifPresent(context -> store().record(context, url));
    }

    /**
     * @return The stations used in the current world or server, with the latest first
     */
    public static List<String> entries() {
        return currentContext().map(context -> store().entries(context)).orElse(List.of());
    }

    /**
     * Forgets the stations of the current world or server.
     */
    public static void clear() {
        currentContext().ifPresent(context -> store().clear(context));
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
