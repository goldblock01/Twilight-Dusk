package github.gold_block.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import github.gold_block.entity.TowerSpirit;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** 高塔乐魂停留时使用完整坐标同步，使站立表面在两侧保持一致。 */
@Mixin(ServerEntity.class)
public abstract class TowerSpiritPrecisePositionMixin {
    @Shadow @Final private Entity entity;

    /** 将成年乐魂的停留状态并入完整位置包分支，保留原有同步条件。 */
    @ModifyExpressionValue(method = "sendChanges",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/server/level/ServerEntity;wasRiding:Z",
                    opcode = Opcodes.GETFIELD), require = 0)
    private boolean twilight_dusk$requiresPrecisePosition(boolean wasRiding) {
        return wasRiding || entity instanceof TowerSpirit spirit && !spirit.isBaby() && spirit.staysStill();
    }
}
