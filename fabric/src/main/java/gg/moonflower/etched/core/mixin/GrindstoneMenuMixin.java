package gg.moonflower.etched.core.mixin;

import gg.moonflower.etched.common.item.AlbumCoverItem;
import gg.moonflower.etched.core.Etched;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric replacement for the NeoForge grindstone event and the item grindstone extension.
 */
@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {

    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
    public void computeResult(ItemStack top, ItemStack bottom, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = Etched.getGrindstoneResult(top, bottom);
        if (!result.isEmpty()) {
            cir.setReturnValue(result);
        }
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 0))
    public Slot allowAlbumCoversInTopSlot(Slot slot) {
        return etched$allowAlbumCovers(slot);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 1))
    public Slot allowAlbumCoversInBottomSlot(Slot slot) {
        return etched$allowAlbumCovers(slot);
    }

    private static Slot etched$allowAlbumCovers(Slot slot) {
        return new Slot(slot.container, slot.getContainerSlot(), slot.x, slot.y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return slot.mayPlace(stack) || stack.getItem() instanceof AlbumCoverItem item && item.canGrindstoneRepair(stack);
            }
        };
    }
}
