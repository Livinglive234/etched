package gg.moonflower.etched.common.menu;

import gg.moonflower.etched.common.blockentity.VolumeControlled;
import gg.moonflower.etched.core.registry.EtchedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * The menu to set the volume of a vanilla jukebox. It has no slots.
 */
public class JukeboxSettingsMenu extends AbstractContainerMenu implements SoundSettingsMenu {

    private final ContainerLevelAccess access;
    private final float initialVolume;

    private BlockPos pos = BlockPos.ZERO;

    public JukeboxSettingsMenu(int id, BlockPos pos, float volume) {
        this(id, ContainerLevelAccess.NULL, volume);
        this.pos = pos;
    }

    public JukeboxSettingsMenu(int id, ContainerLevelAccess access, float volume) {
        super(EtchedMenus.JUKEBOX_SETTINGS_MENU.get(), id);
        this.access = access;
        this.initialVolume = volume;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Blocks.JUKEBOX);
    }

    @Override
    public void setVolume(float volume) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof VolumeControlled jukebox) {
                jukebox.setVolume(volume);
            }
        });
    }

    /**
     * @return The position of the jukebox on the client
     */
    public BlockPos getPos() {
        return this.pos;
    }

    /**
     * @return The volume of the jukebox before opening the menu
     */
    public float getInitialVolume() {
        return this.initialVolume;
    }
}
