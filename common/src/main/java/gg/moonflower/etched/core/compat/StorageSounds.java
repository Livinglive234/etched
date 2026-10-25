package gg.moonflower.etched.core.compat;

import net.minecraft.client.resources.sounds.SoundInstance;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Plays sounds on behalf of storage mods. Only available when a mod loader supplies a handler.
 */
@ApiStatus.Internal
public final class StorageSounds {

    @Nullable
    private static BiConsumer<UUID, SoundInstance> handler;

    private StorageSounds() {
    }

    public static void setHandler(@Nullable BiConsumer<UUID, SoundInstance> handler) {
        StorageSounds.handler = handler;
    }

    /**
     * Plays the specified sound for a storage.
     *
     * @return Whether a handler took over playing the sound
     */
    public static boolean play(UUID storageId, SoundInstance sound) {
        if (handler == null) {
            return false;
        }
        handler.accept(storageId, sound);
        return true;
    }
}
