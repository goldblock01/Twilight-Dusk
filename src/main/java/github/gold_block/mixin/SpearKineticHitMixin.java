package github.gold_block.mixin;

import github.gold_block.client.SpearAnimations;
import github.gold_block.item.KnightSpearItem;
import github.gold_block.registry.ModSounds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class SpearKineticHitMixin {

    @Unique
    private long twilightdusk$lastKineticHit;

    @Unique
    private boolean twilightdusk$hasKineticHit;

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void twilightdusk$kineticHit(byte id, CallbackInfo ci) {
        if (id != KnightSpearItem.KINETIC_HIT_EVENT || !((Object) this instanceof Player player)) {
            return;
        }
        ci.cancel();
        if (!(player instanceof LocalPlayer localPlayer)) {
            return;
        }
        long now = player.level().getGameTime();
        if (twilightdusk$hasKineticHit && now - twilightdusk$lastKineticHit <= SpearAnimations.HIT_FEEDBACK_TICKS) {
            return;
        }
        twilightdusk$hasKineticHit = true;
        twilightdusk$lastKineticHit = now;
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                ModSounds.SPEAR_HIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
        SpearAnimations.onKineticHit(localPlayer);
    }
}
