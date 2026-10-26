package gg.moonflower.etched.client.radio;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * Remembers, for each radio this client can hear, how many times a lost connection was retried.
 * <p>The play, stop and reconnect behaviour is modelled on re-etched.
 */
public final class RadioSession {

    /**
     * The number of times a failed connection is retried before giving up
     */
    public static final int MAX_RECONNECTS = 3;

    private static final Map<BlockPos, Integer> ATTEMPTS = new HashMap<>();

    private RadioSession() {
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
