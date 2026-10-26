package gg.moonflower.etched.common.blockentity;

import gg.moonflower.etched.client.radio.history.RadioHistory;
import net.minecraft.util.Mth;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author Ocelot
 */
public class RadioBlockEntity extends BlockEntity implements Clearable, VolumeControlled {

    private String url;
    private float volume = 1.0F;
    private boolean loaded;

    public RadioBlockEntity(BlockPos pos, BlockState state) {
        super(EtchedBlocks.RADIO_BE.get(), pos, state);
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, RadioBlockEntity blockEntity) {
        if (!blockEntity.loaded) {
            blockEntity.loaded = true;
            SoundTracker.playRadio(blockEntity.url, state, level, pos);
        }

        if (blockEntity.isPlaying()) {
            AABB range = new AABB(pos).inflate(3.45);
            List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, range);
            livingEntities.forEach(living -> living.setRecordPlayingNearby(pos, true));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String oldUrl = this.url;
        float oldVolume = this.volume;
        this.url = tag.contains("Url", Tag.TAG_STRING) ? tag.getString("Url") : null;
        this.volume = tag.contains("Volume", Tag.TAG_FLOAT) ? Mth.clamp(tag.getFloat("Volume"), 0.0F, 1.0F) : 1.0F;
        if (this.level != null && this.level.isClientSide() && !Objects.equals(oldUrl, this.url)) {
            RadioHistory.confirm(this.url);
        }
        if (this.loaded) {
            if (!Objects.equals(oldUrl, this.url)) {
                SoundTracker.playRadio(this.url, this.getBlockState(), this.level, this.getBlockPos());
            } else if (oldVolume != this.volume) {
                SoundTracker.setBlockVolume(this.getBlockPos(), this.volume);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.url != null) {
            tag.putString("Url", this.url);
        }
        if (this.volume != 1.0F) {
            tag.putFloat("Volume", this.volume);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clearContent() {
        this.url = null;
        if (this.level != null && this.level.isClientSide()) {
            SoundTracker.playRadio(this.url, this.getBlockState(), this.level, this.getBlockPos());
        }
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        if (!Objects.equals(this.url, url)) {
            this.url = url;
            this.setChanged();
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public void setVolume(float volume) {
        volume = Mth.clamp(volume, 0.0F, 1.0F);
        if (this.volume != volume) {
            this.volume = volume;
            this.setChanged();
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    public boolean isPlaying() {
        BlockState state = this.getBlockState();
        return (!state.hasProperty(RadioBlock.POWERED) || !state.getValue(RadioBlock.POWERED)) && !StringUtil.isNullOrEmpty(this.url);
    }
}
