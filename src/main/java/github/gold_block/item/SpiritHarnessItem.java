package github.gold_block.item;

import github.gold_block.entity.TowerSpirit;
import github.gold_block.registry.ModSounds;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

/** 保存挽具颜色；装备与装卸由高塔乐魂自身处理。 */
public class SpiritHarnessItem extends Item {
    private final DyeColor color;
    public SpiritHarnessItem(DyeColor color, Properties properties) { super(properties); this.color = color;
        // 发射器直接给前方成年个体装备，复用同一装备槽与掉落规则。
        DispenserBlock.registerBehavior(this,
                new DefaultDispenseItemBehavior() {
            @Override protected ItemStack execute(BlockSource source,
                    ItemStack stack) {
                var pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                for (var spirit : source.getLevel().getEntitiesOfClass(TowerSpirit.class,
                        new AABB(pos), entity -> entity.isAlive() && !entity.isBaby() && !entity.hasHarness())) {
                    spirit.setItemSlot(EquipmentSlot.CHEST, stack.copyWithCount(1));
                    spirit.setDropChance(EquipmentSlot.CHEST, 2.0F);
                    spirit.playSound(ModSounds.SPIRIT_HARNESS_EQUIP.get(), 1, 1);
                    stack.shrink(1); return stack;
                }
                return super.execute(source, stack);
            }
        });
    }
    public DyeColor color() { return color; }
}
