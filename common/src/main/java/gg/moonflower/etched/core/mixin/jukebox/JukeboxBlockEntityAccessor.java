package gg.moonflower.etched.core.mixin.jukebox;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JukeboxBlockEntity.class)
public interface JukeboxBlockEntityAccessor {

    @Accessor("item")
    void etched$setRawItem(ItemStack stack);
}
