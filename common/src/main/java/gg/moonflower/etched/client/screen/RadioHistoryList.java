package gg.moonflower.etched.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A short list of recent radio stations that scrolls. Picking a station only gives it to the screen, it does not start playing.
 */
final class RadioHistoryList extends AbstractWidget {

    static final int ROW_HEIGHT = 12;
    static final int ROWS = 5;
    private static final int SCROLLBAR_WIDTH = 5;

    private final Font font;
    private final Consumer<String> onSelect;
    private final Function<String, String> labels;
    private final Component emptyMessage;
    private List<String> stations;
    private int offset;

    RadioHistoryList(Font font, int x, int y, int width, List<String> stations, Component title, Component emptyMessage, Function<String, String> labels, Consumer<String> onSelect) {
        super(x, y, width, ROWS * ROW_HEIGHT, title);
        this.font = font;
        this.stations = stations;
        this.emptyMessage = emptyMessage;
        this.onSelect = onSelect;
        this.labels = labels;
    }

    void setStations(List<String> stations) {
        this.stations = stations;
        this.offset = Mth.clamp(this.offset, 0, this.maxOffset());
    }

    private int maxOffset() {
        return Math.max(0, this.stations.size() - ROWS);
    }

    private boolean hasScrollbar() {
        return this.stations.size() > ROWS;
    }

    private int textWidth() {
        return this.width - 6 - (this.hasScrollbar() ? SCROLLBAR_WIDTH : 0);
    }

    private int rowAt(double mouseX, double mouseY) {
        if (mouseX < this.getX() || mouseX >= this.getX() + this.width - (this.hasScrollbar() ? SCROLLBAR_WIDTH : 0) || mouseY < this.getY() || mouseY >= this.getY() + this.height) {
            return -1;
        }
        int row = this.offset + (int) ((mouseY - this.getY()) / ROW_HEIGHT);
        return row < this.stations.size() ? row : -1;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xAA000000);

        if (this.stations.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, this.emptyMessage, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xA0A0A0);
            return;
        }

        int hovered = this.rowAt(mouseX, mouseY);
        for (int i = 0; i < ROWS && this.offset + i < this.stations.size(); i++) {
            int row = this.offset + i;
            int top = this.getY() + i * ROW_HEIGHT;
            if (row == hovered) {
                guiGraphics.fill(this.getX() + 1, top, this.getX() + this.width - (this.hasScrollbar() ? SCROLLBAR_WIDTH : 0) - 1, top + ROW_HEIGHT, 0x40FFFFFF);
            }

            String text = this.labels.apply(this.stations.get(row));
            if (this.font.width(text) > this.textWidth()) {
                text = this.font.plainSubstrByWidth(text, this.textWidth() - this.font.width("...")) + "...";
            }
            guiGraphics.drawString(this.font, text, this.getX() + 3, top + 2, 0xFFFFFF, false);
        }

        if (this.hasScrollbar()) {
            int trackLeft = this.getX() + this.width - SCROLLBAR_WIDTH;
            guiGraphics.fill(trackLeft, this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF202020);
            int thumbHeight = Math.max(8, this.height * ROWS / this.stations.size());
            int thumbTop = this.getY() + (this.height - thumbHeight) * this.offset / this.maxOffset();
            guiGraphics.fill(trackLeft + 1, thumbTop, this.getX() + this.width - 1, thumbTop + thumbHeight, 0xFFA0A0A0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible || button != 0) {
            return false;
        }

        if (this.hasScrollbar() && mouseX >= this.getX() + this.width - SCROLLBAR_WIDTH && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height) {
            this.scrollTo(mouseY);
            return true;
        }

        int row = this.rowAt(mouseX, mouseY);
        if (row == -1) {
            return false;
        }
        this.playDownSound(net.minecraft.client.Minecraft.getInstance().getSoundManager());
        this.onSelect.accept(this.stations.get(row));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.hasScrollbar() && button == 0 && mouseX >= this.getX() + this.width - SCROLLBAR_WIDTH - 2 && mouseX < this.getX() + this.width + 2) {
            this.scrollTo(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void scrollTo(double mouseY) {
        this.offset = Mth.clamp((int) Math.round((mouseY - this.getY()) / this.height * this.maxOffset()), 0, this.maxOffset());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.isMouseOver(mouseX, mouseY) || !this.hasScrollbar()) {
            return false;
        }
        this.offset = Mth.clamp(this.offset - (int) Math.signum(scrollY), 0, this.maxOffset());
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
