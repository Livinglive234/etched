package gg.moonflower.etched.core.mixin.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Amendments draws Etched discs on top of jukeboxes and only draws the colorable label patterns. The gold label has a fixed color, so
 * it is missing from the disc. Draws the label the way Amendments draws the others, using its sixth pattern in the colors of the
 * gold label.
 */
@Pseudo
@Mixin(targets = "net.mehvahdjukaar.amendments.integration.EtchedCompat", remap = false)
public class AmendmentsEtchedCompatMixin {

    private static final Material GOLD_LABEL = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath("amendments", "block/music_discs/music_disc_pattern_5"));
    private static final Material GOLD_LABEL_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath("amendments", "block/music_discs/music_disc_pattern_5s"));

    @Inject(method = "drawDisc", at = @At("TAIL"), remap = false, require = 0)
    private static void etched$drawGoldLabel(ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource, int lu, int lv, CallbackInfo ci) {
        DiscAppearanceComponent appearance = item.get(EtchedComponents.DISC_APPEARANCE.get());
        if (appearance == null || appearance.pattern().isColorable()) {
            return;
        }

        // The yellow and orange of the gold label texture
        etched$drawQuad(GOLD_LABEL.buffer(bufferSource, RenderType::entityCutout), poseStack, lu, lv, 253, 245, 95);
        etched$drawQuad(GOLD_LABEL_OVERLAY.buffer(bufferSource, RenderType::entityCutout), poseStack, lu, lv, 233, 125, 21);
    }

    private static void etched$drawQuad(VertexConsumer builder, PoseStack poseStack, int lu, int lv, int r, int g, int b) {
        Vector3f normal = poseStack.last().normal().transform(new Vector3f(0, 0, -1));
        etched$vertex(builder, poseStack, -0.5F, 0.5F, 1, 0, lu, lv, r, g, b, normal);
        etched$vertex(builder, poseStack, 0.5F, 0.5F, 0, 0, lu, lv, r, g, b, normal);
        etched$vertex(builder, poseStack, 0.5F, -0.5F, 0, 1, lu, lv, r, g, b, normal);
        etched$vertex(builder, poseStack, -0.5F, -0.5F, 1, 1, lu, lv, r, g, b, normal);
    }

    private static void etched$vertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float u, float v, int lu, int lv, int r, int g, int b, Vector3f normal) {
        builder.addVertex(poseStack.last().pose(), x, y, 0);
        builder.setColor(r, g, b, 255);
        builder.setUv(u, v);
        builder.setUv1(0, 10);
        builder.setUv2(lu, lv);
        builder.setNormal(normal.x, normal.y, normal.z);
    }
}
