package gg.moonflower.etched.core.compat;

import net.neoforged.fml.loading.FMLLoader;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.DiscHandlerRegistry;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.StorageSoundHandler;

public class SophisticatedCoreCompat {

    public static void load() {
        DiscHandlerRegistry.registerHandler(new EtchedDiscHandler());
        if (FMLLoader.getDist().isClient()) {
            StorageSounds.setHandler(StorageSoundHandler::playStorageSound);
        }
    }
}
