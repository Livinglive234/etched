package gg.moonflower.etched.core.mixin.jukebox;

import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JukeboxSongPlayer.class)
public interface JukeboxSongPlayerAccessor {

    @Accessor
    void setTicksSinceSongStarted(long ticks);

    @Accessor("song")
    void etched$setSong(@Nullable Holder<JukeboxSong> song);
}
