package gg.moonflower.etched.core.data.tags;

import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.data.EtchedTagGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EtchedPointOfInterestTagsProvider extends PoiTypeTagsProvider {
    public EtchedPointOfInterestTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, Etched.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        EtchedTagGenerator.addPoiTypeTags((tag, values) -> this.tag(tag).add(values.toArray(ResourceKey[]::new)));
    }
}
