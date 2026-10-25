package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.core.Etched;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class EtchedTags {

    public static final TagKey<Block> RECORD_PLAYERS = TagKey.create(Registries.BLOCK, Etched.etchedPath("record_players"));
    public static final TagKey<Block> VILLAGER_JOB_SITES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "villager_job_sites"));
    public static final TagKey<EntityType<?>> MINECARTS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("c", "minecarts"));
    public static final TagKey<Item> MUSIC_DISCS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "music_discs"));
}
