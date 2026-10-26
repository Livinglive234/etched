package gg.moonflower.etched.client.compat;

import dev.ryanhcode.sable.companion.SableCompanion;
import gg.moonflower.etched.api.sound.SoundPositions;

/**
 * Makes the distance checks of radios, jukeboxes and boomboxes count blocks on Sable sub-levels, such as the ships of Create: Aeronautics, as being
 * where they are seen. Sable moves the sound itself, so the sounds keep the position their block is stored at.
 * <p>The distance checks that count sub-levels follow BazZziliuS' NeoForge version, https://github.com/BazZziliuS/etched
 * <p>Only load this class when Sable is installed.
 */
public final class SableCompat {

    private SableCompat() {
    }

    public static void load() {
        SoundPositions.setDistanceFunction((level, x1, y1, z1, x2, y2, z2) -> SableCompanion.INSTANCE.distanceSquaredWithSubLevels(level, x1, y1, z1, x2, y2, z2));
    }
}
