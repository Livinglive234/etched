package gg.moonflower.etched.api.sound;

import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.api.util.DownloadProgressListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * @author Ocelot
 */
public class OnlineRecordSoundInstance extends AbstractOnlineSoundInstance implements TickableSoundInstance {

    private final Entity entity;
    private final Supplier<Vec3> position;
    private boolean stopped;
    private boolean anchored;
    private double anchorX;
    private double anchorY;
    private double anchorZ;

    public OnlineRecordSoundInstance(String url, Entity entity, float volume, int attenuationDistance, DownloadProgressListener progressListener, AudioSource.AudioFileType type) {
        super(url, null, attenuationDistance, SoundSource.RECORDS, progressListener, type, entity == Minecraft.getInstance().player);
        this.volume = volume;
        this.entity = entity;
        this.position = null;
    }

    /**
     * Creates a sound that follows a moving point, such as a block on a contraption.
     *
     * @param position Where the sound is right now, or <code>null</code> when it has to stop
     */
    public OnlineRecordSoundInstance(String url, Supplier<Vec3> position, float volume, int attenuationDistance, DownloadProgressListener progressListener, AudioSource.AudioFileType type) {
        super(url, null, attenuationDistance, SoundSource.RECORDS, progressListener, type, false);
        this.volume = volume;
        this.entity = null;
        this.position = position;
        Vec3 start = position.get();
        if (start != null) {
            this.x = start.x;
            this.y = start.y;
            this.z = start.z;
        }
    }

    public OnlineRecordSoundInstance(String url, Entity entity, int attenuationDistance, DownloadProgressListener progressListener, AudioSource.AudioFileType type) {
        this(url, entity, 4.0F, attenuationDistance, progressListener, type);
    }

    public OnlineRecordSoundInstance(String url, double x, double y, double z, float volume, int attenuationDistance, DownloadProgressListener progressListener, AudioSource.AudioFileType type) {
        this(url, (Entity) null, volume, attenuationDistance, progressListener, type);
        this.x = x;
        this.y = y;
        this.z = z;
        this.anchored = true;
        this.anchorX = x;
        this.anchorY = y;
        this.anchorZ = z;
        this.followAnchor();
    }

    /**
     * Puts the sound where its block really is when something else moves the block, such as a ship.
     */
    private void followAnchor() {
        Vec3 world = SoundPositions.toWorld(this.anchorX, this.anchorY, this.anchorZ);
        this.x = world != null ? world.x : this.anchorX;
        this.y = world != null ? world.y : this.anchorY;
        this.z = world != null ? world.z : this.anchorZ;
    }

    public OnlineRecordSoundInstance(String url, double x, double y, double z, int attenuationDistance, DownloadProgressListener progressListener, AudioSource.AudioFileType type) {
        this(url, x, y, z, 4.0F, attenuationDistance, progressListener, type);
    }

    @Override
    public void tick() {
        if (this.anchored) {
            this.followAnchor();
            return;
        }
        if (this.position != null) {
            Vec3 pos = this.position.get();
            if (pos == null) {
                this.stopped = true;
            } else {
                this.x = pos.x;
                this.y = pos.y;
                this.z = pos.z;
            }
            return;
        }
        if (this.entity == null) {
            return;
        }

        if (!this.entity.isAlive()) {
            this.stopped = true;
        } else {
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();
        }
    }

    @Override
    public boolean isStopped() {
        return this.stopped;
    }
}
