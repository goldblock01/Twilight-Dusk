package github.gold_block.twilightlootr.mixin;

import github.gold_block.twilightlootr.impl.BossChestHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.loot.TFLootTables;

@Mixin(TFLootTables.class)
public abstract class MixinTFLootTables {

    @Inject(
            method = "entityDropsIntoContainer(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void twilightlootr$entityDropsIntoContainer(
            LivingEntity entity, DamageSource source, BlockState blockContaining, BlockPos placement,
            CallbackInfo ci) {
        if (BossChestHandler.entityDropsIntoChest(entity, source, blockContaining, placement)) {
            ci.cancel();
        }
    }
}
