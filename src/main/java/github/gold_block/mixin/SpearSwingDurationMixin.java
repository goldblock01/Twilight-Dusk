package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SpearSwingDurationMixin {

    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$spearSwingDuration(CallbackInfoReturnable<Integer> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getMainHandItem().getItem() instanceof KnightSpearItem spear) {
            cir.setReturnValue(spear.getStabDurationTicks());
        }
    }
}
