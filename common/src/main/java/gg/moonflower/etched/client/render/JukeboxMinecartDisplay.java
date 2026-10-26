package gg.moonflower.etched.client.render;

import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.common.entity.MinecartJukebox;
import gg.moonflower.etched.core.mixin.BlockEntityAccessor;
import gg.moonflower.etched.core.mixin.jukebox.JukeboxBlockEntityAccessor;
import gg.moonflower.etched.core.mixin.jukebox.JukeboxSongPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Gives each jukebox minecart a jukebox block entity that is never added to a level. Mods that draw the record on top of a jukebox, such
 * as Amendments, do it with a block entity renderer, so the minecart uses that renderer with this block entity to look the same.
 */
public final class JukeboxMinecartDisplay {

    /**
     * Marks the song player as playing. It is never ticked by a level, so only mods that ask whether it is playing see it.
     */
    private static final Holder<JukeboxSong> PLAYING = Holder.direct(new JukeboxSong(SoundEvents.UI_BUTTON_CLICK, Component.empty(), 1.0E6F, 0));
    private static final Map<MinecartJukebox, JukeboxBlockEntity> DISPLAYS = new WeakHashMap<>();

    private JukeboxMinecartDisplay() {
    }

    /**
     * Updates the display block entity of the minecart. Called every client tick.
     */
    public static void tick(MinecartJukebox minecart) {
        ItemStack record = minecart.getDisplayRecord();
        if (record.isEmpty()) {
            DISPLAYS.remove(minecart);
            return;
        }

        Level level = minecart.level();
        BlockPos pos = minecart.blockPosition();
        BlockState state = Blocks.JUKEBOX.defaultBlockState().setValue(JukeboxBlock.HAS_RECORD, true);
        JukeboxBlockEntity display = DISPLAYS.get(minecart);
        if (display == null) {
            display = new JukeboxBlockEntity(pos, state);
            display.setLevel(level);
            DISPLAYS.put(minecart, display);
        }

        if (!display.getBlockPos().equals(pos)) {
            ((BlockEntityAccessor) display).etched$setWorldPosition(pos.immutable());
        }
        ((JukeboxBlockEntityAccessor) display).etched$setRawItem(record);
        ((JukeboxSongPlayerAccessor) display.getSongPlayer()).etched$setSong(SoundTracker.getEntitySound(minecart.getId()) != null ? PLAYING : null);
        JukeboxBlockEntity.tick(level, pos, state, display);
    }

    @Nullable
    public static JukeboxBlockEntity get(MinecartJukebox minecart) {
        return DISPLAYS.get(minecart);
    }
}
