package github.gold_block.item;

import github.gold_block.event.KnightHammerEvents;
import github.gold_block.util.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import twilightforest.util.TwilightItemTier;

import java.util.List;

public class KnightHammerItem extends SwordItem {

    public KnightHammerItem(Properties properties) {
        // 玩家基础伤害 1 + 材质伤害 3 + 参数 6 = 10；基础攻速 4 - 3.1 = 0.9。
        super(TwilightItemTier.KNIGHTMETAL, 6, -3.1F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {
            KnightHammerEvents.finishAttack(player, target);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    /** 禁止在附魔台为战锤添加横扫之刃。 */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.SWEEPING_EDGE && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    /** 拒绝含有横扫之刃的附魔书。 */
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return !EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.SWEEPING_EDGE)
                && super.isBookEnchantable(stack, book);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
