package gg.moonflower.etched.common.network.play;

import gg.moonflower.etched.core.Etched;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;

/**
 * Sets the volume of the music player the player has open.
 * <p>
 * Based on the sound settings of BazZziliuS/etched, https://github.com/BazZziliuS/etched
 *
 * @param volume The volume from 0 to 1
 */
@ApiStatus.Internal
public record ServerboundSoundSettingsPacket(float volume) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundSoundSettingsPacket> TYPE = new CustomPacketPayload.Type<>(Etched.etchedPath("sound_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSoundSettingsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            ServerboundSoundSettingsPacket::volume,
            ServerboundSoundSettingsPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
