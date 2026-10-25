package gg.moonflower.etched.platform;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Supplier;

public class FabricPacketRegistry implements PacketRegistry {

    private final boolean client;

    public FabricPacketRegistry(boolean client) {
        this.client = client;
    }

    @Override
    public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<PacketHandler<T>> clientHandler) {
        PayloadTypeRegistry.playS2C().register(type, codec);
        if (this.client) {
            FabricClientHooks.registerClientReceiver(type, clientHandler.get());
        }
    }

    @Override
    public <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PacketHandler<T> serverHandler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> serverHandler.handle(payload, context.player()));
    }
}
