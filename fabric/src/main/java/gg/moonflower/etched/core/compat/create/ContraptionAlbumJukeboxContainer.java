package gg.moonflower.etched.core.compat.create;

import com.simibubi.create.content.contraptions.Contraption;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/**
 * The discs of an album jukebox on a contraption, kept in the contraption's copy of the block data.
 */
public class ContraptionAlbumJukeboxContainer extends SimpleContainer {

    private final Object entity;
    private final BlockPos localPos;

    public ContraptionAlbumJukeboxContainer(Object entity, BlockPos localPos) {
        super(9);
        this.entity = entity;
        this.localPos = localPos;

        CompoundTag data = CreateCompat.getBlockData(entity, localPos);
        Entity holder = ContraptionAccess.entity(entity);
        if (data != null && holder != null) {
            ContainerHelper.loadAllItems(data, this.getItems(), holder.registryAccess());
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();

        Entity holder = ContraptionAccess.entity(this.entity);
        if (holder == null) {
            return;
        }

        CompoundTag data = new CompoundTag();
        ContainerHelper.saveAllItems(data, this.getItems(), true, holder.registryAccess());
        CreateCompat.updateBlockData(this.entity, this.localPos, data);

        // Keep the disc showing in the jukebox model the way a placed jukebox does
        Contraption contraption = ContraptionAccess.contraption(this.entity);
        StructureBlockInfo info = contraption != null ? contraption.getBlocks().get(this.localPos) : null;
        boolean hasRecord = !this.isEmpty();
        if (info != null && info.state().hasProperty(AlbumJukeboxBlock.HAS_RECORD) && info.state().getValue(AlbumJukeboxBlock.HAS_RECORD) != hasRecord) {
            BlockState state = info.state().setValue(AlbumJukeboxBlock.HAS_RECORD, hasRecord);
            ContraptionAccess.setBlock(this.entity, this.localPos, new StructureBlockInfo(info.pos(), state, info.nbt()));
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return PlayableRecord.isPlayableRecord(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return CreateCompat.canUse(this.entity, this.localPos, EtchedBlocks.ALBUM_JUKEBOX.get(), player);
    }
}
