package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.nbt.CompoundTag;

/**
 * Plays the records inside a jukebox on clients while it is moving with a contraption.
 */
public class JukeboxMovementBehaviour implements MovementBehaviour {

    private final boolean album;

    /**
     * @param album Whether this is an album jukebox that cycles through all of its discs
     */
    public JukeboxMovementBehaviour(boolean album) {
        this.album = album;
    }

    @Override
    public boolean mustTickWhileDisabled() {
        return true; // The music has to stop when contraption controls turn jukeboxes off
    }

    @Override
    public void startMoving(MovementContext context) {
        // Vanilla jukeboxes don't send their disc or volume to clients, so add them to the data Create syncs
        if (this.album || context.world.isClientSide() || context.blockEntityData == null) {
            return;
        }

        CompoundTag updateTag = CreateCompat.getUpdateTag(context.contraption, context.localPos);
        if (updateTag == null) {
            return;
        }
        if (context.blockEntityData.contains("RecordItem")) {
            updateTag.put("RecordItem", context.blockEntityData.getCompound("RecordItem").copy());
        }
        if (context.blockEntityData.contains("EtchedVolume")) {
            updateTag.putFloat("EtchedVolume", context.blockEntityData.getFloat("EtchedVolume"));
        }
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide()) {
            CreateCompatClient.tickJukebox(context, this.album);
        }
    }
}
