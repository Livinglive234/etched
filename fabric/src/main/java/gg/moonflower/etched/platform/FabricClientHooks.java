package gg.moonflower.etched.platform;

import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client only helpers of the Fabric platform. Kept separate so the server never loads the client classes.
 */
final class FabricClientHooks {

    private FabricClientHooks() {
    }

    static <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type, PacketHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.handle(payload, context.player()));
    }

    static BakedModel getStandaloneModel(ModelResourceLocation location) {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        BakedModel model = ((FabricBakedModelManager) manager).getModel(location.id());
        return model != null ? model : manager.getMissingModel();
    }
}
