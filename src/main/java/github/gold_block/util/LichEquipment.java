package github.gold_block.util;

import github.gold_block.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import twilightforest.entity.boss.Lich;

/** 管理巫妖近战阶段的专属武器。 */
public final class LichEquipment {
    private LichEquipment() { }

    /** 仅替换本体第三阶段的金剑，并由独立战利品规则负责武器奖励。 */
    public static boolean shouldReplace(Lich lich, EquipmentSlot slot, ItemStack stack) {
        return !lich.level().isClientSide && !lich.isShadowClone() && lich.getPhase() == 3
                && slot == EquipmentSlot.MAINHAND && stack.is(Items.GOLDEN_SWORD);
    }

    public static ItemStack createSword() {
        ItemStack sword = new ItemStack(ModItems.MAGIC_SWORD.get());
        sword.enchant(Enchantments.SMITE, 2);
        sword.enchant(Enchantments.UNBREAKING, 2);
        return sword;
    }

    /** 读档恢复阶段状态后同步替换旧存档中的金剑。 */
    public static void restore(Lich lich) {
        if (shouldReplace(lich, EquipmentSlot.MAINHAND, lich.getMainHandItem())) {
            lich.setItemSlot(EquipmentSlot.MAINHAND, createSword());
            lich.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }
    }
}
