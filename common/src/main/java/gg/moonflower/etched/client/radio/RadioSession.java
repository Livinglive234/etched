package gg.moonflower.etched.client.radio;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Remembers, for each radio this client can hear, whether the player stopped it and how many times a lost connection was retried.
 * <p>Stopping only silences the radio for this player. It stays silent until the player presses play or the station changes.
 * The play, stop and reconnect behaviour is modelled on re-etched.
 */
public final class RadioSession {

    /**
     * The number of times a failed connection is retried before giving up
     */
    public static final int MAX_RECONNECTS = 3;

    private static final Map<BlockPos, String> STOPPED = new HashMap<>();
    private static final Map<BlockPos, Integer> ATTEMPTS = new HashMap<>();

    private RadioSession() {
    }

    public static void stop(BlockPos pos, @Nullable String url) {
        if (url != null) {
            STOPPED.put(pos.immutable(), url);
        }
        ATTEMPTS.remove(pos);
    }

    public static void resume(BlockPos pos) {
        STOPPED.remove(pos);
        ATTEMPTS.remove(pos);
    }

    /**
     * @return Whether the player stopped this station on this radio
     */
    public static boolean isStopped(BlockPos pos, @Nullable String url) {
        return Objects.equals(STOPPED.get(pos), url);
    }

    public static void resetAttempts(BlockPos pos) {
        ATTEMPTS.remove(pos);
    }

    /**
     * Counts a reconnect.
     *
     * @return The number of this attempt, starting at 1
     */
    public static int nextAttempt(BlockPos pos) {
        return ATTEMPTS.merge(pos.immutable(), 1, Integer::sum);
    }

    /**
     * @return The reconnect in progress, or 0 if the radio is not reconnecting
     */
    public static int reconnectAttempt(BlockPos pos) {
        return ATTEMPTS.getOrDefault(pos, 0);
    }
}
