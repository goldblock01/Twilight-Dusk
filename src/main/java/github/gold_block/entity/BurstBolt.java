package github.gold_block.entity;

import github.gold_block.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import twilightforest.entity.projectile.TFThrowable;

public class BurstBolt extends TFThrowable {

    private static final float EXPLOSION_POWER = 2.0F;

    public BurstBolt(EntityType<? extends BurstBolt> type, Level level) {
        super(type, level);
    }

    public BurstBolt(Level level, LivingEntity thrower) {
        super(ModEntities.BURST_BOLT.get(), level, thrower);
        this.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 1.5F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.makeTrail(ParticleTypes.FLAME, 1);
        }
    }

    @Override
    protected float getGravity() {
        return 0.001F;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.burst();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof BurstBolt) {
            return;
        }
        this.burst();
    }

    private void burst() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity owner = this.getOwner();
        serverLevel.explode(this, serverLevel.damageSources().explosion(owner, this), null,
                this.getX(), this.getY(), this.getZ(), EXPLOSION_POWER, false, Level.ExplosionInteraction.NONE);
        this.discard();
    }
}
