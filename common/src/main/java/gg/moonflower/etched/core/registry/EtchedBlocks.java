package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import gg.moonflower.etched.common.block.EtchingTableBlock;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.common.item.PortalRadioItem;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;
import java.util.function.Supplier;

public class EtchedBlocks {

    public static final Registrar<Block> BLOCKS = EtchedPlatform.get().registrar(BuiltInRegistries.BLOCK);
    public static final Registrar<BlockEntityType<?>> BLOCK_ENTITIES = EtchedPlatform.get().registrar(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final RegistryObject<Block> ETCHING_TABLE = registerWithItem("etching_table", () -> new EtchingTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).strength(2.5F).sound(SoundType.WOOD)), new Item.Properties());
    public static final RegistryObject<Block> ALBUM_JUKEBOX = registerWithItem("album_jukebox", () -> new AlbumJukeboxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX)), new Item.Properties());
    public static final RegistryObject<Block> RADIO = registerWithItem("radio", () -> new RadioBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX).noOcclusion()), new Item.Properties());
    public static final RegistryObject<Item> PORTAL_RADIO_ITEM = EtchedItems.REGISTRY.register("portal_radio", () -> new PortalRadioItem(RADIO.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<AlbumJukeboxBlockEntity>> ALBUM_JUKEBOX_BE = BLOCK_ENTITIES.register("album_jukebox", () -> EtchedPlatform.get().createBlockEntityType(AlbumJukeboxBlockEntity::new, ALBUM_JUKEBOX.get()));
    public static final RegistryObject<BlockEntityType<RadioBlockEntity>> RADIO_BE = BLOCK_ENTITIES.register("radio", () -> EtchedPlatform.get().createBlockEntityType(RadioBlockEntity::new, RADIO.get()));

    /**
     * Registers a block with a simple item.
     *
     * @param id         The id of the block
     * @param block      The block to register
     * @param properties The properties of the item to register
     * @param <R>        The type of block being registered
     * @return The registered block
     */
    private static <R extends Block> RegistryObject<R> registerWithItem(String id, Supplier<R> block, Item.Properties properties) {
        return registerWithItem(id, block, object -> new BlockItem(object, properties));
    }

    /**
     * Registers a block with an item.
     *
     * @param id          The id of the block
     * @param block       The block to register
     * @param itemFactory The factory to create a new item from the registered block
     * @param <R>         The type of block being registered
     * @return The registered block
     */
    private static <R extends Block> RegistryObject<R> registerWithItem(String id, Supplier<R> block, Function<R, Item> itemFactory) {
        RegistryObject<R> register = BLOCKS.register(id, block);
        EtchedItems.REGISTRY.register(id, () -> itemFactory.apply(register.get()));
        return register;
    }
}
