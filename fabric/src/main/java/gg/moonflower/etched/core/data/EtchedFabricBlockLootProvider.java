package gg.moonflower.etched.core.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class EtchedFabricBlockLootProvider extends FabricBlockLootTableProvider {

    public EtchedFabricBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        EtchedBlockLootGenerator.generate(this::add);
    }
}
