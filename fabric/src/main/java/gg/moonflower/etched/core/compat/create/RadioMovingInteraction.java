package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.menu.RadioMenu;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

/**
 * Opens the radio menu for a radio on a contraption, so its station and volume can be changed while it moves.
 * <p>Opening the menu of a mounted block follows BazZziliuS' NeoForge version.
 */
public class RadioMovingInteraction extends MovingInteractionBehaviour {

    private static final Component CONTAINER_TITLE = Component.translatable("container." + Etched.MOD_ID + ".radio");

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return true;
        }

        Object entity = contraptionEntity;
        CompoundTag data = CreateCompat.getBlockData(entity, localPos);
        if (data == null) {
            return false;
        }

        String url = data.getString("Url");
        float volume = CreateCompat.getVolume(data, "Volume");
        RadioMenu.Controls controls = new RadioMenu.Controls() {
            @Override
            public boolean stillValid(Player user) {
                return CreateCompat.canUse(entity, localPos, EtchedBlocks.RADIO.get(), user);
            }

            @Override
            public void setUrl(String newUrl) {
                CompoundTag change = new CompoundTag();
                change.putString("Url", newUrl);
                change.putBoolean("Stopped", false); // A new station starts playing
                CreateCompat.updateBlockData(entity, localPos, change);
            }

            @Override
            public void setStopped(boolean stopped) {
                CompoundTag change = new CompoundTag();
                change.putBoolean("Stopped", stopped);
                CreateCompat.updateBlockData(entity, localPos, change);
            }

            @Override
            public void setVolume(float newVolume) {
                CompoundTag change = new CompoundTag();
                change.putFloat("Volume", Math.max(0.0F, Math.min(1.0F, newVolume)));
                CreateCompat.updateBlockData(entity, localPos, change);
            }
        };

        EtchedPlatform.get().openMenu(serverPlayer, new SimpleMenuProvider((menuId, inventory, menuPlayer) -> new RadioMenu(menuId, controls), CONTAINER_TITLE), buf -> {
            buf.writeBlockPos(localPos);
            buf.writeBoolean(true);
            buf.writeUtf(url);
            buf.writeFloat(volume);
        });
        return true;
    }
}
