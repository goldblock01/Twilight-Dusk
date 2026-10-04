package github.gold_block.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PhantomSwordItem extends SwordItem implements PhantomEnchantRestriction {

    public PhantomSwordItem(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.twilightforest.knightmetal_sword.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.twilightforest.phantom_armor.desc").withStyle(ChatFormatting.GRAY));
    }
}
