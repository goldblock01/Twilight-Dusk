package github.gold_block.mixin;

import github.gold_block.entity.KnightArrow;
import github.gold_block.util.ArrowPenetrationMath;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

/** 处理玩家受箭矢伤害时独立执行的护甲结算。 */
@Mixin(Player.class)
public abstract class KnightArrowPlayerArmorMixin extends LivingEntity {
    /** 为玩家混入提供实体继承关系，以调用继承的护甲结算方法。 */
    protected KnightArrowPlayerArmorMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    /** 先执行已有护甲处理链一次，再按箭类型处理返回值。 */
    @WrapOperation(method = "actuallyHurt", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float twilight_dusk$armorPenetration(Player victim, DamageSource source, float amount, Operation<Float> original) {
        float result = original.call(victim, source, amount);
        return source.getDirectEntity() instanceof KnightArrow arrow
                ? ArrowPenetrationMath.restore(amount, result, arrow.armorPenetration()) : result;
    }
}
