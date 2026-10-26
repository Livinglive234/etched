package gg.moonflower.etched.core.mixin.jukebox;

import gg.moonflower.etched.common.blockentity.VolumeControlled;
import gg.moonflower.etched.common.menu.JukeboxSettingsMenu;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Opens the volume settings when a player with empty hands uses a jukebox while sneaking, instead of taking out the disc.
 */
@Mixin(JukeboxBlock.class)
public class JukeboxBlockMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    public void openSettings(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isSecondaryUseActive() || !(level.getBlockEntity(pos) instanceof VolumeControlled jukebox)) {
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            EtchedPlatform.get().openMenu(serverPlayer, new SimpleMenuProvider((id, inventory, p) -> new JukeboxSettingsMenu(id, ContainerLevelAccess.create(level, pos), jukebox.getVolume()), Component.translatable("container." + Etched.MOD_ID + ".jukebox_settings")), buf -> buf.writeFloat(jukebox.getVolume()));
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
    }
}
