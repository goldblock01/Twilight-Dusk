package github.gold_block.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * 往指定的战利品表里「加」一样东西，产出仍由原表决定（不动原表的 JSON）。
 *
 * <p>用途场景：给别的模组的 Boss 战利品表追加一件材料。以暮色森林的冰雪女王为例，
 * 它的 {@code die()} 会走 {@code TFLootTables.entityDropsIntoContainer}，
 * 把实体战利品表 {@code twilightforest:entities/snow_queen} 直接 {@code fill} 进 Boss 箱；
 * 同时 {@code shouldDropLoot()} 在开箱模式下返回 {@code false}，脚下不掉落物。
 * 因此想让材料出现在箱子里，唯一正确的落点是**这张表**，不是死亡事件。</p>
 *
 * <p>参数全部由 JSON 给：{@code item}（物品 id）＋ {@code count}（数量，可选，默认 1）。
 * 对哪张表生效由 {@code conditions} 里的 {@code forge:loot_table_id} 决定。
 * 三段式（逻辑 / 序列化器 / 挂钩数据）缺一段就静默不生效，见
 * {@code registry/ModLootModifiers} 与 {@code data/forge/loot_modifiers/global_loot_modifiers.json}。</p>
 */
public class AddItemModifier extends LootModifier {

    public static final Codec<AddItemModifier> CODEC = RecordCodecBuilder.create(instance ->
            LootModifier.codecStart(instance).and(instance.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(AddItemModifier::item),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(AddItemModifier::count)
            )).apply(instance, (conditions, item, count) -> new AddItemModifier(conditions, item, count)));

    private final Item item;
    private final int count;

    public AddItemModifier(LootItemCondition[] conditions, Item item, int count) {
        super(conditions);
        this.item = item;
        this.count = count;
    }

    public Item item() {
        return this.item;
    }

    public int count() {
        return this.count;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.add(new ItemStack(this.item, this.count));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
