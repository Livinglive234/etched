package gg.moonflower.etched.core.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import gg.moonflower.etched.client.EtchedClientSetup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Fabric replacement for the NeoForge attribute tooltip event. Adds the lines where the attribute tooltips begin.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackTooltipMixin {

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;)V"))
    public void addTooltips(Item.TooltipContext context, @Nullable Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, @Local Consumer<Component> consumer) {
        EtchedClientSetup.addTooltips((ItemStack) (Object) this, context, flag, consumer);
    }
}
