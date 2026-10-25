package gg.moonflower.etched.core.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class EtchedFabricItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public EtchedFabricItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, FabricTagProvider.BlockTagProvider blockTags) {
        super(output, registries, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        EtchedTagGenerator.addItemTags((tag, values) -> {
            FabricTagBuilder builder = this.getOrCreateTagBuilder(tag);
            for (Item value : values) {
                builder.add(value);
            }
        });
    }
}
