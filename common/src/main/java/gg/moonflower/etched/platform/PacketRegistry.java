package gg.moonflower.etched.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Supplier;

/**
 * Registers play payloads for the current mod loader.
 */
public interface PacketRegistry {

    /**
     * Registers a payload sent from the server to the client.
     *
     * @param type          The type of the payload
     * @param codec         The codec to read and write the payload
     * @param clientHandler Supplies the handler. Only invoked on the physical client
     */
    <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<PacketHandler<T>> clientHandler);

    /**
     * Registers a payload sent from the client to the server.
     *
     * @param type          The type of the payload
     * @param codec         The codec to read and write the payload
     * @param serverHandler The handler on the server
     */
    <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PacketHandler<T> serverHandler);

    /**
     * Registers a payload sent from the client to the server that the other side does not have to support. Use
     * {@link EtchedPlatform#canSendToServer} and {@link EtchedPlatform#canClientSendToServer} to check whether it can be used.
     *
     * @param type          The type of the payload
     * @param codec         The codec to read and write the payload
     * @param serverHandler The handler on the server
     */
    default <T extends CustomPacketPayload> void playToServerOptional(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PacketHandler<T> serverHandler) {
        this.playToServer(type, codec, serverHandler);
    }

    /**
     * Registers a payload sent in both directions.
     */
    default <T extends CustomPacketPayload> void playBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<PacketHandler<T>> clientHandler, PacketHandler<T> serverHandler) {
        this.playToClient(type, codec, clientHandler);
        this.playToServer(type, codec, serverHandler);
    }
}
