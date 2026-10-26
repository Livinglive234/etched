package gg.moonflower.etched.client.screen;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Draws what is currently playing across the top of a screen.
 */
public final class NowPlayingHeader {

    private static final int PADDING = 4;

    private NowPlayingHeader() {
    }

    /**
     * Draws the header centered above the container.
     *
     * @param graphics The graphics to draw with
     * @param font     The font to draw with
     * @param width    The width of the screen
     * @param topPos   The top of the container
     * @param playing  The name of what is playing, or <code>null</code> to draw nothing
     */
    public static void render(GuiGraphics graphics, Font font, int width, int topPos, @Nullable Component playing) {
        if (playing == null) {
            return;
        }

        Component text = Component.translatable("screen." + Etched.MOD_ID + ".now_playing", playing);
        int maxWidth = width - 2 * (PADDING + 8);
        String shown = font.width(text) > maxWidth ? font.plainSubstrByWidth(text.getString(), maxWidth - font.width("...")) + "..." : text.getString();
        int textWidth = font.width(shown);
        int x = (width - textWidth) / 2;
        int y = Math.max(PADDING + 2, topPos - 16);
        graphics.fill(x - PADDING, y - PADDING, x + textWidth + PADDING, y + font.lineHeight + PADDING - 1, 0xA0000000);
        graphics.drawString(font, shown, x, y, 0xFFFFFF, true);
    }

    /**
     * @param registries The registries to look up songs in
     * @param stack      A record
     * @return The name to show for what the record is playing, or <code>null</code> if it has none
     */
    public static @Nullable Component forRecord(HolderLookup.Provider registries, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        List<TrackData> tracks = PlayableRecord.getTracks(registries, stack);
        if (tracks.size() == 1) {
            return tracks.getFirst().getDisplayName();
        }
        if (tracks.size() > 1) {
            return PlayableRecord.getAlbum(stack).map(TrackData::getDisplayName).orElseGet(() -> tracks.getFirst().getDisplayName());
        }
        return JukeboxSong.fromStack(registries, stack).map(song -> song.value().description()).orElse(null);
    }
}
