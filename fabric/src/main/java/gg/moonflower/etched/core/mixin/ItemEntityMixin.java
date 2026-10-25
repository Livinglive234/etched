package gg.moonflower.etched.core.mixin;

import gg.moonflower.etched.common.item.BoomboxItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric replacement for the NeoForge item extension that is called on every item entity update.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();
        if (stack.getItem() instanceof BoomboxItem boombox) {
            boombox.onEntityItemUpdate(stack, self);
        }
    }
}
