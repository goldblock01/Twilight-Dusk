package github.gold_block.mixin;

import github.gold_block.entity.KnightArrow;
import github.gold_block.util.ArrowPenetrationMath;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/** 单独处理附魔减伤阶段的箭矢穿透。 */
@Mixin(LivingEntity.class)
public abstract class KnightArrowEnchantmentMixin {
    /** 先执行已有附魔处理链，再返还该阶段减伤，保留抗性效果。 */
    @WrapOperation(method = "getDamageAfterMagicAbsorb", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterMagicAbsorb(FF)F"))
    private float twilight_dusk$enchantmentPenetration(float amount, float protection, Operation<Float> original, DamageSource source, float incoming) {
        float result = original.call(amount, protection);
        return source.getDirectEntity() instanceof KnightArrow arrow
                ? ArrowPenetrationMath.restore(amount, result, arrow.enchantmentPenetration()) : result;
    }
}
