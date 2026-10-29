package github.gold_block.item;

import github.gold_block.entity.KnightArrow;
import github.gold_block.entity.PhantomKnightArrow;
import github.gold_block.util.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/** 为弓和弩提供两种骑士箭的物品入口。 */
public class KnightArrowItem extends ArrowItem {
    /** 决定物品创建普通骑士箭还是幻影骑士箭。 */
    private final boolean phantom;

    /** 保存箭矢种类并应用物品属性。 */
    public KnightArrowItem(boolean phantom, Properties properties) {
        super(properties);
        this.phantom = phantom;
    }

    /** 在弓或弩发射时创建对应的箭实体。 */
    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        return phantom ? new PhantomKnightArrow(level, shooter) : new KnightArrow(level, shooter);
    }

    /** 使无限附魔仍消耗此类箭矢。 */
    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return false;
    }

    /** 使用统一的 Shift 展开描述。 */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
