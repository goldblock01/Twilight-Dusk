package github.gold_block.mixin;

import github.gold_block.event.CasketFinalForm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.block.KeepsakeCasketBlock;
import twilightforest.init.TFItems;

@Mixin(KeepsakeCasketBlock.class)
public class KeepsakeCasketBlockMixin {

    @Inject(method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$blockFinalFormRepair(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(TFItems.CHARM_OF_KEEPING_3.get()) && state.getValue(KeepsakeCasketBlock.BREAKAGE) > 0
                && level.getBlockEntity(pos) instanceof CasketFinalForm casket && casket.twilight_dusk$isFinalForm()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
