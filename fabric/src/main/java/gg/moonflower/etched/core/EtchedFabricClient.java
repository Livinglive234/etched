package gg.moonflower.etched.core;

import gg.moonflower.etched.client.EtchedClientSetup;
import gg.moonflower.etched.client.render.EtchedModelLayers;
import gg.moonflower.etched.client.render.JukeboxMinecartRenderer;
import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.client.screen.*;
import gg.moonflower.etched.core.registry.EtchedEntities;
import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.core.registry.EtchedMenus;
import gg.moonflower.etched.core.compat.vs.ValkyrienSkiesCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EtchedFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EtchedClientSetup.registerItemProperties();
        if (FabricLoader.getInstance().isModLoaded("valkyrienskies")) {
            ValkyrienSkiesCompat.load();
        }

        MenuScreens.register(EtchedMenus.ETCHING_MENU.get(), EtchingScreen::new);
        MenuScreens.register(EtchedMenus.ALBUM_JUKEBOX_MENU.get(), AlbumJukeboxScreen::new);
        MenuScreens.register(EtchedMenus.BOOMBOX_MENU.get(), BoomboxScreen::new);
        MenuScreens.register(EtchedMenus.ALBUM_COVER_MENU.get(), AlbumCoverScreen::new);
        MenuScreens.register(EtchedMenus.RADIO_MENU.get(), RadioScreen::new);
        MenuScreens.register(EtchedMenus.JUKEBOX_SETTINGS_MENU.get(), JukeboxSettingsScreen::new);

        BuiltinItemRendererRegistry.INSTANCE.register(EtchedItems.ALBUM_COVER.get(), (stack, mode, matrices, buffers, light, overlay) -> AlbumCoverItemRenderer.INSTANCE.renderByItem(stack, mode, matrices, buffers, light, overlay));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return Etched.etchedPath("album_cover_renderer");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return AlbumCoverItemRenderer.INSTANCE.reload(barrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(group).ifPresent(tab -> EtchedClientSetup.addCreativeTabItems(tab, entries::accept)));

        ModelLoadingPlugin.register(context -> EtchedClientSetup.registerAdditionalModels(model -> context.addModels(model.id())));

        EntityRendererRegistry.register(EtchedEntities.JUKEBOX_MINECART.get(), JukeboxMinecartRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(EtchedModelLayers.JUKEBOX_MINECART, MinecartModel::createBodyLayer);

        // The dyed color provider is replaced by the label color provider, as it is on NeoForge
        ColorProviderRegistry.ITEM.register(EtchedClientSetup.MUSIC_LABEL_COLOR, EtchedItems.MUSIC_LABEL.get());
        ColorProviderRegistry.ITEM.register(EtchedClientSetup.BLANK_MUSIC_DISC_COLOR, EtchedItems.BLANK_MUSIC_DISC.get());
        ColorProviderRegistry.ITEM.register(EtchedClientSetup.ETCHED_MUSIC_DISC_COLOR, EtchedItems.ETCHED_MUSIC_DISC.get());
    }
}
