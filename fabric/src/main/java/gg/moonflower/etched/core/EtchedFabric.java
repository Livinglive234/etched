package gg.moonflower.etched.core;

import gg.moonflower.etched.common.network.EtchedMessages;
import gg.moonflower.etched.core.registry.EtchedVillagers;
import gg.moonflower.etched.platform.FabricPacketRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtchedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Etched.init();
        EtchedFabricConfig.register();
        EtchedMessages.init(new FabricPacketRegistry(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT));
        Etched.commonSetup();

        Map<Integer, List<VillagerTrades.ItemListing>> bardTrades = new HashMap<>();
        EtchedVillagers.addBardTrades(tier -> bardTrades.computeIfAbsent(tier, key -> new ArrayList<>()));
        bardTrades.forEach((tier, trades) -> TradeOfferHelper.registerVillagerOffers(EtchedVillagers.BARD.get(), tier, factories -> factories.addAll(trades)));

        ServerLifecycleEvents.SERVER_STARTING.register(EtchedVillagers::addVillagePieces);
        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) -> {
            if (newEntity instanceof ItemEntity entity && destination.dimension() == Level.NETHER) {
                Etched.convertRadioForNether(entity);
            }
        });
    }
}
