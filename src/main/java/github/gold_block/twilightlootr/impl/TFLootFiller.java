package github.gold_block.twilightlootr.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.api.LootFiller;
import noobanidus.mods.lootr.api.LootrAPI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TFLootFiller implements LootFiller {

    private final ServerLevel level;
    private final BlockPos pos;
    private final ResourceLocation table;
    private final ObjectArrayList<ItemStack> bossUniqueItems;

    public TFLootFiller(ServerLevel level, BlockPos pos, ResourceLocation table,
                        ObjectArrayList<ItemStack> bossUniqueItems) {
        this.level = level;
        this.pos = pos;
        this.table = table;
        this.bossUniqueItems = bossUniqueItems;
    }

    @Override
    public void unpackLootTable(@NotNull Player player, Container inventory, ResourceLocation overrideTable, long seed) {
        ResourceLocation actual = overrideTable != null ? overrideTable : this.table;
        LootTable lootTable = actual == null
                ? LootTable.EMPTY
                : this.level.getServer().getLootData().getLootTable(actual);

        ObjectArrayList<ItemStack> items = new ObjectArrayList<>();
        if (lootTable != LootTable.EMPTY) {
            LootParams.Builder builder = new LootParams.Builder(this.level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.pos));
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            items.addAll(lootTable.getRandomItems(builder.create(LootContextParamSets.CHEST)));
        }
        if (this.bossUniqueItems != null) {
            for (ItemStack stack : this.bossUniqueItems) {
                items.add(stack.copy());
            }
        }

        RandomSource random = RandomSource.create(LootrAPI.getLootSeed(seed));
        List<Integer> slots = availableSlots(inventory.getContainerSize(), random);
        shuffle(items, random);

        for (ItemStack stack : items) {
            if (slots.isEmpty()) {
                return;
            }
            inventory.setItem(slots.remove(slots.size() - 1), stack.isEmpty() ? ItemStack.EMPTY : stack);
        }
    }

    private static List<Integer> availableSlots(int size, RandomSource random) {
        ObjectArrayList<Integer> list = new ObjectArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        Util.shuffle(list, random);
        return list;
    }

    private static void shuffle(ObjectArrayList<ItemStack> items, RandomSource random) {
        for (int i = items.size(); i > 1; i--) {
            int j = random.nextInt(i);
            ItemStack a = items.get(i - 1);
            items.set(i - 1, items.get(j));
            items.set(j, a);
        }
    }
}
