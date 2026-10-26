package gg.moonflower.etched.common.network;

import gg.moonflower.etched.common.network.play.*;
import gg.moonflower.etched.common.network.play.handler.EtchedClientPlayPacketHandler;
import gg.moonflower.etched.common.network.play.handler.EtchedServerPlayPacketHandler;
import gg.moonflower.etched.platform.PacketRegistry;

public class EtchedMessages {

    /**
     * The network protocol version of the Etched payloads. Raised when the radio menu started to send the volume.
     */
    public static final String VERSION = "8";

    public static void init(PacketRegistry registry) {
        // Client
        registry.playToClient(ClientboundInvalidEtchUrlPacket.TYPE, ClientboundInvalidEtchUrlPacket.CODEC, () -> EtchedClientPlayPacketHandler::handleSetInvalidEtch);
        registry.playToClient(ClientboundPlayBlockMusicPacket.TYPE, ClientboundPlayBlockMusicPacket.CODEC, () -> EtchedClientPlayPacketHandler::handlePlayBlockMusicPacket);
        registry.playToClient(ClientboundPlayEntityMusicPacket.TYPE, ClientboundPlayEntityMusicPacket.CODEC, () -> EtchedClientPlayPacketHandler::handlePlayEntityMusicPacket);

        // Server
        registry.playToServer(ServerboundEditMusicLabelPacket.TYPE, ServerboundEditMusicLabelPacket.CODEC, EtchedServerPlayPacketHandler::handleEditMusicLabel);
        // Optional so clients without it can still join. The server resolves the tracks itself for them
        registry.playToServerOptional(ServerboundEtchTrackDataPacket.TYPE, ServerboundEtchTrackDataPacket.CODEC, EtchedServerPlayPacketHandler::handleEtchTrackData);

        registry.playToServer(ServerboundRadioControlPacket.TYPE, ServerboundRadioControlPacket.CODEC, EtchedServerPlayPacketHandler::handleRadioControl);
        registry.playToServer(ServerboundSoundSettingsPacket.TYPE, ServerboundSoundSettingsPacket.CODEC, EtchedServerPlayPacketHandler::handleSoundSettings);

        // Bidirectional
        registry.playBidirectional(SetAlbumJukeboxTrackPacket.TYPE, SetAlbumJukeboxTrackPacket.CODEC, () -> EtchedClientPlayPacketHandler::handleSetAlbumJukeboxTrack, EtchedServerPlayPacketHandler::handleSetAlbumJukeboxTrack);
        registry.playBidirectional(SetUrlPacket.TYPE, SetUrlPacket.CODEC, () -> EtchedClientPlayPacketHandler::handleSetUrl, EtchedServerPlayPacketHandler::handleSetUrl);
    }
}
