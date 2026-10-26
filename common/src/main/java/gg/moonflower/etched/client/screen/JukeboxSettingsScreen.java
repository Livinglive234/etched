package gg.moonflower.etched.client.screen;

import gg.moonflower.etched.api.sound.SoundTracker;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import gg.moonflower.etched.common.menu.JukeboxSettingsMenu;
import gg.moonflower.etched.common.network.play.ServerboundSoundSettingsPacket;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.platform.EtchedPlatform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The screen to set the volume of a vanilla jukebox.
 */
public class JukeboxSettingsScreen extends AbstractContainerScreen<JukeboxSettingsMenu> {

    private static final ResourceLocation TEXTURE = Etched.etchedPath("textures/gui/radio.png");

    public JukeboxSettingsScreen(JukeboxSettingsMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 39;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SettingSlider(this.leftPos + 8, this.topPos + 18, 160, 14, "screen." + Etched.MOD_ID + ".volume", 0, 100, Math.round(this.menu.getInitialVolume() * 100.0F), value -> EtchedPlatform.get().sendToServer(new ServerboundSoundSettingsPacket(value / 100.0F))));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Container screens only drag items, so the slider has to be passed drags itself
        if (button == 0 && this.isDragging() && this.getFocused() instanceof SettingSlider slider) {
            return slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.minecraft.level != null && SoundTracker.isPlayingAt(this.menu.getPos()) && this.minecraft.level.getBlockEntity(this.menu.getPos()) instanceof JukeboxBlockEntity jukebox) {
            NowPlayingHeader.render(guiGraphics, this.font, this.width, this.topPos, NowPlayingHeader.forRecord(this.minecraft.level.registryAccess(), jukebox.getTheItem()));
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
