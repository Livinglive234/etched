package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.menu.AlbumJukeboxMenu;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

/**
 * Opens the album jukebox menu for a jukebox on a contraption, so its discs and volume can be changed while it moves.
 * <p>Opening the menu of a mounted block follows BazZziliuS' NeoForge version.
 */
public class AlbumJukeboxMovingInteraction extends MovingInteractionBehaviour {

    private static final Component CONTAINER_TITLE = Component.translatable("container." + Etched.MOD_ID + ".album_jukebox");

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return true;
        }

        CompoundTag data = CreateCompat.getBlockData(contraptionEntity, localPos);
        if (data == null && ContraptionAccess.contraption(contraptionEntity) == null) {
            return false;
        }

        float volume = CreateCompat.getVolume(data, "Volume");
        EtchedPlatform.get().openMenu(serverPlayer, new SimpleMenuProvider((menuId, inventory, menuPlayer) ->
                new AlbumJukeboxMenu(menuId, inventory, new ContraptionAlbumJukeboxContainer(contraptionEntity, localPos), localPos, volume, newVolume -> {
                    CompoundTag change = new CompoundTag();
                    change.putFloat("Volume", newVolume);
                    CreateCompat.updateBlockData(contraptionEntity, localPos, change);
                }), CONTAINER_TITLE), buf -> {
            buf.writeBlockPos(localPos);
            buf.writeBoolean(true);
            buf.writeFloat(volume);
        });
        return true;
    }
}
