package github.gold_block.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.gold_block.registry.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class YetiBonesModifier extends LootModifier {

    public static final Codec<YetiBonesModifier> CODEC =
            RecordCodecBuilder.create(instance -> LootModifier.codecStart(instance).apply(instance, YetiBonesModifier::new));

    private static final int MIN_BONES = 6;
    private static final int MAX_BONES = 10;

    public YetiBonesModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(ModItems.EXTREME_COLD_ICE_BONES.get(),
                Mth.nextInt(context.getRandom(), MIN_BONES, MAX_BONES)));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
