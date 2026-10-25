package gg.moonflower.etched.client;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.common.component.TooltipHelper;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.item.BoomboxItem;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedComponents;
import gg.moonflower.etched.core.registry.EtchedItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Client setup shared between all mod loaders.
 */
public final class EtchedClientSetup {

    private static final ResourceLocation TOOLS_AND_UTILITIES = ResourceLocation.withDefaultNamespace("tools_and_utilities");
    private static final ResourceLocation REDSTONE_BLOCKS = ResourceLocation.withDefaultNamespace("redstone_blocks");
    private static final ResourceLocation FUNCTIONAL_BLOCKS = ResourceLocation.withDefaultNamespace("functional_blocks");

    public static final ResourceLocation BOOMBOX_GUI_MODEL = Etched.etchedPath("item/boombox_gui");

    public static final ItemColor MUSIC_LABEL_DYE_COLOR = (stack, index) -> index == 0 || index == 1 ? DyedItemColor.getOrDefault(stack, -1) : -1;

    public static final ItemColor MUSIC_LABEL_COLOR = (stack, index) -> {
        MusicLabelComponent label = stack.getOrDefault(EtchedComponents.MUSIC_LABEL.get(), MusicLabelComponent.DEFAULT);
        if (index == 0) {
            return label.primaryColor();
        }
        if (index == 1) {
            return label.secondaryColor();
        }
        return -1;
    };

    public static final ItemColor BLANK_MUSIC_DISC_COLOR = (stack, index) -> index > 0 ? -1 : 0xFF000000 | DyedItemColor.getOrDefault(stack, 0x515151);

    public static final ItemColor ETCHED_MUSIC_DISC_COLOR = (stack, index) -> {
        DiscAppearanceComponent discAppearance = stack.getOrDefault(EtchedComponents.DISC_APPEARANCE.get(), DiscAppearanceComponent.DEFAULT);
        if (index == 0) {
            return discAppearance.discColor();
        }

        if (discAppearance.pattern().isColorable()) {
            if (index == 1) {
                return discAppearance.labelPrimaryColor();
            }
            if (discAppearance.pattern().isComplex() && index == 2) {
                return discAppearance.labelSecondaryColor();
            }
        }
        return -1;
    };

    private EtchedClientSetup() {
    }

    /**
     * Registers the item properties used by item model overrides.
     */
    public static void registerItemProperties() {
        ItemProperties.register(EtchedItems.BOOMBOX.get(), Etched.etchedPath("playing"), (stack, level, entity, i) -> {
            InteractionHand hand = entity != null ? BoomboxItem.getPlayingHand(entity) : null;
            return hand != null && stack == entity.getItemInHand(hand) ? 1 : 0;
        });
        ItemProperties.register(EtchedItems.ETCHED_MUSIC_DISC.get(), Etched.etchedPath("pattern"), new ClampedItemPropertyFunction() {
            @Override
            public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
                DiscAppearanceComponent discAppearance = stack.get(EtchedComponents.DISC_APPEARANCE.get());
                if (discAppearance != null) {
                    return discAppearance.pattern().ordinal();
                }
                return 0.0F;
            }

            @Override
            public float call(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
                // The pattern is a number from 0 to 5, so it must not be clamped to 1 like vanilla properties
                return this.unclampedCall(stack, level, entity, seed);
            }
        });
    }

    /**
     * Adds the items of the mod to vanilla creative tabs.
     *
     * @param tab    The tab being filled
     * @param accept Adds an item to the tab
     */
    public static void addCreativeTabItems(ResourceKey<CreativeModeTab> tab, Consumer<ItemLike> accept) {
        ResourceLocation id = tab.location();
        if (id.equals(TOOLS_AND_UTILITIES)) {
            accept.accept(EtchedItems.MUSIC_LABEL.get());
            accept.accept(EtchedItems.BLANK_MUSIC_DISC.get());
            accept.accept(EtchedItems.BOOMBOX.get());
            accept.accept(EtchedItems.ALBUM_COVER.get());
        } else if (id.equals(REDSTONE_BLOCKS)) {
            accept.accept(EtchedItems.JUKEBOX_MINECART.get());
        } else if (id.equals(FUNCTIONAL_BLOCKS)) {
            accept.accept(EtchedBlocks.ETCHING_TABLE.get());
            accept.accept(EtchedBlocks.ALBUM_JUKEBOX.get());
            accept.accept(EtchedBlocks.RADIO.get());
        }
    }

    /**
     * Registers all additional models that are baked with the other models.
     *
     * @param register Registers a model at the specified location
     */
    public static void registerAdditionalModels(Consumer<ModelResourceLocation> register) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        String folder = "models/" + AlbumCoverItemRenderer.FOLDER_NAME;

        register.accept(new ModelResourceLocation(Etched.etchedPath("item/boombox_in_hand"), "standalone"));
        register.accept(new ModelResourceLocation(BOOMBOX_GUI_MODEL, "standalone"));
        for (ResourceLocation location : resourceManager.listResources(folder, name -> name.getPath().endsWith(".json")).keySet()) {
            register.accept(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().substring(7, location.getPath().length() - 5)), "standalone"));
        }
        register.accept(AlbumCoverItemRenderer.BLANK_ALBUM_COVER);
        register.accept(AlbumCoverItemRenderer.DEFAULT_ALBUM_COVER);
    }

    /**
     * Adds the tooltip lines of the mod components to an item stack tooltip.
     *
     * @param stack   The stack to add tooltips for
     * @param context The context of the tooltip
     * @param flag    The flag of the tooltip
     * @param adder   Adds a line to the tooltip
     */
    public static void addTooltips(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, Consumer<Component> adder) {
        TooltipHelper.addToTooltip(stack, EtchedComponents.ALBUM_COVER.get(), context, adder, flag);
        TooltipHelper.addToTooltip(stack, EtchedComponents.MUSIC_LABEL.get(), context, adder, flag);
        TooltipHelper.addToTooltip(stack, EtchedComponents.PLAYING_RECORD.get(), context, adder, flag);
        PlayableRecord.addToTooltip(stack, context, adder);
        TooltipHelper.addToTooltip(stack, EtchedComponents.PAUSED.get(), context, adder, flag);
    }
}
