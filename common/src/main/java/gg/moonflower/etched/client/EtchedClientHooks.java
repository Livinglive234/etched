package gg.moonflower.etched.client;

import gg.moonflower.etched.client.screen.EditMusicLabelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Entry points from common code into client only code. Kept separate so the server never loads the client classes.
 */
@ApiStatus.Internal
public final class EtchedClientHooks {

    private EtchedClientHooks() {
    }

    public static void openMusicLabelEditScreen(Player player, InteractionHand hand, ItemStack stack) {
        Minecraft.getInstance().setScreen(new EditMusicLabelScreen(player, hand, stack));
    }
}
