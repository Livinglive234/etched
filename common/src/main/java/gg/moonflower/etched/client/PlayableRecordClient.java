package gg.moonflower.etched.client;

import gg.moonflower.etched.api.record.AlbumCover;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
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
        return player == null || player.distanceToSqr(x, y, z) <= 4096.0;
    }

    public static Optional<SoundInstance> createEntitySound(ItemStack stack, Entity entity, int track) {
        return createEntitySound(stack, entity, track, 16);
    }

    public static Optional<SoundInstance> createEntitySound(ItemStack stack, Entity entity, int track, int attenuationDistance) {
        if (track < 0) {
            return Optional.empty();
        }

        Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(entity.registryAccess(), stack);
        if (optional.isPresent()) {
            if (track == 0) {
                JukeboxSong song = optional.get().value();
                if (entity.level().getBlockState(entity.blockPosition().above()).isAir() && canShowMessage(entity.getX(), entity.getY(), entity.getZ())) {
                    Minecraft.getInstance().gui.setNowPlaying(song.description());
                }
                return Optional.of(new EntityRecordSoundInstance(song.soundEvent().value(), entity));
            }
            track--;
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null) {
            List<TrackData> tracks = music.tracks();
            if (track < tracks.size()) {
                TrackData trackData = tracks.get(track);
                return Optional.ofNullable(SoundTracker.getEtchedRecord(trackData.url(), trackData.getDisplayName(), entity, attenuationDistance, false));
            }
            track -= tracks.size();
        }

        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null) {
            for (ItemStack record : albumCover.getItems()) {
                Optional<SoundInstance> entitySound = createEntitySound(record, entity, track, attenuationDistance);
                if (entitySound.isPresent()) {
                    return entitySound;
                }
                track -= PlayableRecord.getTrackCount(entity.registryAccess(), record);
            }
        }

        return Optional.empty();
    }

    public static Optional<SoundInstance> createBlockSound(ItemStack stack, CommonLevelAccessor level, BlockPos pos, int track) {
        return createBlockSound(stack, level, pos, track, 16);
    }

    public static Optional<SoundInstance> createBlockSound(ItemStack stack, CommonLevelAccessor level, BlockPos pos, int track, int attenuationDistance) {
        if (track < 0) {
            return Optional.empty();
        }

        Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(level.registryAccess(), stack);
        if (optional.isPresent()) {
            if (track == 0) {
                JukeboxSong song = optional.get().value();
                return Optional.ofNullable(SoundTracker.getEtchedRecord(song.soundEvent().value().getLocation().toString(), song.description(), level, pos, attenuationDistance, AudioSource.AudioFileType.FILE));
            }
            track--;
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null) {
            List<TrackData> tracks = music.tracks();
            if (track < tracks.size()) {
                TrackData trackData = tracks.get(track);
                String url = trackData.url();
                return Optional.ofNullable(SoundTracker.getEtchedRecord(url, trackData.getDisplayName(), level, pos, attenuationDistance, AudioSource.AudioFileType.FILE));
            }
            track -= tracks.size();
        }

        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null) {
            for (ItemStack record : albumCover.getItems()) {
                Optional<SoundInstance> entitySound = createBlockSound(record, level, pos, track, attenuationDistance);
                if (entitySound.isPresent()) {
                    return entitySound;
                }
                track -= PlayableRecord.getTrackCount(level.registryAccess(), record);
            }
        }

        return Optional.empty();
    }

    public static Optional<SoundInstance> createMovingSound(ItemStack stack, HolderLookup.Provider registries, Supplier<Vec3> position, int track, int attenuationDistance) {
        if (track < 0) {
            return Optional.empty();
        }

        Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(registries, stack);
        if (optional.isPresent()) {
            if (track == 0) {
                JukeboxSong song = optional.get().value();
                return Optional.ofNullable(SoundTracker.getMovingRecord(song.soundEvent().value().getLocation().toString(), song.description(), position, attenuationDistance, AudioSource.AudioFileType.FILE));
            }
            track--;
        }

        MusicTrackComponent music = stack.get(EtchedComponents.MUSIC.get());
        if (music != null) {
            List<TrackData> tracks = music.tracks();
            if (track < tracks.size()) {
                TrackData trackData = tracks.get(track);
                return Optional.ofNullable(SoundTracker.getMovingRecord(trackData.url(), trackData.getDisplayName(), position, attenuationDistance, AudioSource.AudioFileType.FILE));
            }
            track -= tracks.size();
        }

        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER.get());
        if (albumCover != null) {
            for (ItemStack record : albumCover.getItems()) {
                Optional<SoundInstance> sound = createMovingSound(record, registries, position, track, attenuationDistance);
                if (sound.isPresent()) {
                    return sound;
                }
                track -= PlayableRecord.getTrackCount(registries, record);
            }
        }

        return Optional.empty();
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
