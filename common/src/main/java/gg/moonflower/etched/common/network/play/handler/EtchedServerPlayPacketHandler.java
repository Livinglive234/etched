package gg.moonflower.etched.common.network.play.handler;

import gg.moonflower.etched.common.network.play.ServerboundRadioControlPacket;
import gg.moonflower.etched.common.network.play.ServerboundSoundSettingsPacket;
import gg.moonflower.etched.common.menu.RadioMenu;
import gg.moonflower.etched.common.menu.SoundSettingsMenu;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.common.component.MusicLabelComponent;
import gg.moonflower.etched.common.menu.AlbumJukeboxMenu;
import gg.moonflower.etched.common.menu.EtchingMenu;
import gg.moonflower.etched.common.menu.UrlMenu;
import gg.moonflower.etched.common.network.play.ServerboundEditMusicLabelPacket;
import gg.moonflower.etched.common.network.play.ServerboundEtchTrackDataPacket;
import gg.moonflower.etched.common.network.play.SetAlbumJukeboxTrackPacket;
import gg.moonflower.etched.common.network.play.SetUrlPacket;
import gg.moonflower.etched.core.registry.EtchedComponents;
import gg.moonflower.etched.core.registry.EtchedItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EtchedServerPlayPacketHandler {

    public static void handleSetUrl(SetUrlPacket pkt, Player player) {
        
        if (player.containerMenu instanceof UrlMenu menu) {
            menu.setUrl(pkt.url());
        }
    }

    public static void handleSoundSettings(ServerboundSoundSettingsPacket pkt, Player player) {
        if (player.containerMenu instanceof SoundSettingsMenu menu && Float.isFinite(pkt.volume())) {
            menu.setVolume(Math.max(0.0F, Math.min(1.0F, pkt.volume())));
        }
    }

    public static void handleRadioControl(ServerboundRadioControlPacket pkt, Player player) {
        if (player.containerMenu instanceof RadioMenu menu) {
            menu.setStopped(pkt.stopped());
        }
    }

    public static void handleEtchTrackData(ServerboundEtchTrackDataPacket pkt, Player player) {
        if (player.containerMenu instanceof EtchingMenu menu) {
            menu.setClientTrackData(pkt.url(), pkt.tracks(), pkt.error());
        }
    }

    public static void handleEditMusicLabel(ServerboundEditMusicLabelPacket pkt, Player player) {
        int slot = pkt.slot();
        if (!Inventory.isHotbarSlot(slot) && slot != 40) {
            return;
        }

        
        ItemStack labelStack = player.getInventory().getItem(slot);
        if (!labelStack.is(EtchedItems.MUSIC_LABEL.get())) {
            return;
        }

        labelStack.update(EtchedComponents.MUSIC_LABEL.get(),
                MusicLabelComponent.DEFAULT,
                label -> label.withInfo(StringUtils.normalizeSpace(pkt.artist()), StringUtils.normalizeSpace(pkt.title())));
    }

    public static void handleSetAlbumJukeboxTrack(SetAlbumJukeboxTrackPacket pkt, Player sender) {
        if (!(sender instanceof ServerPlayer player)) {
            return;
        }

        if (player.containerMenu instanceof AlbumJukeboxMenu menu) {
            ServerLevel level = player.serverLevel();
            if (menu.setPlayingTrack(level, pkt)) {
                EtchedPlatform.get().sendToPlayersTrackingChunk(level, new ChunkPos(menu.getPos()), new SetAlbumJukeboxTrackPacket(pkt.playingIndex(), pkt.track()));
            }
        }
    }
}
