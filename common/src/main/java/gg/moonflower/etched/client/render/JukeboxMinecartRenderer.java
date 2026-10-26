package gg.moonflower.etched.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.common.entity.MinecartJukebox;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxMinecartRenderer<T extends AbstractMinecart> extends MinecartRenderer<T> {

    /**
     * The jukebox model of other mods, such as Amendments, has a recessed top and draws the disc inside of it
     */
    private static final float RECESSED_TOP = 15.25F / 16.0F;
    private static final float FLAT_TOP = 1.01F;

    public JukeboxMinecartRenderer(EntityRendererProvider.Context context) {
        super(context, EtchedModelLayers.JUKEBOX_MINECART);
    }

    @Override
    protected void renderMinecartContents(T entity, float partialTicks, BlockState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.renderMinecartContents(entity, partialTicks, state, poseStack, buffer, packedLight);

        if (!(entity instanceof MinecartJukebox jukebox)) {
            return;
        }

        ItemStack record = jukebox.getDisplayRecord();
        if (record.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, EtchedPlatform.get().isModLoaded("amendments") ? RECESSED_TOP : FLAT_TOP, 0.5F);
        // The disc turns while the record is playing
        if (SoundTracker.getEntitySound(entity.getId()) != null) {
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 2.0F));
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.scale(2.0F, 2.0F, 2.0F);
        Minecraft.getInstance().getItemRenderer().renderStatic(record, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
    }
}
