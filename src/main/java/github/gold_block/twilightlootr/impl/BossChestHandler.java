package github.gold_block.twilightlootr.impl;

import github.gold_block.Config;
import github.gold_block.compat.LootrCompat;
import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import github.gold_block.twilightlootr.init.TLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import twilightforest.TFConfig;
import twilightforest.entity.boss.IBossLootBuffer;
import twilightforest.loot.TFLootTables;

public final class BossChestHandler {

    private BossChestHandler() {
    }

    public static boolean entityDropsIntoChest(
            LivingEntity entity, DamageSource source, BlockState incomingChest, BlockPos pos) {
        if (!LootrCompat.isLoaded() || !TLBlocks.BOSS_CHEST.isPresent() || !Config.twilightLootrEnabled()
                || !TFConfig.COMMON_CONFIG.bossDropChests.get()) {
            return false;
        }
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        BlockState newChest = TLBlocks.BOSS_CHEST.get().defaultBlockState()
                .setValue(ChestBlock.FACING, incomingChest.getValue(ChestBlock.FACING));

        BlockPos chestPos = pos;
        boolean placed = createChest(newChest, chestPos, serverLevel);
        if (!placed) {
            BlockPos.MutableBlockPos mutable = pos.mutable();
            for (int y = pos.getY(); y < serverLevel.getMaxBuildHeight(); y++) {
                mutable.setY(y);
                if (createChest(newChest, mutable, serverLevel)) {
                    placed = true;
                    chestPos = mutable.immutable();
                    break;
                }
            }
        }
        if (!placed || !(serverLevel.getBlockEntity(chestPos) instanceof TFLootrBossChestBlockEntity chest)) {
            return false;
        }

        chest.setLootTable(entity.getLootTable(), entity.getLootTableSeed());
        chest.setBossItems(NonNullList.create());
        return true;
    }

    public static <T extends LivingEntity & IBossLootBuffer> void depositDropsIntoChest(
            T boss, BlockState incomingChest, BlockPos pos, ServerLevel serverLevel) {
        if (!LootrCompat.isLoaded() || !TLBlocks.BOSS_CHEST.isPresent() || !Config.twilightLootrEnabled()) {
            IBossLootBuffer.depositDropsIntoChest(boss, incomingChest, pos, serverLevel);
            return;
        }

        BlockState newChest = TLBlocks.BOSS_CHEST.get().defaultBlockState()
                .setValue(ChestBlock.FACING, incomingChest.getValue(ChestBlock.FACING));

        BlockPos chestPos = pos;
        boolean placed = createChest(newChest, chestPos, serverLevel);
        if (!placed) {
            BlockPos.MutableBlockPos mutable = pos.mutable();
            for (int y = pos.getY(); y < serverLevel.getMaxBuildHeight(); y++) {
                mutable.setY(y);
                if (createChest(newChest, mutable, serverLevel)) {
                    placed = true;
                    chestPos = mutable.immutable();
                    break;
                }
            }
        }
        if (!placed || !(serverLevel.getBlockEntity(chestPos) instanceof TFLootrBossChestBlockEntity chest)) {
            IBossLootBuffer.depositDropsIntoChest(boss, incomingChest, pos, serverLevel);
            return;
        }

        chest.setLootTable(boss.getLootTable(), serverLevel.getRandom().nextLong());
        chest.setBossItems(boss.getItemStacks());
    }

    private static boolean createChest(BlockState chest, BlockPos pos, ServerLevel serverLevel) {
        return ((serverLevel.getBlockState(pos).is(chest.getBlock()) ||
                ((serverLevel.getBlockState(pos).canBeReplaced() || serverLevel.getBlockState(pos)
                        .getPistonPushReaction() != PushReaction.BLOCK) &&
                        serverLevel.getBlockEntity(pos) == null &&
                        serverLevel.setBlock(pos, chest, TFLootTables.DEFAULT_PLACE_FLAG))) &&
                serverLevel.getBlockState(pos).is(chest.getBlock()));
    }
}
