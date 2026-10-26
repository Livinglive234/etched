package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Lets radios and jukeboxes keep playing while they are mounted on Create contraptions such as trains.
 * <p>The idea and the way the music is kept in sync come from BazZziliuS' NeoForge version, https://github.com/BazZziliuS/etched
 */
public final class CreateCompat {

    private static final Field UPDATE_TAGS = findUpdateTags();

    private CreateCompat() {
    }

    private static @Nullable Field findUpdateTags() {
        try {
            Field field = Contraption.class.getDeclaredField("updateTags");
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            Etched.LOGGER.warn("Failed to access the Create contraption update tags, a jukebox with a vanilla disc may stay silent on contraptions", e);
            return null;
        }
    }

    public static void load() {
        MovementBehaviour.REGISTRY.register(EtchedBlocks.RADIO.get(), new RadioMovementBehaviour());
        MovementBehaviour.REGISTRY.register(EtchedBlocks.ALBUM_JUKEBOX.get(), new JukeboxMovementBehaviour(true));
        if (MovementBehaviour.REGISTRY.get(Blocks.JUKEBOX) == null) {
            MovementBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxMovementBehaviour(false));
        }
    }

    /**
     * @return The URL the radio should currently play or <code>null</code> if it is silent
     */
    static @Nullable String getPlayingUrl(BlockState state, @Nullable CompoundTag data) {
        if (state.hasProperty(RadioBlock.POWERED) && state.getValue(RadioBlock.POWERED)) {
            return null;
        }
        if (data == null || data.getBoolean("Stopped") || !data.contains("Url", Tag.TAG_STRING)) {
            return null;
        }
        String url = data.getString("Url");
        return StringUtil.isNullOrEmpty(url) ? null : url;
    }

    /**
     * @return The volume of a mounted radio or jukebox from 0 to 1
     */
    static float getVolume(@Nullable CompoundTag data, String key) {
        return data != null && data.contains(key, Tag.TAG_FLOAT) ? Mth.clamp(data.getFloat(key), 0.0F, 1.0F) : 1.0F;
    }

    /**
     * @return The data Create sends to clients that track the contraption, or <code>null</code> if it is not available
     */
    @SuppressWarnings("unchecked")
    static @Nullable CompoundTag getUpdateTag(Contraption contraption, BlockPos localPos) {
        if (UPDATE_TAGS == null) {
            return null;
        }
        try {
            return ((Map<BlockPos, CompoundTag>) UPDATE_TAGS.get(contraption)).get(localPos);
        } catch (Exception e) {
            return null;
        }
    }
}
