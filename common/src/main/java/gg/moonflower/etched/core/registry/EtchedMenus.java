package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.common.menu.*;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class EtchedMenus {

    public static final Registrar<MenuType<?>> REGISTRY = EtchedPlatform.get().registrar(BuiltInRegistries.MENU);

    public static final RegistryObject<MenuType<EtchingMenu>> ETCHING_MENU = REGISTRY.register("etching_table", () -> EtchedPlatform.get().<EtchingMenu>createSimpleMenuType(EtchingMenu::new));
    public static final RegistryObject<MenuType<AlbumJukeboxMenu>> ALBUM_JUKEBOX_MENU = REGISTRY.register("album_jukebox", () -> EtchedPlatform.get().<AlbumJukeboxMenu>createMenuType((windowId, inv, data) -> new AlbumJukeboxMenu(windowId, inv, data.readBlockPos())));
    public static final RegistryObject<MenuType<BoomboxMenu>> BOOMBOX_MENU = REGISTRY.register("boombox", () -> EtchedPlatform.get().<BoomboxMenu>createSimpleMenuType(BoomboxMenu::new));
    public static final RegistryObject<MenuType<AlbumCoverMenu>> ALBUM_COVER_MENU = REGISTRY.register("album_cover", () -> EtchedPlatform.get().<AlbumCoverMenu>createSimpleMenuType(AlbumCoverMenu::new));
    public static final RegistryObject<MenuType<JukeboxSettingsMenu>> JUKEBOX_SETTINGS_MENU = REGISTRY.register("jukebox_settings", () -> EtchedPlatform.get().<JukeboxSettingsMenu>createMenuType((windowId, inv, data) -> new JukeboxSettingsMenu(windowId, data.readBlockPos(), data.readFloat())));
    public static final RegistryObject<MenuType<RadioMenu>> RADIO_MENU = REGISTRY.register("radio", () -> EtchedPlatform.get().<RadioMenu>createMenuType((windowId, inv, data) -> new RadioMenu(windowId, data.readBlockPos(), data.readUtf(), data.readFloat())));

}
