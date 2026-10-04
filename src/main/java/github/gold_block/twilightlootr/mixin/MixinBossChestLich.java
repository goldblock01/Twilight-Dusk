package github.gold_block.twilightlootr.mixin;

import github.gold_block.twilightlootr.impl.BossChestHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import twilightforest.entity.boss.IBossLootBuffer;
import twilightforest.entity.boss.Lich;

@Mixin(Lich.class)
public abstract class MixinBossChestLich {

    @Redirect(
            method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ltwilightforest/entity/boss/IBossLootBuffer;depositDropsIntoChest(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/server/level/ServerLevel;)V",
                    remap = false
            ),
            remap = false
    )
    private <T extends LivingEntity & IBossLootBuffer> void twilightlootr$depositDropsIntoChest(
            T boss, BlockState incomingChest, BlockPos pos, ServerLevel serverLevel) {
        BossChestHandler.depositDropsIntoChest(boss, incomingChest, pos, serverLevel);
    }
}
