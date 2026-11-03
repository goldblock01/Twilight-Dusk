package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class SpearPickMixin {

    @Shadow
    private Minecraft minecraft;

    /** 执行已有距离查询后，仅对长矛应用原有最大距离规则。 */
    @WrapOperation(method = "pick", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getEntityReach()D"))
    private double twilightdusk$spearEntityReach(LocalPlayer player, Operation<Double> original) {
        double reach = original.call(player);
        if (player.getMainHandItem().getItem() instanceof KnightSpearItem) {
            return KnightSpearItem.maxReach(player);
        }
        return reach;
    }

    /** 原选取流程结束后，排除长矛最小攻击距离以内的目标。 */
    @Inject(method = "pick", at = @At("RETURN"), require = 0)
    private void twilightdusk$enforceSpearMinReach(CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        Entity crosshair = this.minecraft.crosshairPickEntity;
        if (crosshair == null || player == null) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof KnightSpearItem)) {
            return;
        }
        double minRange = KnightSpearItem.minReach(player);
        if (player.distanceToSqr(crosshair) < minRange * minRange) {
            this.minecraft.crosshairPickEntity = null;
        }
    }
}
