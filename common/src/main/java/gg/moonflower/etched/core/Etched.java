package gg.moonflower.etched.core;

import com.mojang.logging.LogUtils;
import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import gg.moonflower.etched.common.component.AlbumCoverComponent;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.sound.download.BandcampSource;
import gg.moonflower.etched.common.sound.download.SoundCloudSource;
import gg.moonflower.etched.core.registry.*;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import org.slf4j.Logger;

import java.util.Map;

public class Etched {

    public static final String MOD_ID = "etched";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean SOPHSTICATED_CORE_LOADED = EtchedPlatform.get().isModLoaded("sophisticatedcore");
    public static final EtchedConfig.Client CLIENT_CONFIG = new EtchedConfig.Client();
    public static final EtchedConfig.Server SERVER_CONFIG = new EtchedConfig.Server();

    /**
     * Loads every registry class so all entries are queued with the platform registrars. Must be called from the loader entrypoint.
     */
    public static void init() {
        EtchedComponents.REGISTRY.getClass();
        EtchedSounds.REGISTRY.getClass();
        EtchedBlocks.BLOCKS.getClass();
        EtchedItems.REGISTRY.getClass();
        EtchedEntities.REGISTRY.getClass();
        EtchedMenus.REGISTRY.getClass();
        EtchedRecipes.REGISTRY.getClass();
        EtchedVillagers.BARD.getClass();
    }

    public static ResourceLocation etchedPath(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Common setup that must run after registration. Safe to call on the main thread.
     */
    public static void commonSetup() {
        SoundSourceManager.registerSource(new SoundCloudSource());
        SoundSourceManager.registerSource(new BandcampSource());

        Map<Item, CauldronInteraction> map = CauldronInteraction.WATER.map();
        map.put(EtchedItems.BLANK_MUSIC_DISC.get(), CauldronInteraction.DYED_ITEM);
        map.put(EtchedItems.MUSIC_LABEL.get(), (state, level, pos, player, hand, stack) -> {
            MusicLabelComponent label = stack.get(EtchedComponents.MUSIC_LABEL.get());
            if (label == null || !label.isColored()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!level.isClientSide()) {
                ItemStack newStack = stack.copy();
                newStack.set(EtchedComponents.MUSIC_LABEL.get(), label.withColor(-1, -1));
                player.setItemInHand(hand, newStack);
                player.awardStat(Stats.CLEAN_ARMOR);
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        });
    }

    /**
     * Computes the result of removing the cover from an album cover in a grindstone.
     *
     * @param top    The top grindstone input
     * @param bottom The bottom grindstone input
     * @return The result stack, or {@link ItemStack#EMPTY} if the grindstone should behave normally
     */
    public static ItemStack getGrindstoneResult(ItemStack top, ItemStack bottom) {
        if (top.isEmpty() == bottom.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = top.isEmpty() ? bottom : top;
        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null && !albumCover.getCoverStack().isEmpty()) {
            ItemStack result = stack.copyWithCount(1);
            result.set(EtchedComponents.ALBUM_COVER.get(), albumCover.toBuilder().setCoverStack(ItemStack.EMPTY).build());
            return result;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Replaces a radio item that is entering the nether with the portal variant.
     *
     * @param entity The item entity that is traveling
     */
    public static void convertRadioForNether(ItemEntity entity) {
        ItemStack oldStack = entity.getItem();
        if (oldStack.getItem() != EtchedBlocks.RADIO.get().asItem()) {
            return;
        }

        ItemStack newStack = new ItemStack(EtchedBlocks.PORTAL_RADIO_ITEM.get(), oldStack.getCount());
        newStack.applyComponents(oldStack.getComponents());
        entity.setItem(newStack);
    }
}
