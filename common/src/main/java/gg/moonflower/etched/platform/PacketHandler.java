package gg.moonflower.etched.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/**
 * Handles a received payload on the main thread of the receiving side.
 *
 * @param <T> The type of payload
 */
@FunctionalInterface
public interface PacketHandler<T extends CustomPacketPayload> {

    /**
     * Handles the payload.
     *
     * @param payload The received payload
     * @param player  The local player on the client, or the sending player on the server
     */
    void handle(T payload, Player player);
}
