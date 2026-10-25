package gg.moonflower.etched.core.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class EtchedFabricEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public EtchedFabricEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        EtchedTagGenerator.addEntityTypeTags((tag, values) -> {
            FabricTagBuilder builder = this.getOrCreateTagBuilder(tag);
            for (EntityType<?> value : values) {
                builder.add(value);
            }
        });
    }
}
