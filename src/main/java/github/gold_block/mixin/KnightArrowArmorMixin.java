package github.gold_block.mixin;

import github.gold_block.entity.KnightArrow;
import github.gold_block.util.ArrowPenetrationMath;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/** 在实际护甲结算结果上返还骑士箭穿透的减伤量。 */
@Mixin(LivingEntity.class)
public abstract class KnightArrowArmorMixin {
    /** 先执行已有护甲处理链一次，再按箭类型处理返回值。 */
    @WrapOperation(method = "actuallyHurt", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float twilight_dusk$armorPenetration(LivingEntity victim, DamageSource source, float amount, Operation<Float> original) {
        float result = original.call(victim, source, amount);
        return source.getDirectEntity() instanceof KnightArrow arrow
                ? ArrowPenetrationMath.restore(amount, result, arrow.armorPenetration()) : result;
    }
}
