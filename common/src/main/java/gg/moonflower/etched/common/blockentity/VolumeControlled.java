package gg.moonflower.etched.common.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

/**
 * A block that lets players choose how loud its music plays. Lowering the volume also lowers how far away it can be heard.
 */
public interface VolumeControlled {

    /**
     * Reads a saved volume.
     *
     * @param tag The saved data, if there is any
     * @param key The name the volume is saved under
     * @return The volume from 0 to 1, which is full when none was saved
     */
    static float read(@Nullable CompoundTag tag, String key) {
        return tag != null && tag.contains(key, Tag.TAG_FLOAT) ? Mth.clamp(tag.getFloat(key), 0.0F, 1.0F) : 1.0F;
    }


    /**
     * @return The volume from 0 to 1
     */
    float getVolume();

    /**
     * Sets the volume and tells the players that can hear it.
     *
     * @param volume The new volume from 0 to 1
     */
    void setVolume(float volume);
}
