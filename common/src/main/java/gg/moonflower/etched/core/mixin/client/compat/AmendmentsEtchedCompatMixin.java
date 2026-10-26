package gg.moonflower.etched.core.mixin.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Amendments draws Etched discs on top of jukeboxes and only draws the colorable label patterns. The gold label has a fixed color, so
 * it is missing from the disc. Draws it the same way Amendments draws the other labels.
 */
@Pseudo
@Mixin(targets = "net.mehvahdjukaar.amendments.integration.EtchedCompat", remap = false)
public class AmendmentsEtchedCompatMixin {

    private static final Material GOLD_LABEL = new Material(TextureAtlas.LOCATION_BLOCKS, Etched.etchedPath("item/gold_label"));

    @Inject(method = "drawDisc", at = @At("TAIL"), remap = false, require = 0)
    private static void etched$drawGoldLabel(ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource, int lu, int lv, CallbackInfo ci) {
        DiscAppearanceComponent appearance = item.get(EtchedComponents.DISC_APPEARANCE.get());
        if (appearance == null || appearance.pattern().isColorable()) {
            return;
        }

        VertexConsumer builder = GOLD_LABEL.buffer(bufferSource, RenderType::entityCutout);
        Vector3f normal = poseStack.last().normal().transform(new Vector3f(0, 0, -1));
        etched$vertex(builder, poseStack, -0.5F, 0.5F, 1, 0, lu, lv, normal);
        etched$vertex(builder, poseStack, 0.5F, 0.5F, 0, 0, lu, lv, normal);
        etched$vertex(builder, poseStack, 0.5F, -0.5F, 0, 1, lu, lv, normal);
        etched$vertex(builder, poseStack, -0.5F, -0.5F, 1, 1, lu, lv, normal);
    }

    private static void etched$vertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float u, float v, int lu, int lv, Vector3f normal) {
        builder.addVertex(poseStack.last().pose(), x, y, 0);
        builder.setColor(255, 255, 255, 255);
        builder.setUv(u, v);
        builder.setUv1(0, 10);
        builder.setUv2(lu, lv);
        builder.setNormal(normal.x, normal.y, normal.z);
    }
}
