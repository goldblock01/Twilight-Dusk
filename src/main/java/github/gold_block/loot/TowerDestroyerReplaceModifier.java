package github.gold_block.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.gold_block.registry.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import twilightforest.init.TFItems;

public class TowerDestroyerReplaceModifier extends LootModifier {

    public static final Codec<TowerDestroyerReplaceModifier> CODEC =
            RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).apply(inst, TowerDestroyerReplaceModifier::new));

    /** 塔钥匙被替换为高塔摧毁者的概率 */
    private static final float REPLACE_CHANCE = 0.5F;

    public TowerDestroyerReplaceModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (stack.is(TFItems.TOWER_KEY.get()) && context.getRandom().nextFloat() < REPLACE_CHANCE) {
                generatedLoot.set(i, createTowerDestroyer());
            }
        }
        return generatedLoot;
    }

    /** 生成一把自带效率 IV、时运 II、耐久 III 的高塔摧毁者 */
    private static ItemStack createTowerDestroyer() {
        ItemStack stack = new ItemStack(ModItems.TOWER_DESTROYER.get());
        stack.enchant(Enchantments.BLOCK_EFFICIENCY, 4);
        stack.enchant(Enchantments.BLOCK_FORTUNE, 2);
        stack.enchant(Enchantments.UNBREAKING, 3);
        return stack;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
