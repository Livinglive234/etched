package gg.moonflower.etched.core.compat.jei;

import gg.moonflower.etched.client.screen.JukeboxSettingsScreen;
import gg.moonflower.etched.client.screen.MusicSearchScreen;
import gg.moonflower.etched.client.screen.RadioScreen;
import gg.moonflower.etched.core.Etched;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

/**
 * Hides the JEI overlay on screens that are full of their own controls, where it only gets in the way.
 */
@JeiPlugin
public class EtchedJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return Etched.etchedPath("jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(RadioScreen.class, screen -> null);
        registration.addGuiScreenHandler(JukeboxSettingsScreen.class, screen -> null);
        registration.addGuiScreenHandler(MusicSearchScreen.class, screen -> null);
    }
}
