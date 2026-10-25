package gg.moonflower.etched.core.data;

import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedEntities;
import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.core.registry.EtchedTags;
import gg.moonflower.etched.core.registry.EtchedVillagers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Defines the tags of the mod for every mod loader.
 */
public final class EtchedTagGenerator {

    private EtchedTagGenerator() {
    }

    public static void addBlockTags(TagAdder<Block> tags) {
        tags.add(EtchedTags.RECORD_PLAYERS, List.of(EtchedBlocks.ALBUM_JUKEBOX.get(), EtchedBlocks.RADIO.get(), Blocks.JUKEBOX));
        tags.add(BlockTags.MINEABLE_WITH_AXE, List.of(EtchedBlocks.ETCHING_TABLE.get(), EtchedBlocks.ALBUM_JUKEBOX.get(), EtchedBlocks.RADIO.get()));
        tags.add(EtchedTags.VILLAGER_JOB_SITES, List.of(Blocks.NOTE_BLOCK));
    }

    public static void addItemTags(TagAdder<Item> tags) {
        tags.add(ItemTags.DYEABLE, List.of(EtchedItems.BLANK_MUSIC_DISC.get()));
        tags.add(EtchedTags.MUSIC_DISCS, List.of(EtchedItems.ETCHED_MUSIC_DISC.get()));
        tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "paper")), List.of(Items.PAPER));
    }

    public static void addEntityTypeTags(TagAdder<EntityType<?>> tags) {
        tags.add(EtchedTags.MINECARTS, List.of(EtchedEntities.JUKEBOX_MINECART.get()));
    }

    public static void addPoiTypeTags(BiConsumer<TagKey<PoiType>, List<ResourceKey<PoiType>>> tags) {
        tags.accept(PoiTypeTags.ACQUIRABLE_JOB_SITE, List.of(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, EtchedVillagers.BARD_POI.id())));
    }
}
