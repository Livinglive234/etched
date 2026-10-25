package gg.moonflower.etched.platform;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Creates a menu on the client without any extra data from the server.
 *
 * @param <T> The type of menu
 */
@FunctionalInterface
public interface SimpleMenuFactory<T extends AbstractContainerMenu> {

    T create(int containerId, Inventory inventory);
}
