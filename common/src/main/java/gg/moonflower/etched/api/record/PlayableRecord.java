package gg.moonflower.etched.api.record;

import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import gg.moonflower.etched.client.PlayableRecordClient;
import gg.moonflower.etched.common.component.AlbumCoverComponent;
import gg.moonflower.etched.common.component.MusicTrackComponent;
import gg.moonflower.etched.common.network.play.ClientboundPlayEntityMusicPacket;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.extension.JukeboxSongExt;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.CommonLevelAccessor;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Denotes an item as having the capability of being played as a record item.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public final class PlayableRecord {

    private static final Component ALBUM = Component.translatable("item." + Etched.MOD_ID + ".etched_music_disc.album").withStyle(ChatFormatting.DARK_GRAY);

    private PlayableRecord() {
    }

    /**
     * Checks to see if the specified stack can be played.
     *
     * @param stack The stack to check
     * @return Whether that stack can play
     */
    public static boolean isPlayableRecord(ItemStack stack) {
        return stack.has(EtchedComponents.ALBUM_COVER.get()) || stack.has(EtchedComponents.MUSIC.get()) || stack.has(DataComponents.JUKEBOX_PLAYABLE);
    }

    /**
     * Checks to see if the local player is close enough to receive the record text.
     *
     * @param x The x position of the entity
     * @param y The y position of the entity
     * @param z The z position of the entity
     * @return Whether the player is within distance
     */
    public static boolean canShowMessage(double x, double y, double z) {
        return PlayableRecordClient.canShowMessage(x, y, z);
    }

    /**
     * Sends a packet to the client notifying them to begin playing an entity record.
     *
     * @param entity  The entity playing the record
     * @param record  The record to play
     * @param restart Whether to restart the track from the beginning or start a new playback
     */
    public static void playEntityRecord(Entity entity, ItemStack record, boolean restart) {
        EtchedPlatform.get().sendToPlayersTrackingEntityAndSelf(entity, new ClientboundPlayEntityMusicPacket(record.copy(), entity, restart, null));
    }

    /**
     * Sends a packet to the client notifying them to stop playing an entity record.
     *
     * @param entity The entity to stop playing records
     */
    public static void stopEntityRecord(Entity entity) {
        EtchedPlatform.get().sendToPlayersTrackingEntityAndSelf(entity, new ClientboundPlayEntityMusicPacket(entity));
    }

    /**
     * Retrieves the music for the specified stack.
     *
     * @param registries The registry instance to get data from
     * @param stack      The stack to check
     * @return The tracks on that record
     */
    public static List<TrackData> getTracks(HolderLookup.Provider registries, ItemStack stack) {
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }

        List<TrackData> tracks = new ArrayList<>();
        Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(registries, stack);
        if (optional.isPresent()) {
            JukeboxSongExt song = (JukeboxSongExt) (Object) optional.get().value();
            tracks.addAll(song.veil$tracks());
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null) {
            tracks.addAll(music.tracks());
        }

        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null) {
            for (ItemStack record : albumCover.getItems()) {
                tracks.addAll(getTracks(registries, record));
            }
        }
        return tracks;
    }

    /**
     * Retrieves the number of tracks on the specified stack.
     *
     * @param registries The registry instance to get data from
     * @param stack      The stack to check
     * @return The number of tracks on the record
     */
    public static int getTrackCount(HolderLookup.Provider registries, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        int tracks = 0;
        Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(registries, stack);
        if (optional.isPresent()) {
            JukeboxSongExt song = (JukeboxSongExt) (Object) optional.get().value();
            tracks += song.veil$tracks().size();
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null) {
            tracks += music.tracks().size();
        }

        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null) {
            for (ItemStack record : albumCover.getItems()) {
                tracks += getTrackCount(registries, record);
            }
        }
        return tracks;
    }

    /**
     * Retrieves the album music for the specified stack.
     *
     * @param stack The stack to check
     * @return The album track on that record
     */
    public static Optional<TrackData> getAlbum(ItemStack stack) {
        TrackData album = stack.get(EtchedComponents.ALBUM.get());
        if (album != null) {
            return Optional.of(album);
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null && !music.tracks().isEmpty()) {
            return Optional.of(music.tracks().getFirst());
        }

        return Optional.empty();
    }

    /**
     * Creates the sound for an entity with the default attenuation distance.
     *
     * @param stack  The stack to play
     * @param entity The entity to play the sound for
     * @param track  The track to play on the disc
     * @return The sound to play or nothing to error
     */
    public static Optional<SoundInstance> createEntitySound(ItemStack stack, Entity entity, int track) {
        return PlayableRecordClient.createEntitySound(stack, entity, track);
    }

    /**
     * Creates the sound for an entity.
     *
     * @param stack               The stack to play
     * @param entity              The entity to play the sound for
     * @param track               The track to play on the disc
     * @param attenuationDistance The attenuation distance of the sound
     * @return The sound to play or nothing to error
     */
    public static Optional<SoundInstance> createEntitySound(ItemStack stack, Entity entity, int track, int attenuationDistance) {
        return PlayableRecordClient.createEntitySound(stack, entity, track, attenuationDistance);
    }

    /**
     * Creates the sound for a block with the default attenuation distance.
     *
     * @param stack The stack to play
     * @param level The level to play the sound in
     * @param pos   The position of the sound
     * @param track The track to play on the disc
     * @return The sound to play or nothing to error
     */
    public static Optional<SoundInstance> createBlockSound(ItemStack stack, CommonLevelAccessor level, BlockPos pos, int track) {
        return PlayableRecordClient.createBlockSound(stack, level, pos, track);
    }

    /**
     * Creates the sound for a block.
     *
     * @param stack               The stack to play
     * @param level               The level to play the sound in
     * @param pos                 The position of the sound
     * @param track               The track to play on the disc
     * @param attenuationDistance The attenuation distance of the sound
     * @return The sound to play or nothing to error
     */
    public static Optional<SoundInstance> createBlockSound(ItemStack stack, CommonLevelAccessor level, BlockPos pos, int track, int attenuationDistance) {
        return PlayableRecordClient.createBlockSound(stack, level, pos, track, attenuationDistance);
    }

    /**
     * Retrieves the album cover for this item.
     *
     * @param stack The stack to get art for
     * @return A future for a potential cover
     */
    public static CompletableFuture<AlbumCover> getAlbumCover(ItemStack stack, Proxy proxy, ResourceManager resourceManager) {
        return PlayableRecordClient.getAlbumCover(stack, proxy, resourceManager);
    }

    /**
     * Adds album information to the item tooltip.
     *
     * @param stack   The stack to get the album from
     * @param context The context for adding tooltip lines
     * @param adder   The consumer for tooltips
     */
    public static void addToTooltip(ItemStack stack, Item.TooltipContext context, Consumer<Component> adder) {
        getAlbum(stack).ifPresent(track -> {
            boolean album = getTrackCount(context.registries(), stack) > 1;
            adder.accept(track.getDisplayName().copy().withStyle(ChatFormatting.GRAY));
            SoundSourceManager.getBrandText(track.url())
                    .map(component -> {
                        if (album) {
                            return Component.literal("  ").append(component).append(" ").append(ALBUM);
                        } else {
                            return Component.literal("  ").append(component);
                        }
                    })
                    .ifPresentOrElse(adder, () -> {
                        if (album) {
                            adder.accept(ALBUM);
                        }
                    });
        });
    }
}
