package gg.moonflower.etched.core;

import java.util.function.Supplier;

/**
 * Holds the config values used by common code. The mod loader replaces the suppliers with values backed by a config file.
 */
public class EtchedConfig {

    public static class Client {

        public Supplier<Boolean> forceStereo = () -> false;
        public Supplier<Boolean> smoothParrotAnimation = () -> true;
        public Supplier<Boolean> allowPrivateNetwork = () -> false;
        public Supplier<Boolean> muteGameMusic = () -> true;
    }

    public static class Server {

        public Supplier<Boolean> useBoomboxMenu = () -> false;
        public Supplier<Boolean> useAlbumCoverMenu = () -> false;
    }
}
