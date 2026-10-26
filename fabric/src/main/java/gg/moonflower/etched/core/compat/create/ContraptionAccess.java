package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Reaches the entity that carries a contraption. Its class needs libraries that are not on the compile path, so it is only handled as
 * an object and its methods are called by name.
 */
final class ContraptionAccess {

    private ContraptionAccess() {
    }

    static @Nullable Entity entity(@Nullable Object holder) {
        return holder instanceof Entity entity ? entity : null;
    }

    static @Nullable Contraption contraption(@Nullable Object holder) {
        if (holder == null) {
            return null;
        }
        try {
            return holder.getClass().getMethod("getContraption").invoke(holder) instanceof Contraption contraption ? contraption : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Changes the block of a contraption on the server and tells the clients about it.
     */
    static void setBlock(Object holder, BlockPos localPos, StructureBlockInfo info) {
        try {
            Method method = holder.getClass().getMethod("setBlock", BlockPos.class, StructureBlockInfo.class);
            method.invoke(holder, localPos, info);
        } catch (ReflectiveOperationException ignored) {
        }
    }
}
