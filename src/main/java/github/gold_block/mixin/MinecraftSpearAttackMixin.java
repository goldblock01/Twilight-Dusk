package github.gold_block.mixin;

import github.gold_block.item.KnightSpearItem;
import github.gold_block.network.ModNetwork;
import github.gold_block.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftSpearAttackMixin {

    @Shadow
    protected int missTime;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$spearJab(CallbackInfoReturnable<Boolean> ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof KnightSpearItem)) {
            return;
        }
        ci.cancel();
        if (this.missTime > 0 || !KnightSpearItem.isAttackReady(player)) {
            return;
        }
        ModNetwork.CHANNEL.sendToServer(ModNetwork.SpearJabPacket.INSTANCE);
        player.resetAttackStrengthTicker();
        player.swing(InteractionHand.MAIN_HAND, false);
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(),
                ModSounds.SPEAR_ATTACK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$suppressSpearMining(boolean destroying, CallbackInfo ci) {
        if (!destroying) {
            return;
        }
        Minecraft minecraft = (Minecraft) (Object) this;
        LocalPlayer player = minecraft.player;
        if (player != null && player.getMainHandItem().getItem() instanceof KnightSpearItem) {
            ci.cancel();
        }
    }
}
