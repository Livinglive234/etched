package gg.moonflower.etched.core.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import gg.moonflower.etched.common.component.DiscAppearanceComponent;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.core.registry.EtchedItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Generates the item models of the mod. Mirrors the models generated on NeoForge.
 */
public class EtchedFabricItemModelProvider implements DataProvider {

    private final PackOutput.PathProvider modelPathProvider;

    public EtchedFabricItemModelProvider(FabricDataOutput output) {
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    private static ResourceLocation itemId(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

    private static JsonObject model(String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        return json;
    }

    private static JsonObject generated(ResourceLocation... layers) {
        JsonObject json = model("minecraft:item/generated");
        JsonObject textures = new JsonObject();
        for (int i = 0; i < layers.length; i++) {
            textures.addProperty("layer" + i, layers[i].toString());
        }
        json.add("textures", textures);
        return json;
    }

    private static JsonObject basicItem(ItemLike item) {
        ResourceLocation id = itemId(item);
        return generated(id.withPrefix("item/"));
    }

    private static JsonObject override(JsonObject json, ResourceLocation model, ResourceLocation predicate, float value) {
        JsonArray overrides = json.has("overrides") ? json.getAsJsonArray("overrides") : new JsonArray();
        JsonObject override = new JsonObject();
        override.addProperty("model", model.toString());
        JsonObject predicates = new JsonObject();
        predicates.addProperty(predicate.toString(), value);
        override.add("predicate", predicates);
        overrides.add(override);
        json.add("overrides", overrides);
        return json;
    }

    private static ResourceLocation texturePath(ResourceLocation texture) {
        // Strips the textures/ folder and the .png extension
        return texture.withPath(texture.getPath().substring(9, texture.getPath().length() - 4));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<ResourceLocation, JsonObject> models = new java.util.LinkedHashMap<>();

        models.put(itemId(EtchedItems.MUSIC_LABEL.get()).withPrefix("item/"), generated(Etched.etchedPath("item/music_label_top"), Etched.etchedPath("item/music_label_bottom")));
        models.put(itemId(EtchedItems.BLANK_MUSIC_DISC.get()).withPrefix("item/"), basicItem(EtchedItems.BLANK_MUSIC_DISC.get()));

        ResourceLocation etchedMusicDisc = itemId(EtchedItems.ETCHED_MUSIC_DISC.get()).withPrefix("item/");
        JsonObject etchedMusicDiscModel = new JsonObject();
        for (DiscAppearanceComponent.LabelPattern value : DiscAppearanceComponent.LabelPattern.values()) {
            Pair<ResourceLocation, ResourceLocation> textures = value.getTextures();
            ResourceLocation patternId = Etched.etchedPath("item/etched_music_disc/" + value.name().toLowerCase(Locale.ROOT));

            List<ResourceLocation> layers = new ArrayList<>();
            layers.add(etchedMusicDisc);
            layers.add(texturePath(textures.getFirst()));
            if (value.isComplex()) {
                layers.add(texturePath(textures.getSecond()));
            }
            models.put(patternId, generated(layers.toArray(ResourceLocation[]::new)));

            if (value.ordinal() == 0) {
                etchedMusicDiscModel.addProperty("parent", patternId.toString());
            }
            override(etchedMusicDiscModel, patternId, Etched.etchedPath("pattern"), value.ordinal());
        }
        models.put(etchedMusicDisc, etchedMusicDiscModel);

        models.put(Etched.etchedPath("item/boombox_gui"), generated(Etched.etchedPath("item/boombox")));
        models.put(itemId(EtchedItems.JUKEBOX_MINECART.get()).withPrefix("item/"), basicItem(EtchedItems.JUKEBOX_MINECART.get()));

        // Album Cover
        JsonObject albumCover = model("minecraft:builtin/entity");
        albumCover.addProperty("gui_light", "front");
        models.put(itemId(EtchedItems.ALBUM_COVER.get()).withPrefix("item/"), albumCover);
        models.put(Etched.etchedPath("item/etched_album_cover/blank"), generated(Etched.etchedPath("item/blank_album_cover")));
        models.put(Etched.etchedPath("item/etched_album_cover/default"), generated(Etched.etchedPath("item/default_album_cover")));
        // Vanilla Album Covers
        for (Map.Entry<net.minecraft.resources.ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Item item = entry.getValue();
            if (item.components().has(DataComponents.JUKEBOX_PLAYABLE)) {
                models.put(entry.getKey().location().withPath("item/etched_album_cover/" + entry.getKey().location().getPath()), generated(Etched.etchedPath("item/vanilla_album_cover")));
            }
        }

        models.put(Etched.etchedPath("item/boombox_playing"), model("etched:item/boombox_in_hand_playing"));
        models.put(itemId(EtchedItems.BOOMBOX.get()).withPrefix("item/"), override(model("etched:item/boombox_in_hand"), Etched.etchedPath("item/boombox_playing"), Etched.etchedPath("playing"), 1.0F));

        models.put(itemId(EtchedBlocks.ETCHING_TABLE.get().asItem()).withPrefix("item/"), model("etched:block/etching_table"));
        models.put(itemId(EtchedBlocks.ALBUM_JUKEBOX.get().asItem()).withPrefix("item/"), model("etched:block/album_jukebox"));
        models.put(itemId(EtchedBlocks.RADIO.get().asItem()).withPrefix("item/"), basicItem(EtchedBlocks.RADIO.get().asItem()));
        models.put(itemId(EtchedBlocks.PORTAL_RADIO_ITEM.get()).withPrefix("item/"), basicItem(EtchedBlocks.PORTAL_RADIO_ITEM.get()));

        List<CompletableFuture<?>> futures = new ArrayList<>();
        models.forEach((id, json) -> futures.add(DataProvider.saveStable(output, json, this.modelPathProvider.json(id))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Item Models: " + Etched.MOD_ID;
    }
}
