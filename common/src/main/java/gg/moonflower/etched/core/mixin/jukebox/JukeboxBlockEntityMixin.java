package gg.moonflower.etched.core.mixin.jukebox;

import net.minecraft.util.Mth;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import gg.moonflower.etched.common.blockentity.VolumeControlled;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.common.network.play.ClientboundPlayBlockMusicPacket;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements VolumeControlled {

    @Shadow
    private ItemStack item;

    @Shadow
    @Final
    private JukeboxSongPlayer jukeboxSongPlayer;

    @Shadow
    public abstract void onSongChanged();

    @Unique
    private boolean etched$playing;
    @Unique
    private float etched$volume = 1.0F;

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "setTheItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSongPlayer;stop(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void stop(ItemStack stack, CallbackInfo ci) {
        if (this.level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.getBlockPos();
            if (PlayableRecord.isPlayableRecord(stack)) {
                EtchedPlatform.get().sendToPlayersNear(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 64, new ClientboundPlayBlockMusicPacket(stack.copy(), pos, null));
                this.etched$playing = true;
            } else if (this.etched$playing) {
                this.etched$playing = false;
                ((JukeboxSongPlayerAccessor) this.jukeboxSongPlayer).setTicksSinceSongStarted(0L);
                this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Context.of(this.getBlockState()));
                this.level.levelEvent(1011, pos, 0);
                this.onSongChanged();
            }
        }
    }

    @Inject(method = "getComparatorOutput", at = @At("TAIL"), cancellable = true)
    public void getComparatorOutput(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() == 0 && PlayableRecord.isPlayableRecord(this.item)) {
            cir.setReturnValue(15);
        }
    }

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    public void canPlaceItem(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        JukeboxBlockEntity self = (JukeboxBlockEntity)(Object)this;
        if (stack.has(EtchedComponents.DISC_APPEARANCE.get())
                && self.getItem(slot).isEmpty()) cir.setReturnValue(true);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    public void loadVolume(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        float oldVolume = this.etched$volume;
        this.etched$volume = VolumeControlled.read(tag, "EtchedVolume");
        if (oldVolume != this.etched$volume && this.level != null && this.level.isClientSide()) {
            SoundTracker.onJukeboxVolumeChanged(this.level, this.getBlockPos(), (JukeboxBlockEntity) (Object) this, this.etched$volume);
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void saveVolume(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.etched$volume != 1.0F) {
            tag.putFloat("EtchedVolume", this.etched$volume);
        }
    }

    @Override
    public float getVolume() {
        return this.etched$volume;
    }

    @Override
    public void setVolume(float volume) {
        volume = Mth.clamp(volume, 0.0F, 1.0F);
        if (this.etched$volume != volume) {
            this.etched$volume = volume;
            this.setChanged();
            if (this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }
}
