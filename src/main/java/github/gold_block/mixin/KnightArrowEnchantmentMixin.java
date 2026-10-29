package github.gold_block.mixin;

import github.gold_block.entity.KnightArrow;
import github.gold_block.util.ArrowPenetrationMath;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 单独处理附魔减伤阶段的箭矢穿透。 */
@Mixin(LivingEntity.class)
public abstract class KnightArrowEnchantmentMixin {
    /** 返还附魔减掉的伤害，使抗性效果继续保留原有作用。 */
    @Redirect(method = "getDamageAfterMagicAbsorb", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterMagicAbsorb(FF)F"))
    private float twilight_dusk$enchantmentPenetration(float amount, float protection, DamageSource source, float incoming) {
        float result = CombatRules.getDamageAfterMagicAbsorb(amount, protection);
        return source.getDirectEntity() instanceof KnightArrow arrow
                ? ArrowPenetrationMath.restore(amount, result, arrow.enchantmentPenetration()) : result;
    }
}
