package github.gold_block.mixin;

import github.gold_block.entity.PhantomKnightArrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** 为幻影箭调整原箭更新流程中的重力加速度。 */
@Mixin(AbstractArrow.class)
public abstract class PhantomArrowGravityMixin {
    /** 将幻影箭的重力项缩小为原值的一半。 */
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 0.05000000074505806D), require = 0)
    private double twilight_dusk$halfGravity(double gravity) {
        return (Object) this instanceof PhantomKnightArrow ? gravity * 0.5D : gravity;
    }
}
