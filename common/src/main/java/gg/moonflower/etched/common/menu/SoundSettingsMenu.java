package gg.moonflower.etched.common.menu;

/**
 * A menu of a music player that lets the player choose how loud it plays.
 */
public interface SoundSettingsMenu {

    /**
     * Sets the volume of the music player. Lowering it also lowers how far away it can be heard.
     *
     * @param volume The new volume from 0 to 1
     */
    void setVolume(float volume);
}
