package gg.moonflower.etched.core.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class EtchedFabricBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public EtchedFabricBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        EtchedTagGenerator.addBlockTags((tag, values) -> {
            FabricTagBuilder builder = this.getOrCreateTagBuilder(tag);
            for (Block value : values) {
                builder.add(value);
            }
        });
    }
}
