package github.gold_block.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import github.gold_block.entity.TowerSpirit;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** 为高塔乐魂乘客提供第三人称距离，仍由相机执行方块遮挡检测。 */
@Mixin(Camera.class)
public abstract class TowerSpiritCameraMixin {
    @Shadow private Entity entity;

    /** 骑乘成年个体时扩大距离，并保留已有的更远距离。 */
    @ModifyExpressionValue(method = "setup",
            at = @At(value = "CONSTANT", args = "doubleValue=4.0"), require = 0)
    private double twilight_dusk$passengerCameraDistance(double distance) {
        if (entity != null && entity.getVehicle() instanceof TowerSpirit spirit && !spirit.isBaby()) {
            return Math.max(distance, spirit.getPassengerCameraDistance());
        }
        return distance;
    }
}
