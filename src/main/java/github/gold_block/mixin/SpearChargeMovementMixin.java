package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为本模组长矛保留蓄力时的移动输入与冲刺能力。
 * 包装调用先执行已有处理，再仅为本模组长矛调整结果，其他物品沿用已有结果。
 * 各注入允许独立缺失，将无法接入的移动能力降级，而非阻止游戏启动。
 */
@Mixin(LocalPlayer.class)
public class SpearChargeMovementMixin {

    /**
     * 对该方法内的物品使用判断保留原有长矛放行规则，避免蓄力输入受到使用减速。
     * 先调用已有处理，保留其他注入的执行及副作用；仅在使用本模组长矛时调整返回值。
     * require 为零允许目标调用缺失，不捕获处理函数异常；缺失由转换检查报告。
     */
    @WrapOperation(method = "aiStep", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean twilightdusk$keepChargeInput(LocalPlayer player, Operation<Boolean> original) {
        boolean usingItem = original.call(player);
        return usingItem && !twilightdusk$chargingSpear(player);
    }

    /**
     * 仅放行长矛的物品使用限制，目标方法中的其他冲刺条件仍按原有流程判断。
     * 包装调用保留同一位置上已有处理的结果，不为其他物品改变冲刺规则。
     */
    @WrapOperation(method = "canStartSprinting", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean twilightdusk$allowSprintWhileCharging(LocalPlayer player, Operation<Boolean> original) {
        boolean usingItem = original.call(player);
        return usingItem && !twilightdusk$chargingSpear(player);
    }

    /** 按现有条件补充长矛蓄力时的冲刺按键响应，保留原有辅助行为。 */
    @Inject(method = "aiStep", at = @At("TAIL"), require = 0)
    private void twilightdusk$startSprintWhileCharging(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (player.isSprinting() || !twilightdusk$chargingSpear(player) || player.isPassenger()) {
            return;
        }
        if (!Minecraft.getInstance().options.keySprint.isDown() || player.isFallFlying()) {
            return;
        }
        if (!player.input.hasForwardImpulse() || (player.isInWater() && !player.isUnderWater())) {
            return;
        }
        player.setSprinting(true);
    }

    /** 读取真实物品使用状态，避免将移动判断的放行结果误当作停止使用物品。 */
    @Unique
    private static boolean twilightdusk$chargingSpear(LocalPlayer player) {
        if (!player.isUsingItem()) {
            return false;
        }
        ItemStack stack = player.getUseItem();
        return stack.getItem() instanceof KnightSpearItem;
    }
}
