package github.gold_block.mixin;

import github.gold_block.event.ScytheSweepEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerScytheSweepMixin {

    @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), require = 0)
    private void twilight_dusk$beginAttack(Entity target, CallbackInfo ci) {
        ScytheSweepEvents.beginAttack((Player) (Object) this, target);
    }

    @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"), require = 0)
    private void twilight_dusk$endAttack(Entity target, CallbackInfo ci) {
        ScytheSweepEvents.endAttack();
    }
}
