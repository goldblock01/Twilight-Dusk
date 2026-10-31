package github.gold_block.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import github.gold_block.client.SpearAnimations;
import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class SpearThirdPersonItemMixin {

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void twilightdusk$spearThirdPersonItem(LivingEntity entity, ItemStack stack,
                                                   ItemDisplayContext displayContext, HumanoidArm arm,
                                                   PoseStack poseStack, MultiBufferSource buffer,
                                                   int light, CallbackInfo ci) {
        if (!(stack.getItem() instanceof KnightSpearItem spear)) {
            return;
        }
        EntityModel<?> model = ((RenderLayer<?, ?>) (Object) this).getParentModel();
        float attackTime = model.attackTime;
        HumanoidArm mainArm = entity.getMainArm();
        InteractionHand swingHand = entity.swingingArm == null ? InteractionHand.MAIN_HAND : entity.swingingArm;
        HumanoidArm attackArm = swingHand == InteractionHand.MAIN_HAND ? mainArm : mainArm.getOpposite();
        if (arm == attackArm) {
            SpearAnimations.thirdPersonAttackItem(attackTime, poseStack);
        }

        float partialTick = Minecraft.getInstance().getFrameTime();
        boolean usingThisArm = entity.isUsingItem()
                && (entity.getUsedItemHand() == InteractionHand.MAIN_HAND) == (arm == mainArm);
        if (usingThisArm) {
            float ticksUsing = (float) entity.getTicksUsingItem() + partialTick;
            SpearAnimations.thirdPersonUseItem(attackTime, SpearAnimations.ticksSinceHit(entity, partialTick),
                    poseStack, ticksUsing, arm, spear.getProfile());
        }
    }
}
