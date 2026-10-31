package github.gold_block.mixin;

import github.gold_block.client.SpearAnimations;
import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class SpearSwingAnimationMixin {

    @Shadow
    protected ModelPart getArm(HumanoidArm arm) {
        throw new AssertionError();
    }

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$stabAttackAnimation(LivingEntity entity, float ageInTicks, CallbackInfo ci) {
        InteractionHand swingHand = entity.swingingArm == null ? InteractionHand.MAIN_HAND : entity.swingingArm;
        if (!(entity.getItemInHand(swingHand).getItem() instanceof KnightSpearItem)) {
            return;
        }
        ci.cancel();

        float attackTime = ((EntityModel<?>) (Object) this).attackTime;
        if (attackTime <= 0.0F) {
            return;
        }
        HumanoidArm mainArm = entity.getMainArm();
        HumanoidArm attackArm = swingHand == InteractionHand.MAIN_HAND ? mainArm : mainArm.getOpposite();
        SpearAnimations.thirdPersonAttackHand(attackTime, this.getArm(attackArm));
    }
}
