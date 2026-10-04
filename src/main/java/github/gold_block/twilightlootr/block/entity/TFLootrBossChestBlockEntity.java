package github.gold_block.twilightlootr.block.entity;

import github.gold_block.twilightlootr.impl.TFLootFiller;
import github.gold_block.twilightlootr.init.TLBlockEntities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;
import org.jetbrains.annotations.NotNull;

public class TFLootrBossChestBlockEntity extends LootrChestBlockEntity {

    private final ObjectArrayList<ItemStack> bossItems = new ObjectArrayList<>();

    public TFLootrBossChestBlockEntity(BlockPos pos, BlockState state) {
        super(TLBlockEntities.BOSS_CHEST.get(), pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.bossItems.clear();
        if (compound.contains("BossItems")) {
            ListTag list = compound.getList("BossItems", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                this.bossItems.add(ItemStack.of(list.getCompound(i)));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ListTag items = new ListTag();
        for (ItemStack stack : this.bossItems) {
            items.add(stack.save(new CompoundTag()));
        }
        compound.put("BossItems", items);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    public void unpackLootTable(Player player, Container inventory, ResourceLocation overrideTable, long seed) {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        TFLootFiller filler = new TFLootFiller(serverLevel, this.getBlockPos(), overrideTable, this.bossItems);
        filler.unpackLootTable(player, inventory, overrideTable, seed);
    }

    public void setBossItems(NonNullList<ItemStack> items) {
        this.bossItems.clear();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                this.bossItems.add(stack.copy());
            }
        }
        this.setChanged();
    }

    public ObjectArrayList<ItemStack> getBossItems() {
        return this.bossItems;
    }
}
