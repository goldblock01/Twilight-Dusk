package github.gold_block.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;

public interface FieryTool {

    int BURN_SECONDS = 15;

    static void burn(LivingEntity target) {
        if (!target.level().isClientSide()) {
            if (!target.fireImmune()) {
                target.setSecondsOnFire(BURN_SECONDS);
            }
        } else {
            for (int i = 0; i < 20; ++i) {
                double px = target.getX() + target.level().getRandom().nextFloat() * target.getBbWidth() * 2.0F - target.getBbWidth();
                double py = target.getY() + target.level().getRandom().nextFloat() * target.getBbHeight();
                double pz = target.getZ() + target.level().getRandom().nextFloat() * target.getBbWidth() * 2.0F - target.getBbWidth();
                target.level().addParticle(ParticleTypes.FLAME, px, py, pz, 0.02, 0.02, 0.02);
            }
        }
    }

    default void igniteTarget(LivingEntity target) {
        burn(target);
    }
}
