package gg.moonflower.etched.core.compat.jei;

import gg.moonflower.etched.client.screen.JukeboxSettingsScreen;
import gg.moonflower.etched.client.screen.RadioScreen;
import gg.moonflower.etched.core.Etched;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.Screen;
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
        registration.addGuiScreenHandler(RadioScreen.class, screen -> fullWidth(screen));
        registration.addGuiScreenHandler(JukeboxSettingsScreen.class, screen -> fullWidth(screen));
    }

    /**
     * JEI only draws where the screen leaves room, and returning nothing would hand the screen back to its default handler.
     * Claiming the whole width leaves it no room.
     */
    private static IGuiProperties fullWidth(Screen screen) {
        return new IGuiProperties() {
            @Override
            public Class<? extends Screen> screenClass() {
                return screen.getClass();
            }

            @Override
            public int guiLeft() {
                return 0;
            }

            @Override
            public int guiTop() {
                return 0;
            }

            @Override
            public int guiXSize() {
                return screen.width;
            }

            @Override
            public int guiYSize() {
                return screen.height;
            }

            @Override
            public int screenWidth() {
                return screen.width;
            }

            @Override
            public int screenHeight() {
                return screen.height;
            }
        };
    }
}
