package gg.moonflower.etched.core;

import gg.moonflower.etched.common.network.EtchedMessages;
import gg.moonflower.etched.core.compat.SophisticatedCoreCompat;
import gg.moonflower.etched.core.data.EtchedItemModelProvider;
import gg.moonflower.etched.core.data.EtchedRecipeProvider;
import gg.moonflower.etched.core.data.loot.EtchedBlockLootProvider;
import gg.moonflower.etched.core.data.tags.EtchedBlockTagsProvider;
import gg.moonflower.etched.core.data.tags.EtchedEntityTypeTagsProvider;
import gg.moonflower.etched.core.data.tags.EtchedItemTagsProvider;
import gg.moonflower.etched.core.data.tags.EtchedPointOfInterestTagsProvider;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedComponents;
import gg.moonflower.etched.core.registry.EtchedVillagers;
import gg.moonflower.etched.platform.NeoForgePacketRegistry;
import gg.moonflower.etched.platform.NeoForgePlatform;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import gg.moonflower.etched.core.registry.EtchedItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(Etched.MOD_ID)
public class EtchedNeoForge {

    public EtchedNeoForge(IEventBus bus, ModContainer container) {
        Etched.init();
        NeoForgePlatform.registerAll(bus);

        bus.addListener(this::init);
        bus.addListener(this::registerPayloads);
        bus.addListener(this::dataInit);

        NeoForge.EVENT_BUS.addListener(this::onVillagerTrades);
        NeoForge.EVENT_BUS.addListener(this::onServerAboutToStart);
        NeoForge.EVENT_BUS.addListener(this::onGrindstoneChange);
        NeoForge.EVENT_BUS.addListener(this::onItemChangedDimension);

        EtchedNeoForgeConfig.register(container);

        if (Etched.SOPHSTICATED_CORE_LOADED) {
            SophisticatedCoreCompat.load();
        }
    }

    private void init(FMLCommonSetupEvent event) {
        event.enqueueWork(Etched::commonSetup);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        EtchedMessages.init(new NeoForgePacketRegistry(event.registrar(EtchedMessages.VERSION)));
    }

    private void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != EtchedVillagers.BARD.get()) {
            return;
        }
        EtchedVillagers.addBardTrades(tier -> event.getTrades().get(tier));
    }

    private void onServerAboutToStart(ServerAboutToStartEvent event) {
        EtchedVillagers.addVillagePieces(event.getServer());
    }

    private void onGrindstoneChange(GrindstoneEvent.OnPlaceItem event) {
        ItemStack result = Etched.getGrindstoneResult(event.getTopItem(), event.getBottomItem());
        if (!result.isEmpty()) {
            event.setOutput(result);
        }
    }

    private void onItemChangedDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ItemEntity entity && event.getDimension() == Level.NETHER) {
            Etched.convertRadioForNether(entity);
        }
    }

    private void dataInit(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EtchedBlockTagsProvider blockTags = new EtchedBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new EtchedItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        gen.addProvider(event.includeServer(), new EtchedEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new EtchedPointOfInterestTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new EtchedRecipeProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new LootTableProvider(packOutput,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(
                        EtchedBlockLootProvider::new,
                        LootContextParamSets.BLOCK
                )),
                lookupProvider
        ));

        gen.addProvider(event.includeClient(), new EtchedItemModelProvider(packOutput, existingFileHelper));
    }
}
