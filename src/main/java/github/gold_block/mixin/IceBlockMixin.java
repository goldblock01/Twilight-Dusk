package github.gold_block.mixin;

import github.gold_block.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class IceBlockMixin {

    @Inject(method = "playerDestroy", at = @At("HEAD"), cancellable = true)
    private void twilightDusk$harvestIce(Level level, Player player, BlockPos pos, BlockState state,
                                         BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (!tool.is(ModItems.FROSTY_PICK.get())) {
            return;
        }
        player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
        player.causeFoodExhaustion(0.005F);
        if (!level.isClientSide()) {
            level.removeBlock(pos, false);
            Block.popResource(level, pos, new ItemStack(state.getBlock().asItem()));
        }
        ci.cancel();
    }
}
