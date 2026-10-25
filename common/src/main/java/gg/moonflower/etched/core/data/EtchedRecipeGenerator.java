package gg.moonflower.etched.core.data;

import gg.moonflower.etched.common.recipe.MusicDiscCloningRecipe;
import gg.moonflower.etched.common.recipe.MusicLabelDyeRecipe;
import gg.moonflower.etched.common.recipe.MusicLabelMergeRecipe;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedItems;
import gg.moonflower.etched.core.registry.EtchedTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

/**
 * Defines the recipes of the mod for every mod loader.
 */
public final class EtchedRecipeGenerator {

    private static final TagKey<Item> PAPER = conventional("paper");
    private static final TagKey<Item> COPPER_INGOTS = conventional("ingots/copper");
    private static final TagKey<Item> IRON_INGOTS = conventional("ingots/iron");
    private static final TagKey<Item> REDSTONE_DUSTS = conventional("dusts/redstone");
    private static final TagKey<Item> DIAMOND_GEMS = conventional("gems/diamond");
    private static final TagKey<Item> WOODEN_CHESTS = conventional("chests/wooden");

    private EtchedRecipeGenerator() {
    }

    private static TagKey<Item> conventional(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item));
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag));
    }

    public static void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtchedItems.MUSIC_LABEL.get())
                .define('P', PAPER)
                .pattern(" P ")
                .pattern("P P")
                .pattern(" P ")
                .unlockedBy("has_paper", has(PAPER))
                .save(recipeOutput);
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(EtchedTags.MUSIC_DISCS),
                        RecipeCategory.TOOLS,
                        EtchedItems.BLANK_MUSIC_DISC.get(),
                        0.2F,
                        200)
                .unlockedBy("has_music_disc", has(EtchedTags.MUSIC_DISCS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, EtchedItems.JUKEBOX_MINECART.get())
                .requires(Blocks.JUKEBOX)
                .requires(Items.MINECART)
                .unlockedBy("has_minecart", has(Items.MINECART))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtchedItems.BOOMBOX.get())
                .define('I', COPPER_INGOTS)
                .define('R', REDSTONE_DUSTS)
                .define('J', Blocks.JUKEBOX)
                .pattern(" I ")
                .pattern("IRI")
                .pattern("IJI")
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .unlockedBy("has_music_disc", has(EtchedTags.MUSIC_DISCS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtchedItems.ALBUM_COVER.get())
                .define('P', PAPER)
                .define('M', EtchedItems.MUSIC_LABEL.get())
                .pattern("PPP")
                .pattern("PMP")
                .pattern("PPP")
                .unlockedBy("has_etched_music_disc", has(EtchedItems.ETCHED_MUSIC_DISC.get()))
                .unlockedBy("has_music_label", has(EtchedItems.MUSIC_LABEL.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EtchedBlocks.ETCHING_TABLE.get())
                .define('D', DIAMOND_GEMS)
                .define('I', IRON_INGOTS)
                .define('P', ItemTags.PLANKS)
                .pattern(" DI")
                .pattern("PPP")
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .unlockedBy("has_blank_music_disc", has(EtchedItems.BLANK_MUSIC_DISC.get()))
                .unlockedBy("has_music_label", has(EtchedItems.MUSIC_LABEL.get()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EtchedBlocks.ALBUM_JUKEBOX.get())
                .define('R', REDSTONE_DUSTS)
                .define('H', Items.REPEATER)
                .define('J', Blocks.JUKEBOX)
                .define('C', WOODEN_CHESTS)
                .pattern("RHR")
                .pattern("RJR")
                .pattern("RCR")
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EtchedBlocks.RADIO.get())
                .define('R', REDSTONE_DUSTS)
                .define('C', Items.COPPER_INGOT)
                .define('I', IRON_INGOTS)
                .define('N', Blocks.NOTE_BLOCK)
                .define('P', ItemTags.PLANKS)
                .pattern("RCR")
                .pattern("INI")
                .pattern("PPP")
                .unlockedBy("has_note_block", has(Blocks.NOTE_BLOCK))
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .unlockedBy("has_music_disc", has(EtchedTags.MUSIC_DISCS))
                .save(recipeOutput);

        SpecialRecipeBuilder.special(MusicLabelMergeRecipe::new).save(recipeOutput, Etched.etchedPath("merge_music_label").toString());
        SpecialRecipeBuilder.special(MusicDiscCloningRecipe::new).save(recipeOutput, Etched.etchedPath("music_disc_cloning").toString());
        SpecialRecipeBuilder.special(MusicLabelDyeRecipe::new).save(recipeOutput, Etched.etchedPath("dye_music_label").toString());
    }
}
