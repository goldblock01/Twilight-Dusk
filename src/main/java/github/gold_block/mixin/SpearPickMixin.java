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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class SpearPickMixin {

    @Shadow
    private Minecraft minecraft;

    @Redirect(method = "pick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getEntityReach()D"))
    private double twilightdusk$spearEntityReach(LocalPlayer player) {
        if (player.getMainHandItem().getItem() instanceof KnightSpearItem) {
            return KnightSpearItem.maxReach(player);
        }
        return player.getEntityReach();
    }

    @Inject(method = "pick", at = @At("RETURN"))
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
