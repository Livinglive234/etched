package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The client side of the Create support, which starts and stops the sounds of mounted radios and jukeboxes.
 */
final class CreateCompatClient {

    private static final Component RADIO = Component.translatable("sound_source." + Etched.MOD_ID + ".radio");
    private static final int ALBUM_JUKEBOX_SIZE = 9;
    private static final int RADIO_RANGE = 8;
    private static final int JUKEBOX_RANGE = 16;

    private CreateCompatClient() {
    }

    static void tickRadio(MovementContext context) {
        String url = context.disabled ? null : CreateCompat.getPlayingUrl(context.state, context.blockEntityData);

        PlayingRadio playing = context.temporaryData instanceof PlayingRadio radio ? radio : null;
        if (playing != null ? Objects.equals(playing.url, url) : url == null) {
            if (playing != null) {
                SoundTracker.applyVolume(playing.sound, CreateCompat.getVolume(context.blockEntityData, "Volume"));
                spawnNotes(context, playing.sound, 0.2);
            }
            return;
        }

        if (playing != null && playing.sound != null) {
            Minecraft.getInstance().getSoundManager().stop(playing.sound);
        }

        PlayingRadio next = new PlayingRadio(url);
        context.temporaryData = next;
        if (url == null) {
            return;
        }

        // The sound stops itself once the contraption is gone or the radio was retuned
        AbstractOnlineSoundInstance sound = SoundTracker.getMovingRecord(url, RADIO, () -> getPosition(context, next), RADIO_RANGE, AudioSource.AudioFileType.BOTH);
        if (sound != null) {
            sound.setLoop(true);
            SoundTracker.applyVolume(sound, CreateCompat.getVolume(context.blockEntityData, "Volume"));
            next.sound = sound;
            Minecraft.getInstance().getSoundManager().play(sound);
        }
    }

    static void tickJukebox(MovementContext context, boolean album) {
        CompoundTag data = context.blockEntityData;
        boolean powered = context.state.hasProperty(BlockStateProperties.POWERED) && context.state.getValue(BlockStateProperties.POWERED);
        Tag discs = context.disabled || powered || data == null ? null : data.get(album ? "Items" : "RecordItem");

        RecordPlayer player = context.temporaryData instanceof RecordPlayer recordPlayer ? recordPlayer : null;
        if (player == null) {
            if (discs == null) {
                return;
            }
            player = new RecordPlayer(context, album);
            context.temporaryData = player;
        }

        if (!Objects.equals(player.discsTag, discs)) {
            player.setDiscs(discs != null ? discs.copy() : null);
        }
        SoundTracker.applyVolume(player.sound, CreateCompat.getVolume(data, album ? "Volume" : "EtchedVolume"));
        spawnNotes(context, player.sound, 0.7);
    }

    private static @Nullable Vec3 getPosition(MovementContext context, Object owner) {
        // Read as an object because the contraption entity needs libraries that are not on the compile path
        Object holder = context.contraption.entity;
        return holder instanceof Entity entity && entity.isAlive() && context.temporaryData == owner ? context.position : null;
    }

    private static void spawnNotes(MovementContext context, @Nullable SoundInstance sound, double height) {
        if (sound != null && context.position != null && context.world.random.nextInt(10) == 0 && Minecraft.getInstance().getSoundManager().isActive(sound)) {
            context.world.addParticle(ParticleTypes.NOTE, context.position.x, context.position.y + height, context.position.z, context.world.random.nextInt(25) / 24D, 0, 0);
        }
    }

    private static class PlayingRadio {

        private final String url;
        private SoundInstance sound;

        private PlayingRadio(@Nullable String url) {
            this.url = url;
        }
    }

    /**
     * Plays the discs of a mounted jukebox track by track, the same way placed jukeboxes do.
     */
    private static class RecordPlayer {

        private final MovementContext context;
        private final boolean loop;
        private Tag discsTag;
        private List<ItemStack> discs;
        private int index;
        private int track;
        private SoundInstance sound;

        private RecordPlayer(MovementContext context, boolean album) {
            this.context = context;
            this.loop = album;
            this.discs = List.of();
        }

        /**
         * Changes the discs, which keeps the current track playing if its disc wasn't touched.
         */
        private void setDiscs(@Nullable Tag discsTag) {
            HolderLookup.Provider registries = this.context.world.registryAccess();
            List<ItemStack> discs;
            if (discsTag == null) {
                discs = List.of();
            } else if (this.loop) {
                NonNullList<ItemStack> items = NonNullList.withSize(ALBUM_JUKEBOX_SIZE, ItemStack.EMPTY);
                CompoundTag container = new CompoundTag();
                container.put("Items", discsTag);
                ContainerHelper.loadAllItems(container, items, registries);
                discs = items;
            } else {
                discs = discsTag instanceof CompoundTag tag ? List.of(ItemStack.parseOptional(registries, tag)) : List.of();
            }

            boolean keepPlaying = this.sound != null && this.index < discs.size() && this.index < this.discs.size() && ItemStack.matches(this.discs.get(this.index), discs.get(this.index));
            this.discsTag = discsTag;
            this.discs = discs;
            if (!keepPlaying) {
                this.stop();
                this.index = 0;
                this.track = 0;
                this.play();
            }
        }

        private @Nullable Vec3 getPosition() {
            return CreateCompatClient.getPosition(this.context, this);
        }

        private void play() {
            HolderLookup.Provider registries = this.context.world.registryAccess();
            int attempts = this.discs.size();
            for (ItemStack disc : this.discs) {
                attempts += PlayableRecord.getTrackCount(registries, disc);
            }

            // Skip empty slots and tracks that can't be played, giving up once everything was tried
            for (int i = 0; i <= attempts; i++) {
                if (this.index >= this.discs.size()) {
                    if (!this.loop || this.discs.isEmpty()) {
                        break;
                    }
                    this.index = 0;
                }

                ItemStack disc = this.discs.get(this.index);
                if (!PlayableRecord.isPlayableRecord(disc) || this.track >= PlayableRecord.getTrackCount(registries, disc)) {
                    this.index++;
                    this.track = 0;
                    continue;
                }

                Optional<SoundInstance> sound = PlayableRecord.createMovingSound(disc, registries, this::getPosition, this.track, JUKEBOX_RANGE);
                if (sound.isPresent()) {
                    this.sound = StopListeningSound.create(sound.get(), () -> Minecraft.getInstance().tell(this::onFinished));
                    Minecraft.getInstance().getSoundManager().play(this.sound);
                    return;
                }
                this.track++;
            }
            this.sound = null;
        }

        private void onFinished() {
            if (this.getPosition() == null) {
                return;
            }
            this.track++;
            this.play();
        }

        private void stop() {
            if (this.sound != null) {
                if (this.sound instanceof StopListeningSound listening) {
                    listening.stopListening();
                }
                Minecraft.getInstance().getSoundManager().stop(this.sound);
                this.sound = null;
            }
        }
    }
}
