package gg.moonflower.etched.core.data.loot;

import gg.moonflower.etched.core.data.EtchedBlockLootGenerator;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Map;
import java.util.Set;

public class EtchedBlockLootProvider extends BlockLootSubProvider {
    public EtchedBlockLootProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet()
                .stream()
                .filter(e -> e.getKey().location().getNamespace().equals(Etched.MOD_ID))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    protected void generate() {
        EtchedBlockLootGenerator.generate(this::add);
    }
}
