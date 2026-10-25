package gg.moonflower.etched.core.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.concurrent.CompletableFuture;

public class EtchedFabricPoiTypeTagProvider extends FabricTagProvider<PoiType> {

    public EtchedFabricPoiTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        EtchedTagGenerator.addPoiTypeTags((tag, values) -> {
            FabricTagBuilder builder = this.getOrCreateTagBuilder(tag);
            for (ResourceKey<PoiType> value : values) {
                builder.add(value);
            }
        });
    }
}
