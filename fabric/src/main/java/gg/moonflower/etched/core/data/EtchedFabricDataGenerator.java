package gg.moonflower.etched.core.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EtchedFabricDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        EtchedFabricBlockTagProvider blockTags = pack.addProvider(EtchedFabricBlockTagProvider::new);
        pack.addProvider((output, registries) -> new EtchedFabricItemTagProvider(output, registries, blockTags));
        pack.addProvider(EtchedFabricEntityTypeTagProvider::new);
        pack.addProvider(EtchedFabricPoiTypeTagProvider::new);
        pack.addProvider(EtchedFabricRecipeProvider::new);
        pack.addProvider(EtchedFabricBlockLootProvider::new);
        pack.addProvider(EtchedFabricItemModelProvider::new);
    }
}
