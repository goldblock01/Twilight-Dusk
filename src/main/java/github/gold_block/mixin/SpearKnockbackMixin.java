package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class SpearKnockbackMixin {

    @Inject(method = "knockback(DDD)V", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$skipSpearKnockback(double strength, double x, double z, CallbackInfo ci) {
        if (KnightSpearItem.isSuppressingKnockback()) {
            ci.cancel();
        }
    }
}
