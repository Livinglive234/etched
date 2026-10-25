package gg.moonflower.etched.platform;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricPlatform implements EtchedPlatform {

    private static final String MOD_ID = "etched";

    /**
     * Raw bytes written by the server when opening a menu, since fabric menu types need a codec up front.
     */
    private record MenuData(byte[] bytes) {

        private static final StreamCodec<RegistryFriendlyByteBuf, MenuData> CODEC = StreamCodec.of((buf, data) -> buf.writeByteArray(data.bytes), buf -> new MenuData(buf.readByteArray()));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public <T> Registrar<T> registrar(Registry<T> registry) {
        return new Registrar<>() {
            @Override
            public <I extends T> RegistryObject<I> register(String name, Supplier<I> factory) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
                I value = factory.get();
                Holder.Reference<T> holder = Registry.registerForHolder(registry, id, value);
                if (value instanceof PoiType poiType) {
                    @SuppressWarnings("unchecked") Holder<PoiType> poiHolder = (Holder<PoiType>) (Holder<?>) holder;
                    PoiTypes.registerBlockStates(poiHolder, poiType.matchingStates());
                }
                return new RegistryObject<>() {
                    @Override
                    public ResourceLocation id() {
                        return id;
                    }

                    @Override
                    public I get() {
                        return value;
                    }
                };
            }
        };
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return new ExtendedScreenHandlerType<T, MenuData>((containerId, inventory, data) -> factory.create(containerId, inventory, new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data.bytes()), inventory.player.registryAccess())), MenuData.CODEC);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createSimpleMenuType(SimpleMenuFactory<T> factory) {
        return new MenuType<>(factory::create, FeatureFlags.VANILLA_SET);
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... validBlocks) {
        return FabricBlockEntityTypeBuilder.create(factory::create, validBlocks).build();
    }

    @Override
    public ItemStack getCraftingRemainder(ItemStack stack) {
        return stack.getRecipeRemainder();
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraData) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
        extraData.accept(buf);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        MenuData data = new MenuData(bytes);
        player.openMenu(new ExtendedScreenHandlerFactory<MenuData>() {
            @Override
            public MenuData getScreenOpeningData(ServerPlayer player) {
                return data;
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return provider.createMenu(containerId, inventory, player);
            }
        });
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToPlayersNear(ServerLevel level, double x, double y, double z, double radius, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.around(level, new Vec3(x, y, z), radius)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos pos, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(level, pos)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, payload);
        }
        if (entity instanceof ServerPlayer self) {
            ServerPlayNetworking.send(self, payload);
        }
    }

    @Override
    public BakedModel getStandaloneModel(ModelResourceLocation location) {
        return FabricClientHooks.getStandaloneModel(location);
    }

    @Override
    public void addClientTickListener(Runnable listener) {
        ClientTickEvents.END_CLIENT_TICK.register(client -> listener.run());
    }

    @Override
    public void addClientDisconnectListener(Runnable listener) {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> listener.run());
    }
}
