package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.item.AlbumCoverItem;
import gg.moonflower.etched.common.item.BoomboxItem;
import gg.moonflower.etched.common.item.MinecartJukeboxItem;
import gg.moonflower.etched.common.item.MusicLabelItem;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class EtchedItems {

    public static final Registrar<Item> REGISTRY = EtchedPlatform.get().registrar(BuiltInRegistries.ITEM);

    public static final RegistryObject<MusicLabelItem> MUSIC_LABEL = REGISTRY.register("music_label", () -> new MusicLabelItem(new Item.Properties().component(EtchedComponents.MUSIC_LABEL.get(), MusicLabelComponent.DEFAULT)));
    public static final RegistryObject<Item> BLANK_MUSIC_DISC = REGISTRY.register("blank_music_disc", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ETCHED_MUSIC_DISC = REGISTRY.register("etched_music_disc", () -> new Item(new Item.Properties().component(EtchedComponents.DISC_APPEARANCE.get(), DiscAppearanceComponent.DEFAULT).stacksTo(1)));
    public static final RegistryObject<MinecartJukeboxItem> JUKEBOX_MINECART = REGISTRY.register("jukebox_minecart", () -> new MinecartJukeboxItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BoomboxItem> BOOMBOX = REGISTRY.register("boombox", () -> new BoomboxItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<AlbumCoverItem> ALBUM_COVER = REGISTRY.register("album_cover", () -> new AlbumCoverItem(new Item.Properties().stacksTo(1)));

}
