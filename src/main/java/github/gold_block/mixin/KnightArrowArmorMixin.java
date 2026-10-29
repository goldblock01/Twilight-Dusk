package github.gold_block.mixin;

import github.gold_block.entity.KnightArrow;
import github.gold_block.util.ArrowPenetrationMath;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 在实际护甲结算结果上返还骑士箭穿透的减伤量。 */
@Mixin(LivingEntity.class)
public abstract class KnightArrowArmorMixin {
    /** 调用实体自身的护甲结算实现。 */
    @Shadow
    protected abstract float getDamageAfterArmorAbsorb(DamageSource source, float amount);

    /** 保留护甲结算的原有副作用，并按箭类型处理返回值。 */
    @Redirect(method = "actuallyHurt", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float twilight_dusk$armorPenetration(LivingEntity victim, DamageSource source, float amount) {
        float result = getDamageAfterArmorAbsorb(source, amount);
        return source.getDirectEntity() instanceof KnightArrow arrow
                ? ArrowPenetrationMath.restore(amount, result, arrow.armorPenetration()) : result;
    }
}
