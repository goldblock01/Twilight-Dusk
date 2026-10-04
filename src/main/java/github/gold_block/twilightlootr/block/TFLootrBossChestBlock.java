package github.gold_block.twilightlootr.block;

import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.block.LootrChestBlock;

public class TFLootrBossChestBlock extends LootrChestBlock {

    public TFLootrBossChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TFLootrBossChestBlockEntity(pos, state);
    }
}
