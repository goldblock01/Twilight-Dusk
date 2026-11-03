package github.gold_block.mixin;

import github.gold_block.event.ScytheSweepEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(Player.class)
public class PlayerScytheSweepMixin {
    /** 包住整次攻击，保证其他处理提前取消或抛错后也清理横扫上下文。 */
    @WrapMethod(method = "attack(Lnet/minecraft/world/entity/Entity;)V", require = 0)
    private void twilight_dusk$attackContext(Entity target, Operation<Void> original) {
        ScytheSweepEvents.beginAttack((Player) (Object) this, target);
        try {
            original.call(target);
        } finally {
            ScytheSweepEvents.endAttack();
        }
    }
}
