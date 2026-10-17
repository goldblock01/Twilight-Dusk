package github.gold_block.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import twilightforest.init.TFMobEffects;

public class FrostyPickItem extends PickaxeItem {

    private static final int FREEZE_TICKS = 60;
    private static final float HARD_BLOCK_THRESHOLD = 30.0F;
    private static final float HARD_BLOCK_BONUS = 1.5F;

    public FrostyPickItem(Properties properties) {
        super(Tiers.DIAMOND, 5, -2.9F, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        return state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO) >= HARD_BLOCK_THRESHOLD
                ? speed * HARD_BLOCK_BONUS
                : speed;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return TierSortingRegistry.isCorrectTierForDrops(this.getTier(), state);
        }
        return super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), FREEZE_TICKS, 0), attacker);
        return super.hurtEnemy(stack, target, attacker);
    }
}
