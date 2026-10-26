package gg.moonflower.etched.common.menu;

import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * @author Ocelot
 */
public class RadioMenu extends AbstractContainerMenu implements UrlMenu, SoundSettingsMenu {

    private final Controls controls;
    private final boolean moving;
    private String initialUrl;
    private float initialVolume = 1.0F;

    private BlockPos pos = BlockPos.ZERO;

    /**
     * The client side, which only needs to know what the radio was set to
     *
     * @param moving Whether the radio is on a moving contraption, where the position is not a position in the world
     */
    public RadioMenu(int id, BlockPos pos, boolean moving, String url, float volume) {
        this(id, Controls.NONE, moving);
        this.pos = pos;
        this.initialUrl = url;
        this.initialVolume = volume;
    }

    public RadioMenu(int id, ContainerLevelAccess access) {
        this(id, new Controls() {
            @Override
            public boolean stillValid(Player player) {
                return AbstractContainerMenu.stillValid(access, player, EtchedBlocks.RADIO.get());
            }

            @Override
            public void setUrl(String url) {
                withRadio(access, be -> be.setUrl(url));
            }

            @Override
            public void setStopped(boolean stopped) {
                withRadio(access, be -> be.setStopped(stopped));
            }

            @Override
            public void setVolume(float volume) {
                withRadio(access, be -> be.setVolume(volume));
            }
        }, false);
    }

    private static void withRadio(ContainerLevelAccess access, Consumer<RadioBlockEntity> action) {
        access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof RadioBlockEntity be) {
                action.accept(be);
            }
        });
    }

    /**
     * Creates the menu of a radio that is not a block in the world, such as one on a contraption.
     */
    public RadioMenu(int id, Controls controls) {
        this(id, controls, true);
    }

    private RadioMenu(int id, Controls controls, boolean moving) {
        super(EtchedMenus.RADIO_MENU.get(), id);
        this.controls = controls;
        this.moving = moving;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.controls.stillValid(player);
    }

    @Override
    public void setUrl(String url) {
        this.controls.setUrl(url);
    }

    /**
     * Stops or starts the radio for everyone without changing its station.
     */
    public void setStopped(boolean stopped) {
        this.controls.setStopped(stopped);
    }

    @Override
    public void setVolume(float volume) {
        this.controls.setVolume(volume);
    }

    /**
     * @return The volume of the radio before opening the menu
     */
    public float getInitialVolume() {
        return this.initialVolume;
    }

    /**
     * @return The position of the radio on the client. On a contraption it is a position inside the contraption
     */
    public BlockPos getPos() {
        return this.pos;
    }

    /**
     * @return Whether the radio is on a moving contraption instead of in the world
     */
    public boolean isMoving() {
        return this.moving;
    }

    /**
     * @return The original URL inside the radio before opening the menu
     */
    public String getInitialUrl() {
        return this.initialUrl;
    }

    /**
     * What the menu does to the radio it was opened for.
     */
    public interface Controls {

        Controls NONE = new Controls() {
            @Override
            public boolean stillValid(Player player) {
                return false;
            }

            @Override
            public void setUrl(String url) {
            }

            @Override
            public void setStopped(boolean stopped) {
            }

            @Override
            public void setVolume(float volume) {
            }
        };

        boolean stillValid(Player player);

        void setUrl(String url);

        void setStopped(boolean stopped);

        void setVolume(float volume);
    }
}
