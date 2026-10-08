package github.gold_block.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import twilightforest.util.TwilightItemTier;

public interface FieryTool {

    int BURN_SECONDS = 15;

    static boolean ignites(ItemStack stack) {
        if (stack.getItem() instanceof FieryTool) {
            return true;
        }
        return stack.getItem() instanceof TieredItem tiered && tiered.getTier() == TwilightItemTier.FIERY;
    }

    static void burn(LivingEntity target) {
        if (!target.level().isClientSide() && !target.fireImmune()) {
            target.setSecondsOnFire(BURN_SECONDS);
        }
    }

    static void flameParticles(LivingEntity target) {
        if (!target.level().isClientSide()) {
            return;
        }
        for (int i = 0; i < 20; ++i) {
            double px = target.getX() + target.level().getRandom().nextFloat() * target.getBbWidth() * 2.0F - target.getBbWidth();
            double py = target.getY() + target.level().getRandom().nextFloat() * target.getBbHeight();
            double pz = target.getZ() + target.level().getRandom().nextFloat() * target.getBbWidth() * 2.0F - target.getBbWidth();
            target.level().addParticle(ParticleTypes.FLAME, px, py, pz, 0.02, 0.02, 0.02);
        }
    }
}
