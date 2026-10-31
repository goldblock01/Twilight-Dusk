package github.gold_block.mixin;

import github.gold_block.client.SpearAnimations;
import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class SpearChargeArmMixin {

    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$poseRightArm(LivingEntity entity, CallbackInfo ci) {
        twilightdusk$pose(entity, HumanoidArm.RIGHT, this.rightArm, ci);
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$poseLeftArm(LivingEntity entity, CallbackInfo ci) {
        twilightdusk$pose(entity, HumanoidArm.LEFT, this.leftArm, ci);
    }

    @Unique
    private void twilightdusk$pose(LivingEntity entity, HumanoidArm side, ModelPart arm, CallbackInfo ci) {
        InteractionHand hand = side == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack stack = entity.getItemInHand(hand);
        if (!(stack.getItem() instanceof KnightSpearItem spear)) {
            return;
        }
        float partial = Minecraft.getInstance().getFrameTime();
        boolean using = entity.isUsingItem();
        float ticksUsing = using ? (float) entity.getTicksUsingItem() + partial : 0.0F;
        SpearAnimations.thirdPersonHandUse(arm, this.head, side == HumanoidArm.RIGHT, hand,
                spear.getProfile(), ticksUsing, using, entity.getUsedItemHand(),
                entity.isFallFlying(), entity.getSwimAmount(partial));
        ci.cancel();
    }
}
