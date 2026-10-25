package gg.moonflower.etched.api.sound;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;

import java.util.concurrent.CompletableFuture;

/**
 * Allows a sound instance to provide its own audio stream. The signature matches the NeoForge sound instance extension, so it is picked up by NeoForge directly and by a mixin on other mod loaders.
 */
public interface StreamProvider {

    /**
     * Loads the audio stream of the specified sound.
     *
     * @param loader          The library to load vanilla sounds from
     * @param sound           The sound being played
     * @param repeatInstantly Whether the stream should loop without delay
     * @return The future for the stream
     */
    CompletableFuture<AudioStream> getStream(SoundBufferLibrary loader, Sound sound, boolean repeatInstantly);
}
