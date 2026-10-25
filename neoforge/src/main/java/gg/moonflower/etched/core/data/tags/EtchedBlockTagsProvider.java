package gg.moonflower.etched.core.data.tags;

import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.data.EtchedTagGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EtchedBlockTagsProvider extends BlockTagsProvider {

    public EtchedBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Etched.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        EtchedTagGenerator.addBlockTags((tag, values) -> this.tag(tag).add(values.toArray(net.minecraft.world.level.block.Block[]::new)));
    }
}
