package github.gold_block.item;

import github.gold_block.util.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import twilightforest.init.TFBlocks;

import java.util.List;

public class TowerDestroyerItem extends AxeItem {

    /** 塔木类方块标签，用于识别塔木木板 */
    private static final TagKey<Block> TOWERWOOD = BlockTags.create(new ResourceLocation("twilightforest", "towerwood"));

    /** 挖掘塔木类方块时的速度倍率 */
    private static final float TOWERWOOD_SPEED_MULTIPLIER = 17.0F;

    public TowerDestroyerItem(Properties properties) {
        super(Tiers.DIAMOND, 6.0F, -3.0F, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        boolean towerwood = state.is(TOWERWOOD) || state.is(TFBlocks.ENCASED_TOWERWOOD.get());
        return towerwood ? speed * TOWERWOOD_SPEED_MULTIPLIER : speed;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
