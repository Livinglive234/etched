package gg.moonflower.etched.core.registry;

import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.common.component.*;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.platform.Registrar;
import gg.moonflower.etched.platform.RegistryObject;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

public class EtchedComponents {

    public static final Registrar<DataComponentType<?>> REGISTRY = EtchedPlatform.get().registrar(BuiltInRegistries.DATA_COMPONENT_TYPE);
    public static final RegistryObject<DataComponentType<TrackData>> ALBUM = register(
            "album", builder -> builder
                    .persistent(TrackData.CODEC)
                    .networkSynchronized(TrackData.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<AlbumCoverComponent>> ALBUM_COVER = register(
            "album_cover", builder -> builder
                    .persistent(AlbumCoverComponent.CODEC)
                    .networkSynchronized(AlbumCoverComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<DiscAppearanceComponent>> DISC_APPEARANCE = register(
            "disc_appearance", builder -> builder
                    .persistent(DiscAppearanceComponent.CODEC)
                    .networkSynchronized(DiscAppearanceComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<MusicTrackComponent>> MUSIC = register(
            "music", builder -> builder
                    .persistent(MusicTrackComponent.CODEC)
                    .networkSynchronized(MusicTrackComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<MusicLabelComponent>> MUSIC_LABEL = register(
            "music_label", builder -> builder
                    .persistent(MusicLabelComponent.CODEC)
                    .networkSynchronized(MusicLabelComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<PausedComponent>> PAUSED = register(
            "paused", builder -> builder
                    .persistent(PausedComponent.CODEC)
                    .networkSynchronized(PausedComponent.STREAM_CODEC)
                    .cacheEncoding());
    public static final RegistryObject<DataComponentType<PlayingRecordComponent>> PLAYING_RECORD = register(
            "playing_record", builder -> builder
                    .persistent(PlayingRecordComponent.CODEC)
                    .networkSynchronized(PlayingRecordComponent.STREAM_CODEC)
                    .cacheEncoding());

    private static <T> RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return REGISTRY.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
