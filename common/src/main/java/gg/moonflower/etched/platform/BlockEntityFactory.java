package gg.moonflower.etched.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Creates a block entity.
 *
 * @param <T> The type of block entity
 */
@FunctionalInterface
public interface BlockEntityFactory<T extends BlockEntity> {

    T create(BlockPos pos, BlockState state);
}
