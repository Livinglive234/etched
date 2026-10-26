package gg.moonflower.etched.core.compat.create;

import gg.moonflower.etched.core.Etched;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Tells clients about a change to the data of a radio or jukebox on a contraption.
 * <p>Sending the changed data to the clients that track the contraption follows BazZziliuS' NeoForge version.
 *
 * @param entityId The id of the entity carrying the contraption
 * @param localPos The position of the block inside the contraption
 * @param data     The data that changed, merged into what the client has
 */
public record ClientboundContraptionBlockDataPacket(int entityId, BlockPos localPos, CompoundTag data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundContraptionBlockDataPacket> TYPE = new CustomPacketPayload.Type<>(Etched.etchedPath("contraption_block_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundContraptionBlockDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundContraptionBlockDataPacket::entityId,
            BlockPos.STREAM_CODEC,
            ClientboundContraptionBlockDataPacket::localPos,
            ByteBufCodecs.COMPOUND_TAG,
            ClientboundContraptionBlockDataPacket::data,
            ClientboundContraptionBlockDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
