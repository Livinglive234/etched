package gg.moonflower.etched.common.network.play;

import gg.moonflower.etched.core.Etched;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

/**
 * Stops or starts the radio the player has open for everyone. A stopped radio keeps its station.
 *
 * @param stopped Whether the radio should be silent
 */
@ApiStatus.Internal
public record ServerboundRadioControlPacket(boolean stopped) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundRadioControlPacket> TYPE = new CustomPacketPayload.Type<>(Etched.etchedPath("radio_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRadioControlPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ServerboundRadioControlPacket::stopped,
            ServerboundRadioControlPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
