package gg.moonflower.etched.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.etched.common.entity.MinecartJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class JukeboxMinecartRenderer<T extends AbstractMinecart> extends MinecartRenderer<T> {

    public JukeboxMinecartRenderer(EntityRendererProvider.Context context) {
        super(context, EtchedModelLayers.JUKEBOX_MINECART);
    }

    @Override
    protected void renderMinecartContents(T entity, float partialTicks, BlockState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.renderMinecartContents(entity, partialTicks, state, poseStack, buffer, packedLight);

        if (!(entity instanceof MinecartJukebox jukebox)) {
            return;
        }

        // Vanilla draws no record on a jukebox, but mods that do register a renderer for jukebox block entities
        JukeboxBlockEntity display = JukeboxMinecartDisplay.get(jukebox);
        if (display != null) {
            BlockEntityRenderer<JukeboxBlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(display);
            if (renderer != null) {
                renderer.render(display, partialTicks, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
    }
}
