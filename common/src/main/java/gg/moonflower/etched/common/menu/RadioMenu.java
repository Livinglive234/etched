package gg.moonflower.etched.common.menu;

import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedMenus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

/**
 * @author Ocelot
 */
public class RadioMenu extends AbstractContainerMenu implements UrlMenu, SoundSettingsMenu {

    private final ContainerLevelAccess access;
    private String initialUrl;
    private float initialVolume = 1.0F;

    public RadioMenu(int id, String url, float volume) {
        this(id, ContainerLevelAccess.NULL);
        this.initialUrl = url;
        this.initialVolume = volume;
    }

    public RadioMenu(int id, ContainerLevelAccess access) {
        super(EtchedMenus.RADIO_MENU.get(), id);
        this.access = access;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, EtchedBlocks.RADIO.get());
    }

    @Override
    public void setUrl(String url) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof RadioBlockEntity be) {
                be.setUrl(url);
            }
        });
    }

    @Override
    public void setVolume(float volume) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof RadioBlockEntity be) {
                be.setVolume(volume);
            }
        });
    }

    /**
     * @return The volume of the radio before opening the menu
     */
    public float getInitialVolume() {
        return this.initialVolume;
    }

    /**
     * @return The original URL inside the radio before opening the menu
     */
    public String getInitialUrl() {
        return this.initialUrl;
    }
}