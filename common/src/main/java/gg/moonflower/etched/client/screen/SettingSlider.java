package gg.moonflower.etched.client.screen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

/**
 * Picks a whole number setting, such as the volume of a music player.
 * <p>
 * Based on the slider of BazZziliuS/etched, https://github.com/BazZziliuS/etched
 */
public class SettingSlider extends AbstractSliderButton {

    private final String translationKey;
    private final int min;
    private final int max;
    private final IntConsumer onChange;
    private int lastValue;

    /**
     * @param translationKey The message, given the current value
     * @param onChange       Called with each new whole value while dragging
     */
    public SettingSlider(int x, int y, int width, int height, String translationKey, int min, int max, int value, IntConsumer onChange) {
        super(x, y, width, height, Component.empty(), (double) (value - min) / (max - min));
        this.translationKey = translationKey;
        this.min = min;
        this.max = max;
        this.onChange = onChange;
        this.lastValue = this.getValue();
        this.updateMessage();
    }

    public int getValue() {
        return this.min + (int) Math.round(this.value * (this.max - this.min));
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.translatable(this.translationKey, this.getValue()));
    }

    @Override
    protected void applyValue() {
        // Only whole values are reported so dragging does not flood the server
        int value = this.getValue();
        if (value != this.lastValue) {
            this.lastValue = value;
            this.onChange.accept(value);
        }
    }
}
