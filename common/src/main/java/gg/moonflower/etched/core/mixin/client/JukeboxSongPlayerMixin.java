package gg.moonflower.etched.core.mixin.client;

import gg.moonflower.etched.core.mixin.client.render.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.JukeboxSongPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Etched discs are played by the sound tracker instead of the jukebox song player, so the song player never reports them as playing.
 * Other mods, such as ones that spin the disc on top of a jukebox, use that to know when a record is playing.
 */
@Mixin(JukeboxSongPlayer.class)
public class JukeboxSongPlayerMixin {

    @Shadow
    @Final
    private BlockPos blockPos;

    @Inject(method = "isPlaying", at = @At("RETURN"), cancellable = true)
    public void isEtchedPlaying(CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        // The integrated server uses the same class, but only the client knows which sounds are playing
        if (!cir.getReturnValueZ() && minecraft.isSameThread() && minecraft.levelRenderer != null && ((LevelRendererAccessor) minecraft.levelRenderer).getPlayingJukeboxSongs().containsKey(this.blockPos)) {
            cir.setReturnValue(true);
        }
    }
}
