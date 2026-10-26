package gg.moonflower.etched.api.sound;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Lets a mod that moves blocks somewhere else than their stored position, such as the ships of Valkyrien Skies, say where a block
 * really is in the world, so the sounds of radios and jukeboxes on it are heard from there.
 */
public final class SoundPositions {

    private static volatile Transformer transformer;
    private static volatile DistanceFunction distances;

    private SoundPositions() {
    }

    public static void setTransformer(@Nullable Transformer transformer) {
        SoundPositions.transformer = transformer;
    }

    /**
     * Sets how far apart two points are for mods that keep a block's sounds at its stored position and move the sound themselves,
     * such as the sub-levels of Sable. Distances measured with the stored position would otherwise be wrong.
     */
    public static void setDistanceFunction(@Nullable DistanceFunction distances) {
        SoundPositions.distances = distances;
    }

    /**
     * @return The squared distance between two points, which counts blocks on sub-levels as being where they are seen
     */
    public static double distanceSquared(Level level, double x1, double y1, double z1, double x2, double y2, double z2) {
        DistanceFunction current = distances;
        if (current != null) {
            return current.distanceSquared(level, x1, y1, z1, x2, y2, z2);
        }
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * @return The position in the world of the block at the stored position, or <code>null</code> if it is not moved
     */
    public static @Nullable Vec3 toWorld(double x, double y, double z) {
        Transformer current = transformer;
        return current != null ? current.toWorld(x, y, z) : null;
    }

    @FunctionalInterface
    public interface DistanceFunction {

        double distanceSquared(Level level, double x1, double y1, double z1, double x2, double y2, double z2);
    }

    @FunctionalInterface
    public interface Transformer {

        @Nullable Vec3 toWorld(double x, double y, double z);
    }
}
