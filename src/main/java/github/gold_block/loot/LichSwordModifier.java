package github.gold_block.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.gold_block.registry.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class LichSwordModifier extends LootModifier {

    public static final Codec<LichSwordModifier> CODEC =
            RecordCodecBuilder.create(instance -> LootModifier.codecStart(instance).apply(instance, LichSwordModifier::new));

    private static final int MIN_REMAINING_DURABILITY = 40;
    private static final int MAX_REMAINING_DURABILITY = 140;

    public LichSwordModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack sword = new ItemStack(ModItems.MAGIC_SWORD.get());
        sword.enchant(Enchantments.SMITE, 2);
        sword.enchant(Enchantments.UNBREAKING, 2);
        int remaining = Mth.nextInt(context.getRandom(), MIN_REMAINING_DURABILITY, MAX_REMAINING_DURABILITY);
        // Damage 是已经损耗的耐久，需求给的是剩余耐久。
        sword.setDamageValue(sword.getMaxDamage() - remaining);
        generatedLoot.add(sword);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
