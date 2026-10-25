package gg.moonflower.etched.core;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Backs the common config suppliers with NeoForge config files.
 */
public class EtchedNeoForgeConfig {

    public static void register(ModContainer container) {
        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
        clientBuilder.push("Game Feel");
        ModConfigSpec.BooleanValue forceStereo = clientBuilder.comment("Always plays tracks in stereo even when in-world").define("Force Stereo", false);
        ModConfigSpec.BooleanValue smoothParrotAnimation = clientBuilder.comment("Makes the parrot dance animation smoother").define("Smooth Parrot Animation", true);
        clientBuilder.pop();
        clientBuilder.push("Network");
        ModConfigSpec.BooleanValue allowPrivateNetwork = clientBuilder.comment("Allows radios, tracks and covers to connect to addresses on private networks and this computer. Only enable this with servers you trust, since they could make your game send requests to other devices on your network").define("Allow Private Network Stations", false);
        clientBuilder.pop();
        Etched.CLIENT_CONFIG.allowPrivateNetwork = allowPrivateNetwork::get;
        Etched.CLIENT_CONFIG.forceStereo = forceStereo::get;
        Etched.CLIENT_CONFIG.smoothParrotAnimation = smoothParrotAnimation::get;

        ModConfigSpec.Builder serverBuilder = new ModConfigSpec.Builder();
        serverBuilder.push("Boombox");
        ModConfigSpec.BooleanValue useBoomboxMenu = serverBuilder.comment("Disables right clicking music discs into boomboxes and allows the menu to be used by shift right-clicking").define("Use boombox menu", false);
        serverBuilder.pop();
        serverBuilder.push("Album Cover");
        ModConfigSpec.BooleanValue useAlbumCoverMenu = serverBuilder.comment("Disables right clicking music discs into album covers and allows the menu to be used by shift right-clicking").define("Use album cover menu", false);
        serverBuilder.pop();
        Etched.SERVER_CONFIG.useBoomboxMenu = useBoomboxMenu::get;
        Etched.SERVER_CONFIG.useAlbumCoverMenu = useAlbumCoverMenu::get;

        container.registerConfig(ModConfig.Type.CLIENT, clientBuilder.build());
        container.registerConfig(ModConfig.Type.SERVER, serverBuilder.build());
    }
}
