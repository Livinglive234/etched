package gg.moonflower.etched.client;

import gg.moonflower.etched.api.record.AlbumCover;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.api.sound.SoundPositions;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.client.sound.EntityRecordSoundInstance;
import gg.moonflower.etched.common.component.AlbumCoverComponent;
import gg.moonflower.etched.common.component.MusicTrackComponent;
import gg.moonflower.etched.core.registry.EtchedComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.CommonLevelAccessor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.List;
import java.util.function.Supplier;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Client implementation of the {@link PlayableRecord} methods that use client classes. Kept separate so the server never loads them.
 */
@ApiStatus.Internal
public final class PlayableRecordClient {

    private PlayableRecordClient() {
    }

    public static boolean canShowMessage(double x, double y, double z) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null || SoundPositions.distanceSquared(player.level(), player.getX(), player.getY(), player.getZ(), x, y, z) <= 4096.0;
    }

    /**
     * Makes the sound for one track of a record. The tracks are counted vanilla song first, then the tracks etched on the record, then the records inside an album cover.
     */
    private interface TrackSounds {

        @Nullable SoundInstance song(JukeboxSong song);

        @Nullable SoundInstance track(TrackData track);
    }

    private static Optional<SoundInstance> createSound(ItemStack stack, HolderLookup.Provider registries, int track, TrackSounds sounds) {
        if (track < 0) {
            return Optional.empty();
        }

        Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(registries, stack);
        if (optional.isPresent()) {
            if (track == 0) {
                return Optional.ofNullable(sounds.song(optional.get().value()));
            }
            track--;
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null) {
            List<TrackData> tracks = music.tracks();
            if (track < tracks.size()) {
                return Optional.ofNullable(sounds.track(tracks.get(track)));
            }
            track -= tracks.size();
        }

        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null) {
            for (ItemStack record : albumCover.getItems()) {
                Optional<SoundInstance> sound = createSound(record, registries, track, sounds);
                if (sound.isPresent()) {
                    return sound;
                }
                track -= PlayableRecord.getTrackCount(registries, record);
            }
        }

        return Optional.empty();
    }

    public static Optional<SoundInstance> createEntitySound(ItemStack stack, Entity entity, int track, int attenuationDistance) {
        return createSound(stack, entity.registryAccess(), track, new TrackSounds() {
            @Override
            public SoundInstance song(JukeboxSong song) {
                if (entity.level().getBlockState(entity.blockPosition().above()).isAir() && canShowMessage(entity.getX(), entity.getY(), entity.getZ())) {
                    Minecraft.getInstance().gui.setNowPlaying(song.description());
                }
                return new EntityRecordSoundInstance(song.soundEvent().value(), entity);
            }

            @Override
            public SoundInstance track(TrackData track) {
                return SoundTracker.getEtchedRecord(track.url(), track.getDisplayName(), entity, attenuationDistance, false);
            }
        });
    }

    public static Optional<SoundInstance> createBlockSound(ItemStack stack, CommonLevelAccessor level, BlockPos pos, int track, int attenuationDistance) {
        return createSound(stack, level.registryAccess(), track, new TrackSounds() {
            @Override
            public SoundInstance song(JukeboxSong song) {
                return SoundTracker.getEtchedRecord(song.soundEvent().value().getLocation().toString(), song.description(), level, pos, attenuationDistance, AudioSource.AudioFileType.FILE);
            }

            @Override
            public SoundInstance track(TrackData track) {
                return SoundTracker.getEtchedRecord(track.url(), track.getDisplayName(), level, pos, attenuationDistance, AudioSource.AudioFileType.FILE);
            }
        });
    }

    public static Optional<SoundInstance> createMovingSound(ItemStack stack, HolderLookup.Provider registries, Supplier<Vec3> position, int track, int attenuationDistance) {
        return createSound(stack, registries, track, new TrackSounds() {
            @Override
            public SoundInstance song(JukeboxSong song) {
                return SoundTracker.getMovingRecord(song.soundEvent().value().getLocation().toString(), song.description(), position, attenuationDistance, AudioSource.AudioFileType.FILE);
            }

            @Override
            public SoundInstance track(TrackData track) {
                return SoundTracker.getMovingRecord(track.url(), track.getDisplayName(), position, attenuationDistance, AudioSource.AudioFileType.FILE);
            }
        });
    }

    public static CompletableFuture<AlbumCover> getAlbumCover(ItemStack stack, Proxy proxy, ResourceManager resourceManager) {
        Optional<TrackData> album = PlayableRecord.getAlbum(stack);
        if (album.isPresent()) {
            return SoundSourceManager.resolveAlbumCover(album.get().url(), null, proxy, resourceManager);
        }

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (resourceManager.getResource(key.withPath("models/" + AlbumCoverItemRenderer.FOLDER_NAME + "/" + key.getPath() + ".json")).isPresent()) {
            return CompletableFuture.completedFuture(AlbumCover.of(key.withPath(AlbumCoverItemRenderer.FOLDER_NAME + "/" + key.getPath())));
        }

        return CompletableFuture.completedFuture(AlbumCover.EMPTY);
    }
}
