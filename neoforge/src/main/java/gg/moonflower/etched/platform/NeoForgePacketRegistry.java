package gg.moonflower.etched.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

public class NeoForgePacketRegistry implements PacketRegistry {

    private final PayloadRegistrar registrar;

    public NeoForgePacketRegistry(PayloadRegistrar registrar) {
        this.registrar = registrar;
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> clientHandler(Supplier<PacketHandler<T>> handler) {
        if (!FMLLoader.getDist().isClient()) {
            return (payload, context) -> {
            };
        }
        PacketHandler<T> clientHandler = handler.get();
        return (payload, context) -> clientHandler.handle(payload, context.player());
    }

    @Override
    public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<PacketHandler<T>> clientHandler) {
        this.registrar.playToClient(type, codec, clientHandler(clientHandler));
    }

    @Override
    public <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PacketHandler<T> serverHandler) {
        this.registrar.playToServer(type, codec, (payload, context) -> serverHandler.handle(payload, context.player()));
    }

    @Override
    public <T extends CustomPacketPayload> void playBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<PacketHandler<T>> clientHandler, PacketHandler<T> serverHandler) {
        IPayloadHandler<T> client = clientHandler(clientHandler);
        this.registrar.playBidirectional(type, codec, (payload, context) -> {
            if (context.flow().isClientbound()) {
                client.handle(payload, context);
            } else {
                serverHandler.handle(payload, context.player());
            }
        });
    }
}
