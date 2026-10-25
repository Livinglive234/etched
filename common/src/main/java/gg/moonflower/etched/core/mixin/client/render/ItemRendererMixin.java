package gg.moonflower.etched.core.mixin.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.etched.client.EtchedClientSetup;
import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    /**
     * The boombox uses a larger model in the hand and the flat gui model everywhere else, like the trident.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void useBoomboxGuiModel(ItemStack stack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        if (!stack.is(EtchedItems.BOOMBOX.get()) || displayContext != ItemDisplayContext.GUI && displayContext != ItemDisplayContext.GROUND && displayContext != ItemDisplayContext.FIXED) {
            return;
        }

        BakedModel guiModel = EtchedPlatform.get().getStandaloneModel(new ModelResourceLocation(EtchedClientSetup.BOOMBOX_GUI_MODEL, "standalone"));
        if (model != guiModel) {
            ((ItemRenderer) (Object) this).render(stack, displayContext, leftHand, poseStack, buffer, combinedLight, combinedOverlay, guiModel);
            ci.cancel();
        }
    }
}
