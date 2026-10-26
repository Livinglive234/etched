package gg.moonflower.etched.core;

import gg.moonflower.etched.client.EtchedClientSetup;
import gg.moonflower.etched.client.compat.SableCompat;
import gg.moonflower.etched.client.render.EtchedModelLayers;
import gg.moonflower.etched.client.render.JukeboxMinecartRenderer;
import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.client.screen.*;
import gg.moonflower.etched.core.registry.EtchedEntities;
import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.core.registry.EtchedMenus;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(value = Etched.MOD_ID, dist = Dist.CLIENT)
public class EtchedNeoForgeClient {

    public EtchedNeoForgeClient(IEventBus bus) {
        bus.addListener(this::clientInit);
        bus.addListener(this::registerScreens);
        bus.addListener(this::registerClientExtensions);
        bus.addListener(this::registerReloadListeners);
        bus.addListener(this::registerItemGroups);
        bus.addListener(this::registerCustomModels);
        bus.addListener(this::registerEntityRenders);
        bus.addListener(this::registerEntityLayers);
        bus.addListener(this::registerItemColors);

        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onItemTooltip);
    }

    private void clientInit(FMLClientSetupEvent event) {
        event.enqueueWork(EtchedClientSetup::registerItemProperties);
        if (ModList.get().isLoaded("sable")) {
            event.enqueueWork(SableCompat::load);
        }
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(EtchedMenus.ETCHING_MENU.get(), EtchingScreen::new);
        event.register(EtchedMenus.ALBUM_JUKEBOX_MENU.get(), AlbumJukeboxScreen::new);
        event.register(EtchedMenus.BOOMBOX_MENU.get(), BoomboxScreen::new);
        event.register(EtchedMenus.ALBUM_COVER_MENU.get(), AlbumCoverScreen::new);
        event.register(EtchedMenus.RADIO_MENU.get(), RadioScreen::new);
        event.register(EtchedMenus.JUKEBOX_SETTINGS_MENU.get(), JukeboxSettingsScreen::new);
    }

    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return AlbumCoverItemRenderer.INSTANCE;
            }
        }, EtchedItems.ALBUM_COVER.get());
    }

    private void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AlbumCoverItemRenderer.INSTANCE);
    }

    private void registerItemGroups(BuildCreativeModeTabContentsEvent event) {
        EtchedClientSetup.addCreativeTabItems(event.getTabKey(), event::accept);
    }

    private void registerCustomModels(ModelEvent.RegisterAdditional event) {
        EtchedClientSetup.registerAdditionalModels(event::register);
    }

    private void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EtchedEntities.JUKEBOX_MINECART.get(), JukeboxMinecartRenderer::new);
    }

    private void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EtchedModelLayers.JUKEBOX_MINECART, MinecartModel::createBodyLayer);
    }

    private void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(EtchedClientSetup.MUSIC_LABEL_COLOR, EtchedItems.MUSIC_LABEL.get());
        event.register(EtchedClientSetup.BLANK_MUSIC_DISC_COLOR, EtchedItems.BLANK_MUSIC_DISC.get());
        event.register(EtchedClientSetup.ETCHED_MUSIC_DISC_COLOR, EtchedItems.ETCHED_MUSIC_DISC.get());
    }

    private void onItemTooltip(AddAttributeTooltipsEvent event) {
        EtchedClientSetup.addTooltips(event.getStack(), event.getContext(), event.getContext().flag(), event::addTooltipLines);
    }
}
