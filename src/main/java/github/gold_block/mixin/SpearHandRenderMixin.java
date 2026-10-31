package github.gold_block.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.gold_block.client.SpearAnimations;
import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class SpearHandRenderMixin {

    @Shadow
    public abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context,
                                    boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int light);

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$renderSpear(AbstractClientPlayer player, float partialTick, float pitch,
                                          InteractionHand hand, float swingProgress, ItemStack stack,
                                          float equippedProgress, PoseStack poseStack,
                                          MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!(stack.getItem() instanceof KnightSpearItem spear)) {
            return;
        }
        ci.cancel();
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean rightArm = arm == HumanoidArm.RIGHT;
        int handSign = rightArm ? 1 : -1;

        poseStack.pushPose();
        try {
            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
                poseStack.translate((float) handSign * 0.56F, -0.52F, -0.72F);
                float ticksUsing = (float) stack.getUseDuration()
                        - ((float) player.getUseItemRemainingTicks() - partialTick + 1.0F);
                SpearAnimations.firstPersonUse(SpearAnimations.ticksSinceHit(player, partialTick),
                        poseStack, ticksUsing, arm, spear.getProfile());
            } else {
                poseStack.translate((float) handSign * 0.56F,
                        -0.52F + equippedProgress * -0.6F, -0.72F);
                SpearAnimations.firstPersonAttack(swingProgress, poseStack, handSign);
            }

            this.renderItem(player, stack,
                    rightArm ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                    !rightArm, poseStack, buffer, light);
        } finally {
            poseStack.popPose();
        }
    }
}
