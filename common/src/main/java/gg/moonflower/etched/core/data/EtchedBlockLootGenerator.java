package gg.moonflower.etched.core.data;

import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

/**
 * Defines the block loot tables of the mod for every mod loader.
 */
public final class EtchedBlockLootGenerator {

    private EtchedBlockLootGenerator() {
    }

    private static LootTable.Builder dropSelf(ItemLike item) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item))
                .when(ExplosionCondition.survivesExplosion()));
    }

    public static void generate(BiConsumer<Block, LootTable.Builder> consumer) {
        // The random sequence is added by the NeoForge loot provider but not by the Fabric one
        BiConsumer<Block, LootTable.Builder> output = (block, table) -> consumer.accept(block, table.setRandomSequence(block.getLootTable().location()));

        output.accept(EtchedBlocks.ALBUM_JUKEBOX.get(), dropSelf(EtchedBlocks.ALBUM_JUKEBOX.get()));
        output.accept(EtchedBlocks.ETCHING_TABLE.get(), dropSelf(EtchedBlocks.ETCHING_TABLE.get()));

        Block radio = EtchedBlocks.RADIO.get();
        output.accept(radio, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(AlternativesEntry.alternatives(
                                        LootItem.lootTableItem(EtchedBlocks.PORTAL_RADIO_ITEM.get())
                                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(radio)
                                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RadioBlock.PORTAL, true))),
                                        LootItem.lootTableItem(radio))
                                .when(ExplosionCondition.survivesExplosion()))));
    }
}
