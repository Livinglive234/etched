package gg.moonflower.etched.platform;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * Abstraction over the functionality that differs between mod loaders. Each loader provides one implementation through {@link ServiceLoader}.
 */
public interface EtchedPlatform {

    EtchedPlatform INSTANCE = ServiceLoader.load(EtchedPlatform.class, EtchedPlatform.class.getClassLoader()).findFirst().orElseThrow(() -> new IllegalStateException("No Etched platform implementation found"));

    static EtchedPlatform get() {
        return INSTANCE;
    }

    boolean isModLoaded(String modId);

    boolean isClient();

    /**
     * Creates a registrar for the specified registry.
     */
    <T> Registrar<T> registrar(Registry<T> registry);

    /**
     * Creates a menu type that receives extra data from the server when opened.
     */
    <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);

    /**
     * Creates a menu type that does not receive any extra data from the server.
     */
    <T extends AbstractContainerMenu> MenuType<T> createSimpleMenuType(SimpleMenuFactory<T> factory);

    /**
     * Creates a block entity type that is valid for the specified blocks.
     */
    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... validBlocks);

    /**
     * Retrieves the stack left in the crafting grid after the specified stack is used in a recipe.
     *
     * @return The remainder, or {@link ItemStack#EMPTY} if the stack has no remainder
     */
    ItemStack getCraftingRemainder(ItemStack stack);

    /**
     * Opens a menu on the client while writing extra data that is passed to the {@link MenuFactory}.
     */
    void openMenu(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraData);

    void sendToServer(CustomPacketPayload payload);

    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

    void sendToPlayersNear(ServerLevel level, double x, double y, double z, double radius, CustomPacketPayload payload);

    void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, CustomPacketPayload payload);

    void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload);

    /**
     * Retrieves a baked model that was registered as an additional model. Client only.
     *
     * @param location The location the model was registered at
     * @return The baked model, or the missing model if it does not exist
     */
    BakedModel getStandaloneModel(ModelResourceLocation location);

    /**
     * Registers a listener that runs at the end of every client tick. Client only.
     */
    void addClientTickListener(Runnable listener);

    /**
     * Registers a listener that runs when the client disconnects from a world. Client only.
     */
    void addClientDisconnectListener(Runnable listener);
}
