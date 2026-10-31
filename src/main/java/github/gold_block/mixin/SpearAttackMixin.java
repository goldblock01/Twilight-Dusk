package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class SpearAttackMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$spearJab(Entity target, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof KnightSpearItem spear)) {
            return;
        }
        ci.cancel();
        if (!KnightSpearItem.isAttackReady(player)) {
            return;
        }
        if (player.level().isClientSide()) {
            player.resetAttackStrengthTicker();
            player.swing(InteractionHand.MAIN_HAND, false);
            return;
        }
        spear.performJab(player, stack);
    }
}
