package github.gold_block.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.events.CharmEvents;

@Mixin(CharmEvents.class)
public class CharmEventsMixin {

    @Inject(method = "charmOfKeeping(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void twilight_dusk$disableCharmOfKeeping(Player player, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "keepsakeCasket(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void twilight_dusk$disableKeepsakeCasket(Player player, CallbackInfo ci) {
        ci.cancel();
    }
}
