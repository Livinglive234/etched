package gg.moonflower.etched.core.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gg.moonflower.etched.api.sound.StreamProvider;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

/**
 * Fabric replacement for the NeoForge sound instance stream extension.
 */
@Mixin(SoundEngine.class)
public abstract class SoundEngineStreamMixin {

    @WrapOperation(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getStream(Lnet/minecraft/resources/ResourceLocation;Z)Ljava/util/concurrent/CompletableFuture;"))
    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary library, ResourceLocation location, boolean repeatInstantly, Operation<CompletableFuture<AudioStream>> original, @Local(argsOnly = true) SoundInstance instance, @Local Sound sound) {
        if (instance instanceof StreamProvider provider) {
            return provider.getStream(library, sound, repeatInstantly);
        }
        return original.call(library, location, repeatInstantly);
    }
}
