package gg.moonflower.etched.common.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

/**
 * Adds the tooltip of a single component, which vanilla does not expose.
 */
public final class TooltipHelper {

    private TooltipHelper() {
    }

    public static <T extends TooltipProvider> void addToTooltip(ItemStack stack, DataComponentType<T> type, Item.TooltipContext context, Consumer<Component> adder, TooltipFlag flag) {
        T component = stack.get(type);
        if (component != null) {
            component.addToTooltip(context, adder, flag);
        }
    }
}
