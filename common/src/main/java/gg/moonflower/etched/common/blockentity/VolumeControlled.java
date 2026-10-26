package gg.moonflower.etched.common.blockentity;

/**
 * A block that lets players choose how loud its music plays. Lowering the volume also lowers how far away it can be heard.
 */
public interface VolumeControlled {

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
