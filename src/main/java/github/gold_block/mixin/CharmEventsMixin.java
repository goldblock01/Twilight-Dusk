package github.gold_block.mixin;

import github.gold_block.event.CasketEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.events.CharmEvents;

@Mixin(CharmEvents.class)
public class CharmEventsMixin {

    @Inject(method = "charmOfKeeping(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void twilight_dusk$onCharmOfKeeping(Player player, CallbackInfo ci) {
        CasketEvents.charmOfKeeping(player);
        ci.cancel();
    }

    @Inject(method = "keepsakeCasket(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void twilight_dusk$onKeepsakeCasket(Player player, CallbackInfo ci) {
        CasketEvents.keepsakeCasket(player);
        ci.cancel();
    }
}
