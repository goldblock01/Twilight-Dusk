package github.gold_block.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 供战利品加载事件追加条目，保留原池的条件、函数与抽取次数。 */
@Mixin(LootPool.class)
public interface LootPoolEntriesAccessor {
    @Accessor("entries")
    LootPoolEntryContainer[] twilight_dusk$getEntries();

    @Mutable
    @Accessor("entries")
    void twilight_dusk$setEntries(LootPoolEntryContainer[] entries);
}
