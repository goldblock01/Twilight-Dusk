package github.gold_block.twilightlootr.impl;

import github.gold_block.Config;
import github.gold_block.compat.LootrCompat;
import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import github.gold_block.twilightlootr.init.TLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import twilightforest.TFConfig;
import twilightforest.block.TFChestBlock;
import twilightforest.entity.boss.IBossLootBuffer;

public final class BossChestHandler {

    private BossChestHandler() {
    }

    public static boolean convertExistingChest(ServerLevel serverLevel, BlockPos pos, BlockState before,
                                               @Nullable ResourceLocation lootTable, long seed) {
        if (!LootrCompat.isLoaded() || !TLBlocks.BOSS_CHEST.isPresent() || !Config.twilightLootrEnabled()
                || !TFConfig.COMMON_CONFIG.bossDropChests.get()) {
            return true;
        }
        if (!serverLevel.isLoaded(pos)) {
            return false;
        }

        BlockState current = serverLevel.getBlockState(pos);
        if (current.equals(before)) {
            return false;
        }
        if (!(current.getBlock() instanceof TFChestBlock)
                || !(serverLevel.getBlockEntity(pos) instanceof Container container)) {
            return true;
        }

        NonNullList<ItemStack> backup = NonNullList.create();
        for (int i = 0; i < container.getContainerSize() && i < IBossLootBuffer.CONTAINER_SIZE; i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                backup.add(stack.copy());
            }
            container.setItem(i, ItemStack.EMPTY);
        }

        BlockState chest = TLBlocks.BOSS_CHEST.get().defaultBlockState()
                .setValue(ChestBlock.FACING, current.getValue(ChestBlock.FACING));
        if (serverLevel.setBlock(pos, chest, Block.UPDATE_ALL)
                && serverLevel.getBlockEntity(pos) instanceof TFLootrBossChestBlockEntity bossChest) {
            bossChest.setBossItems(lootTable == null ? backup : NonNullList.create());
            if (lootTable != null) {
                bossChest.setLootTable(lootTable, seed);
            }
        } else {
            for (ItemStack stack : backup) {
                Block.popResource(serverLevel, pos, stack);
            }
        }
        return true;
    }
}
