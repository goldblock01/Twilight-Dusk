package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpearAnimations {

    public static final int HIT_FEEDBACK_TICKS = 10;

    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0);
    private static final float RAD_TO_DEG = 180.0F / (float) Math.PI;

    private static final Map<UUID, Integer> LAST_KINETIC_HIT = new HashMap<>();

    private SpearAnimations() {
    }

    public static void onKineticHit(Entity entity) {
        LAST_KINETIC_HIT.put(entity.getUUID(), entity.tickCount);
    }

    public static float ticksSinceHit(Entity entity, float partialTick) {
        Integer last = LAST_KINETIC_HIT.get(entity.getUUID());
        if (last == null) {
            return Float.MAX_VALUE;
        }
        return (float) (entity.tickCount - last) + partialTick;
    }

    public static void clearHit(Entity entity) {
        LAST_KINETIC_HIT.remove(entity.getUUID());
    }

    public static float progress(float value, float start, float end) {
        return Mth.clamp(Mth.inverseLerp(value, start, end), 0.0F, 1.0F);
    }

    public static float hitFeedbackAmount(float ticksSinceHit) {
        return 0.4F * (outQuart(progress(ticksSinceHit, 1.0F, 3.0F))
                - inOutSine(progress(ticksSinceHit, 3.0F, 10.0F)));
    }

    public static void firstPersonAttack(float attackTime, PoseStack poseStack, int handSign) {
        float raise = inOutSine(progress(attackTime, 0.0F, 0.05F));
        float thrust = outBack(progress(attackTime, 0.05F, 0.2F));
        float settle = inOutExpo(progress(attackTime, 0.4F, 1.0F));
        poseStack.translate(handSign * 0.1F * (raise - thrust), -0.075F * (raise - settle), 0.65F * (raise - thrust));
        poseStack.mulPose(Axis.XP.rotationDegrees(-70.0F * (raise - settle)));
        poseStack.translate(0.0D, 0.0D, -0.25D * (settle - thrust));
    }

    public static void firstPersonUse(float ticksSinceHit, PoseStack poseStack, float ticksUsingItem,
                                      HumanoidArm arm, KnightSpearItem.ChargeProfile profile) {
        Params params = Params.of(profile, ticksUsingItem);
        int sign = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(
                sign * (params.raiseProgress() * 0.15F - params.raiseProgressEnd() * 0.05F
                        - params.swayProgress() * 0.1F + params.swayScaleSlow() * 0.005F),
                params.raiseProgress() * -0.075F + params.raiseProgressMiddle() * 0.075F + params.swayScaleFast() * 0.01F,
                params.raiseProgressStart() * 0.05F - params.raiseProgressEnd() * 0.05F + params.swayScaleSlow() * 0.005F);
        poseStack.rotateAround(Axis.XP.rotationDegrees(
                -65.0F * inOutBack(params.raiseProgress())
                        - 35.0F * params.lowerProgress()
                        + 100.0F * params.raiseBackProgress()
                        - 0.5F * params.swayScaleFast()), 0.0F, 0.1F, 0.0F);
        poseStack.rotateAround(Axis.YN.rotationDegrees(
                sign * (-90.0F * progress(params.raiseProgress(), 0.5F, 0.55F)
                        + 90.0F * params.swayProgress()
                        + 2.0F * params.swayScaleSlow())), sign * 0.15F, 0.0F, 0.0F);
        poseStack.translate(0.0F, -hitFeedbackAmount(ticksSinceHit), 0.0F);
    }

    public static void thirdPersonAttackHand(float attackTime, ModelPart attackArm) {
        float raise = inOutSine(progress(attackTime, 0.0F, 0.05F));
        float thrust = inQuad(progress(attackTime, 0.05F, 0.2F));
        float settle = inOutExpo(progress(attackTime, 0.4F, 1.0F));
        attackArm.xRot += (90.0F * raise - 120.0F * thrust + 30.0F * settle) * DEG_TO_RAD;
    }

    public static void thirdPersonAttackItem(float attackTime, PoseStack poseStack) {
        if (attackTime <= 0.0F) {
            return;
        }
        float thrust = inQuad(progress(attackTime, 0.05F, 0.2F));
        float settle = inOutExpo(progress(attackTime, 0.4F, 1.0F));
        poseStack.rotateAround(Axis.XN.rotationDegrees(70.0F * (thrust - settle)), 0.0F, -0.125F, 0.125F);
        poseStack.translate(0.0F, 0.38F * (thrust - settle), 0.0F);
    }

    public static void thirdPersonUseItem(float attackTime, float ticksSinceHit, PoseStack poseStack,
                                          float ticksUsingItem, HumanoidArm arm, KnightSpearItem.ChargeProfile profile) {
        if (profile == null || ticksUsingItem == 0.0F) {
            return;
        }
        float thrust = inQuad(progress(attackTime, 0.05F, 0.2F));
        float settle = inOutExpo(progress(attackTime, 0.4F, 1.0F));
        Params params = Params.of(profile, ticksUsingItem);
        int sign = arm == HumanoidArm.RIGHT ? 1 : -1;
        float raised = 1.0F - outBack(1.0F - params.raiseProgress());
        float feedback = hitFeedbackAmount(ticksSinceHit);
        poseStack.translate(0.0F, -feedback * 0.4F, -0.38F * (raised - params.raiseBackProgress()) + feedback);
        poseStack.rotateAround(Axis.XN.rotationDegrees(
                70.0F * (params.raiseProgress() - params.raiseBackProgress())
                        - 40.0F * (thrust - settle)), 0.0F, -0.03125F, 0.125F);
        poseStack.rotateAround(Axis.YP.rotationDegrees(
                sign * 90.0F * (params.raiseProgress() - params.swayProgress() + 3.0F * settle + thrust)),
                0.0F, 0.0F, 0.125F);
    }

    public static void thirdPersonHandUse(ModelPart arm, ModelPart head, boolean rightArm, InteractionHand armHand,
                                          KnightSpearItem.ChargeProfile profile, float ticksUsingItem,
                                          boolean usingItem, InteractionHand useItemHand,
                                          boolean fallFlying, float swimAmount) {
        int sign = rightArm ? 1 : -1;
        float headYRot = DEG_TO_RAD * Mth.clamp(RAD_TO_DEG * head.yRot, -180.0F, 180.0F);
        float headXRot = DEG_TO_RAD * Mth.clamp(RAD_TO_DEG * head.xRot, -180.0F, 180.0F);
        arm.yRot = -0.1F * sign + headYRot;
        arm.xRot = (float) (-Math.PI / 2) + headXRot + 0.8F;
        if (fallFlying || swimAmount > 0.0F) {
            arm.xRot -= 0.9599311F;
        }
        arm.yRot = DEG_TO_RAD * Mth.clamp(RAD_TO_DEG * arm.yRot, -60.0F, 60.0F);
        arm.xRot = DEG_TO_RAD * Mth.clamp(RAD_TO_DEG * arm.xRot, -120.0F, 30.0F);

        if (ticksUsingItem <= 0.0F) {
            return;
        }
        if (usingItem && useItemHand != armHand) {
            return;
        }
        if (profile == null) {
            return;
        }
        Params params = Params.of(profile, ticksUsingItem);
        arm.yRot += (float) (-sign) * params.swayScaleFast() * DEG_TO_RAD * params.swayIntensity();
        arm.zRot += (float) (-sign) * params.swayScaleSlow() * DEG_TO_RAD * params.swayIntensity() * 0.5F;
        arm.xRot += DEG_TO_RAD * (-40.0F * params.raiseProgressStart()
                + 30.0F * params.raiseProgressMiddle()
                - 20.0F * params.raiseProgressEnd()
                + 20.0F * params.lowerProgress()
                + 10.0F * params.raiseBackProgress()
                + 0.6F * params.swayScaleSlow() * params.swayIntensity());
    }

    public static float inQuad(float x) {
        return x * x;
    }

    public static float outQuart(float x) {
        return 1.0F - Mth.square(Mth.square(1.0F - x));
    }

    public static float outCubic(float x) {
        return 1.0F - (1.0F - x) * (1.0F - x) * (1.0F - x);
    }

    public static float outCirc(float x) {
        return (float) Math.sqrt(1.0F - Mth.square(x - 1.0F));
    }

    public static float inCirc(float x) {
        return (float) (-Math.sqrt(1.0F - x * x)) + 1.0F;
    }

    public static float inOutSine(float x) {
        return -(Mth.cos((float) Math.PI * x) - 1.0F) / 2.0F;
    }

    public static float inOutExpo(float x) {
        if (x < 0.5F) {
            return x == 0.0F ? 0.0F : (float) (Math.pow(2.0D, 20.0D * x - 10.0D) / 2.0D);
        }
        return x == 1.0F ? 1.0F : (float) ((2.0D - Math.pow(2.0D, -20.0D * x + 10.0D)) / 2.0D);
    }

    public static float inOutBack(float x) {
        if (x < 0.5F) {
            return 4.0F * x * x * (7.189819F * x - 2.5949094F) / 2.0F;
        }
        float f = 2.0F * x - 2.0F;
        return (f * f * (3.5949094F * f + 2.5949094F) + 2.0F) / 2.0F;
    }

    public static float outBack(float x) {
        float f = x - 1.0F;
        return 1.0F + 2.70158F * f * f * f + 1.70158F * f * f;
    }

    public static float inOutElastic(float x) {
        float c5 = (float) Math.PI * 4.0F / 9.0F;
        if (x == 0.0F) {
            return 0.0F;
        }
        if (x == 1.0F) {
            return 1.0F;
        }
        double sin = Math.sin((20.0D * x - 11.125D) * c5);
        return x < 0.5F
                ? (float) (-(Math.pow(2.0D, 20.0D * x - 10.0D) * sin) / 2.0D)
                : (float) (Math.pow(2.0D, -20.0D * x + 10.0D) * sin / 2.0D + 1.0D);
    }

    public record Params(float raiseProgress, float raiseProgressStart, float raiseProgressMiddle,
                         float raiseProgressEnd, float swayProgress, float lowerProgress,
                         float raiseBackProgress, float swayIntensity,
                         float swayScaleSlow, float swayScaleFast) {

        public static Params of(KnightSpearItem.ChargeProfile profile, float ticksUsingItem) {
            int delay = profile.delayTicks();
            int dismountEnd = profile.dismountTicks() + delay;
            int swayStart = dismountEnd - 20;
            int knockbackEnd = profile.knockbackTicks() + delay;
            int swayEnd = knockbackEnd - 40;
            int damageEnd = profile.damageTicks() + delay;

            float raise = progress(ticksUsingItem, 0.0F, delay);
            float raiseStart = progress(raise, 0.0F, 0.5F);
            float raiseMiddle = progress(raise, 0.5F, 0.8F);
            float raiseEnd = progress(raise, 0.8F, 1.0F);
            float sway = progress(ticksUsingItem, swayStart, swayEnd);
            float lower = outCubic(inOutElastic(progress(ticksUsingItem - 20.0F, swayEnd, knockbackEnd)));
            float raiseBack = progress(ticksUsingItem, damageEnd - 5, damageEnd);
            float swayIntensity = 2.0F * outCirc(sway) - 2.0F * inCirc(raiseBack);
            float swayScaleSlow = Mth.sin(ticksUsingItem * 19.0F * DEG_TO_RAD) * swayIntensity;
            float swayScaleFast = Mth.sin(ticksUsingItem * 30.0F * DEG_TO_RAD) * swayIntensity;

            return new Params(raise, raiseStart, raiseMiddle, raiseEnd, sway, lower, raiseBack,
                    swayIntensity, swayScaleSlow, swayScaleFast);
        }
    }
}
