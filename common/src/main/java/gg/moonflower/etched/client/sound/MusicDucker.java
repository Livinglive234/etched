package gg.moonflower.etched.client.sound;

import gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stops the game's background music, and music other mods play in the music category, while a radio, jukebox or boombox of this mod can be heard, so the two do not play over each other.
 */
public final class MusicDucker {

    private static final Set<AbstractOnlineSoundInstance> SOUNDS = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();

    private MusicDucker() {
    }

    /**
     * Starts watching a sound of this mod.
     */
    public static void track(AbstractOnlineSoundInstance sound) {
        if (REGISTERED.compareAndSet(false, true)) {
            EtchedPlatform.get().addClientTickListener(MusicDucker::tick);
        }
        SOUNDS.add(sound);
    }

    private static void tick() {
        if (!Etched.CLIENT_CONFIG.muteGameMusic.get() || SOUNDS.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        synchronized (SOUNDS) {
            for (AbstractOnlineSoundInstance sound : SOUNDS) {
                if (minecraft.getSoundManager().isActive(sound) && sound.getVolume() > 0.0F) {
                    minecraft.getMusicManager().stopPlaying();
                    // Mods that play their own music through the music category are not stopped by the music manager
                    minecraft.getSoundManager().stop(null, SoundSource.MUSIC);
                    return;
                }
            }
        }
    }
}
