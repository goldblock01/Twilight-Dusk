package github.gold_block.mixin;

import github.gold_block.entity.PhantomKnightArrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

/** 为幻影箭调整原箭更新流程中的重力加速度。 */
@Mixin(AbstractArrow.class)
public abstract class PhantomArrowGravityMixin {
    /** 保留重力表达式及已有处理链，仅将幻影箭得到的重力缩小一半。 */
    @ModifyExpressionValue(method = "tick",
            at = @At(value = "CONSTANT", args = "doubleValue=0.05000000074505806"), require = 0)
    private double twilight_dusk$halfGravity(double gravity) {
        return (Object) this instanceof PhantomKnightArrow ? gravity * 0.5D : gravity;
    }
}
