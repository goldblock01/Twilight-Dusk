package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class SpearChargeMovementMixin {

    @Redirect(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean twilightdusk$keepChargeInput(LocalPlayer player) {
        return player.isUsingItem() && !twilightdusk$chargingSpear(player);
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean twilightdusk$allowSprintWhileCharging(LocalPlayer player) {
        return player.isUsingItem() && !twilightdusk$chargingSpear(player);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
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

    @Unique
    private static boolean twilightdusk$chargingSpear(LocalPlayer player) {
        if (!player.isUsingItem()) {
            return false;
        }
        ItemStack stack = player.getUseItem();
        return stack.getItem() instanceof KnightSpearItem;
    }
}
