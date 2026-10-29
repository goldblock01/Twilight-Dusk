package github.gold_block.entity;

import github.gold_block.registry.ModEntities;
import github.gold_block.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 带有护甲与附魔减伤穿透比例的骑士箭实体。 */
public class KnightArrow extends AbstractArrow {
    /** 创建网络同步或世界载入使用的箭实体。 */
    public KnightArrow(EntityType<? extends KnightArrow> type, Level level) {
        super(type, level);
    }

    /** 创建由生物射出的骑士箭。 */
    public KnightArrow(Level level, LivingEntity shooter) {
        this(ModEntities.KNIGHT_ARROW.get(), shooter, level);
    }

    /** 为不同骑士箭类型设置射手、出生位置与拾取规则。 */
    protected KnightArrow(EntityType<? extends KnightArrow> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    /** 返回护甲减伤量中返还给箭矢伤害的比例。 */
    public double armorPenetration() {
        return 0.4D;
    }

    /** 返回附魔减伤量中返还给箭矢伤害的比例。 */
    public double enchantmentPenetration() {
        return 0.1D;
    }

    /** 将可拾取的骑士箭还原为对应物品。 */
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.KNIGHT_ARROW.get());
    }
}
