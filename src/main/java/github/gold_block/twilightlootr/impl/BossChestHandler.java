package github.gold_block.twilightlootr.impl;

import github.gold_block.Config;
import github.gold_block.compat.LootrCompat;
import github.gold_block.registry.ModItems;
import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import github.gold_block.twilightlootr.init.TLBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import twilightforest.TFConfig;
import twilightforest.block.TFChestBlock;
import twilightforest.entity.boss.IBossLootBuffer;
import twilightforest.init.TFItems;

public final class BossChestHandler {

    private static final float BURST_SCEPTER_CHANCE = 0.25F;

    private BossChestHandler() {
    }

    public static boolean convertExistingChest(ServerLevel serverLevel, BlockPos pos, BlockState before,
                                               @Nullable ResourceLocation lootTable, long seed, boolean lichChest) {
        boolean convert = LootrCompat.isLoaded() && TLBlocks.BOSS_CHEST.isPresent()
                && Config.twilightLootrEnabled() && TFConfig.COMMON_CONFIG.bossDropChests.get();
        if (!convert) {
            return !lichChest || replaceScepterInChest(serverLevel, pos, before);
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

        if (lichChest) {
            replaceScepter(backup, serverLevel.getRandom());
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

    private static boolean replaceScepterInChest(ServerLevel serverLevel, BlockPos pos, BlockState before) {
        if (!serverLevel.isLoaded(pos) || serverLevel.getBlockState(pos).equals(before)) {
            return false;
        }
        if (!(serverLevel.getBlockEntity(pos) instanceof Container container)) {
            return true;
        }

        NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, container.getItem(i));
        }
        replaceScepter(items, serverLevel.getRandom());
        for (int i = 0; i < items.size(); i++) {
            container.setItem(i, items.get(i));
        }
        container.setChanged();
        return true;
    }

    private static void replaceScepter(NonNullList<ItemStack> items, RandomSource random) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).is(TFItems.TWILIGHT_SCEPTER.get())) {
                if (random.nextFloat() < BURST_SCEPTER_CHANCE) {
                    items.set(i, new ItemStack(ModItems.BURST_SCEPTER.get()));
                }
                return;
            }
        }
    }
}
