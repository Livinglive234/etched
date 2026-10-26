package gg.moonflower.etched.client.screen;

import gg.moonflower.etched.common.network.play.ServerboundSoundSettingsPacket;
import gg.moonflower.etched.platform.EtchedPlatform;
import gg.moonflower.etched.common.menu.RadioMenu;
import gg.moonflower.etched.common.menu.UrlMenu;
import gg.moonflower.etched.common.network.play.SetUrlPacket;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * @author Ocelot
 */
public class RadioScreen extends AbstractContainerScreen<RadioMenu> implements UrlMenu {

    private static final ResourceLocation TEXTURE = Etched.etchedPath("textures/gui/radio.png");

    private static final int TEXTURE_HEIGHT = 39;
    private static final int SETTINGS_HEIGHT = 20;

    private EditBox url;

    public RadioScreen(RadioMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = TEXTURE_HEIGHT + SETTINGS_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        String urlText = this.url != null ? this.url.getValue() : this.menu.getInitialUrl();
        this.url = new EditBox(this.font, this.leftPos + 10, this.topPos + 21, 154, 16, null, Component.translatable("container." + Etched.MOD_ID + ".radio.url"));
        this.url.setMaxLength(32768);
        this.url.setValue(urlText);
        this.url.setTextColor(-1);
        this.url.setTextColorUneditable(-1);
        this.url.setBordered(false);
        this.setFocused(this.url);
        this.addRenderableWidget(this.url);
        this.addRenderableWidget(new SettingSlider(this.leftPos + 8, this.topPos + 36, 160, 14, "screen." + Etched.MOD_ID + ".volume", 0, 100, Math.round(this.menu.getInitialVolume() * 100.0F), value -> EtchedPlatform.get().sendToServer(new ServerboundSoundSettingsPacket(value / 100.0F))));
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            EtchedPlatform.get().sendToServer(new SetUrlPacket(this.url.getValue()));
            Minecraft.getInstance().player.closeContainer();
        }).bounds((this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2 + this.imageHeight + 5, this.imageWidth, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int mouseX, int mouseY) {
        // Stretch a plain row of the panel to make room for the sound settings
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, 35);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos + 35, this.imageWidth, SETTINGS_HEIGHT, 0, 34, this.imageWidth, 1, 256, 256);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos + 35 + SETTINGS_HEIGHT, 0, 35, this.imageWidth, TEXTURE_HEIGHT - 35);
        guiGraphics.blit(TEXTURE, this.leftPos + 8, this.topPos + 18, 0, 39, 160, 14);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.url.keyPressed(keyCode, scanCode, modifiers) || (this.url.isFocused() && this.url.isVisible() && keyCode != GLFW_KEY_ESCAPE) || super.keyPressed(keyCode, scanCode, modifiers);
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
    public void setUrl(String url) {
        this.url.setValue(url);
    }
}
