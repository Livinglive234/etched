package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.Contraption;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.VolumeControlled;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Lets radios and jukeboxes keep playing while they are mounted on Create contraptions such as trains.
 * <p>The idea and the way the music is kept in sync come from BazZziliuS' NeoForge version, https://github.com/BazZziliuS/etched
 */
public final class CreateCompat {

    private static final double MAX_USE_DISTANCE_SQ = 8.0 * 8.0;
    private static final Field UPDATE_TAGS = findUpdateTags();

    private CreateCompat() {
    }

    private static @Nullable Field findUpdateTags() {
        try {
            Field field = Contraption.class.getDeclaredField("updateTags");
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            Etched.LOGGER.warn("Failed to access the Create contraption update tags, a jukebox with a vanilla disc may stay silent on contraptions", e);
            return null;
        }
    }

    public static void load() {
        PayloadTypeRegistry.playS2C().register(ClientboundContraptionBlockDataPacket.TYPE, ClientboundContraptionBlockDataPacket.CODEC);
        MovementBehaviour.REGISTRY.register(EtchedBlocks.RADIO.get(), new RadioMovementBehaviour());
        MovingInteractionBehaviour.REGISTRY.register(EtchedBlocks.RADIO.get(), new RadioMovingInteraction());
        MovementBehaviour.REGISTRY.register(EtchedBlocks.ALBUM_JUKEBOX.get(), new JukeboxMovementBehaviour(true));
        MovingInteractionBehaviour.REGISTRY.register(EtchedBlocks.ALBUM_JUKEBOX.get(), new AlbumJukeboxMovingInteraction());
        if (MovementBehaviour.REGISTRY.get(Blocks.JUKEBOX) == null) {
            MovementBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxMovementBehaviour(false));
        }
    }

    /**
     * @return The URL the radio should currently play or <code>null</code> if it is silent
     */
    static @Nullable String getPlayingUrl(BlockState state, @Nullable CompoundTag data) {
        if (state.hasProperty(RadioBlock.POWERED) && state.getValue(RadioBlock.POWERED)) {
            return null;
        }
        if (data == null || data.getBoolean("Stopped") || !data.contains("Url", Tag.TAG_STRING)) {
            return null;
        }
        String url = data.getString("Url");
        return StringUtil.isNullOrEmpty(url) ? null : url;
    }

    /**
     * @return The volume of a mounted radio or jukebox from 0 to 1
     */
    static float getVolume(@Nullable CompoundTag data, String key) {
        return VolumeControlled.read(data, key);
    }

    /**
     * @return The data of the block at the position in the contraption that holds it, or <code>null</code> if it has none
     */
    static @Nullable CompoundTag getBlockData(Object entity, BlockPos localPos) {
        Contraption contraption = ContraptionAccess.contraption(entity);
        StructureBlockInfo info = contraption != null ? contraption.getBlocks().get(localPos) : null;
        return info != null ? info.nbt() : null;
    }

    /**
     * @return Whether the player is close enough to a radio or jukebox that is still on the contraption
     */
    static boolean canUse(Object entity, BlockPos localPos, Block block, Player player) {
        Entity holder = ContraptionAccess.entity(entity);
        Contraption contraption = ContraptionAccess.contraption(entity);
        if (holder == null || contraption == null || !holder.isAlive()) {
            return false;
        }

        StructureBlockInfo info = contraption.getBlocks().get(localPos);
        MutablePair<StructureBlockInfo, MovementContext> actor = contraption.getActorAt(localPos);
        Vec3 position = actor != null && actor.right != null && actor.right.position != null ? actor.right.position : holder.position();
        return info != null && info.state().is(block) && player.distanceToSqr(position) <= MAX_USE_DISTANCE_SQ;
    }

    /**
     * Changes the data of a radio or jukebox on a contraption and tells every client that tracks it.
     *
     * @param data The data that changed, merged into what is stored
     */
    static void updateBlockData(Object entity, BlockPos localPos, CompoundTag data) {
        Entity holder = ContraptionAccess.entity(entity);
        if (holder != null && holder.isAlive() && applyBlockData(ContraptionAccess.contraption(entity), localPos, data)) {
            ClientboundContraptionBlockDataPacket packet = new ClientboundContraptionBlockDataPacket(holder.getId(), localPos, data);
            for (ServerPlayer player : PlayerLookup.tracking(holder)) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    /**
     * Merges data into every copy of a block's data, so it is kept when the contraption is saved, sent to players who start tracking it or taken apart.
     */
    static boolean applyBlockData(@Nullable Contraption contraption, BlockPos localPos, CompoundTag data) {
        if (contraption == null) {
            return false;
        }

        StructureBlockInfo info = contraption.getBlocks().get(localPos);
        if (info == null) {
            return false;
        }

        if (info.nbt() == null) {
            info = new StructureBlockInfo(info.pos(), info.state(), new CompoundTag());
            contraption.getBlocks().put(localPos, info);
        }
        info.nbt().merge(data);

        // Players who start tracking the contraption later are sent the update tags instead of the block data
        CompoundTag updateTag = getUpdateTag(contraption, localPos);
        if (updateTag != null) {
            updateTag.merge(data);
        }

        // Actors may hold their own copy of the data
        MutablePair<StructureBlockInfo, MovementContext> actor = contraption.getActorAt(localPos);
        if (actor != null) {
            if (actor.left.nbt() != null) {
                actor.left.nbt().merge(data);
            }
            MovementContext context = actor.right;
            if (context != null) {
                if (context.blockEntityData == null) {
                    context.blockEntityData = info.nbt();
                }
                context.blockEntityData.merge(data);
            }
        }
        return true;
    }

    /**
     * @return The data Create sends to clients that track the contraption, or <code>null</code> if it is not available
     */
    @SuppressWarnings("unchecked")
    static @Nullable CompoundTag getUpdateTag(Contraption contraption, BlockPos localPos) {
        if (UPDATE_TAGS == null) {
            return null;
        }
        try {
            return ((Map<BlockPos, CompoundTag>) UPDATE_TAGS.get(contraption)).get(localPos);
        } catch (Exception e) {
            return null;
        }
    }
}
