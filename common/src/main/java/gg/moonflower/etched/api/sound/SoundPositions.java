package gg.moonflower.etched.api.sound;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Lets a mod that moves blocks somewhere else than their stored position, such as the ships of Valkyrien Skies, say where a block
 * really is in the world, so the sounds of radios and jukeboxes on it are heard from there.
 */
public final class SoundPositions {

    private static volatile Transformer transformer;

    private SoundPositions() {
    }

    public static void setTransformer(@Nullable Transformer transformer) {
        SoundPositions.transformer = transformer;
    }

    /**
     * @return The position in the world of the block at the stored position, or <code>null</code> if it is not moved
     */
    public static @Nullable Vec3 toWorld(double x, double y, double z) {
        Transformer current = transformer;
        return current != null ? current.toWorld(x, y, z) : null;
    }

    @FunctionalInterface
    public interface Transformer {

        @Nullable Vec3 toWorld(double x, double y, double z);
    }
}
