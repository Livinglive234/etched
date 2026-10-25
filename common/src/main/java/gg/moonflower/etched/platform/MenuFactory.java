package gg.moonflower.etched.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Creates a menu on the client from the extra data written by the server.
 *
 * @param <T> The type of menu
 */
@FunctionalInterface
public interface MenuFactory<T extends AbstractContainerMenu> {

    T create(int containerId, Inventory inventory, RegistryFriendlyByteBuf data);
}
