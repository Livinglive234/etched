package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.common.recipe.MusicDiscCloningRecipe;
import gg.moonflower.etched.common.recipe.MusicLabelDyeRecipe;
import gg.moonflower.etched.common.recipe.MusicLabelMergeRecipe;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class EtchedRecipes {

    public static final Registrar<RecipeSerializer<?>> REGISTRY = EtchedPlatform.get().registrar(BuiltInRegistries.RECIPE_SERIALIZER);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<MusicLabelMergeRecipe>> COMPLEX_MUSIC_LABEL = REGISTRY.register("merge_music_label", () -> new SimpleCraftingRecipeSerializer<>(MusicLabelMergeRecipe::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<MusicDiscCloningRecipe>> CLONE_MUSIC_DISC = REGISTRY.register("music_disc_cloning", () -> new SimpleCraftingRecipeSerializer<>(MusicDiscCloningRecipe::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<MusicLabelDyeRecipe>> DYE_MUSIC_LABEL = REGISTRY.register("dye_music_label", () -> new SimpleCraftingRecipeSerializer<>(MusicLabelDyeRecipe::new));
}