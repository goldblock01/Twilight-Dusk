package github.gold_block.entity;

import github.gold_block.registry.ModEntities;
import github.gold_block.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 具有额外目标穿透和较低重力的幻影骑士箭实体。 */
public class PhantomKnightArrow extends KnightArrow {
    /** 指示当前是否正在恢复已保存的穿透等级。 */
    private boolean restoringPierceLevel;

    /** 创建世界载入或网络同步使用的幻影箭。 */
    public PhantomKnightArrow(EntityType<? extends PhantomKnightArrow> type, Level level) {
        super(type, level);
        super.setPierceLevel((byte) 2);
    }

    /** 创建由生物射出的幻影箭，并设置三个目标的命中上限。 */
    public PhantomKnightArrow(Level level, LivingEntity shooter) {
        super(ModEntities.PHANTOM_KNIGHT_ARROW.get(), shooter, level);
        super.setPierceLevel((byte) 2);
    }

    /** 返回幻影箭的护甲减伤返还比例。 */
    @Override
    public double armorPenetration() {
        return 0.5D;
    }

    /** 返回幻影箭的附魔减伤返还比例。 */
    @Override
    public double enchantmentPenetration() {
        return 0.15D;
    }

    /** 将弩提供的穿透等级与箭自身的两个额外目标叠加。 */
    @Override
    public void setPierceLevel(byte level) {
        int effective = restoringPierceLevel || level <= 0 ? level : Math.min(127, level + 2);
        super.setPierceLevel((byte) effective);
    }

    /** 恢复最终穿透等级，保持存档中的目标命中上限。 */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        restoringPierceLevel = true;
        try {
            super.readAdditionalSaveData(tag);
        } finally {
            restoringPierceLevel = false;
        }
    }

    /** 将可拾取的幻影箭还原为对应物品。 */
    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.PHANTOM_KNIGHT_ARROW.get());
    }
}
