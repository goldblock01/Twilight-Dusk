package github.gold_block.entity;

import github.gold_block.registry.ModSounds;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** 成年高塔善灵：无攻击行为的飞行生物，保留活动中心和环境回血。 */
public class TowerSpirit extends PathfinderMob {
    private static final net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> AVOIDS =
            net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                    new net.minecraft.resources.ResourceLocation("twilight_dusk", "tower_spirit_avoids"));
    private int stillTimeout;

    public TowerSpirit(EntityType<? extends TowerSpirit> type, Level level) {
        super(type, level);
        this.moveControl = new SpiritMoveControl();
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 16.0).add(Attributes.FLYING_SPEED, 0.05)
                .add(Attributes.MOVEMENT_SPEED, 0.05);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new FloatGoal(this) {
            @Override public boolean canUse() { return !staysStill() && super.canUse(); }
        });
        this.goalSelector.addGoal(4, new SnowballTemptGoal());
        this.goalSelector.addGoal(5, new WanderGoal());
    }

    public boolean staysStill() { return this.stillTimeout > 0; }

    /** 玩家位于头顶时短暂停留，避免生物继续移动；本体不提供骑乘。 */
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;
        if (this.stillTimeout > 0 && this.tickCount > 60) this.stillTimeout--;
        AABB box = this.getBoundingBox();
        AABB above = new AABB(box.minX - 1, box.maxY, box.minZ - 1,
                box.maxX + 1, box.maxY + box.getYsize() / 2, box.maxZ + 1);
        if (!this.level().getEntitiesOfClass(Player.class, above, p -> !p.isSpectator()).isEmpty()) {
            this.stillTimeout = 10;
        }
        if (this.staysStill()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.getNavigation().stop();
            this.setYRot(this.getYRot() - wrapQuarterTurn(this.getYRot()));
        }
        // 活动中心在脱离缓冲范围后重设，避免外力移动后持续返回旧位置。
        if (!this.isLeashed() && (!this.hasRestriction()
                || !this.getRestrictCenter().closerThan(this.blockPosition(), 80))) {
            this.restrictTo(this.blockPosition(), 64);
        }
        if (this.isAlive() && this.hurtTime == 0 && this.getHealth() < this.getMaxHealth()) {
            BlockPos pos = this.blockPosition();
            boolean precipitation = this.level().isRaining() && this.level().canSeeSky(pos)
                    && this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) <= pos.getY()
                    && this.level().getBiome(pos).value().getPrecipitationAt(pos)
                    != net.minecraft.world.level.biome.Biome.Precipitation.NONE;
            boolean fast = this.level().dimensionType().hasSkyLight() && (this.getY() >= 192.0 || precipitation);
            if (this.tickCount % (fast ? 20 : 600) == 0) this.heal(1.0F);
        }
    }

    private static float wrapQuarterTurn(float angle) {
        float wrapped = angle % 90.0F;
        if (wrapped >= 45) wrapped -= 90;
        if (wrapped < -45) wrapped += 90;
        return wrapped;
    }

    /** 飞行不承受重力和坠落伤害，速度按惯性逐步衰减。 */
    @Override
    public void travel(Vec3 input) {
        if (this.isEffectiveAi()) {
            this.moveRelative((float) this.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F, input);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        }
        this.calculateEntityAnimation(false);
    }

    @Override protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) { }
    @Override public boolean onClimbable() { return false; }
    @Override protected SoundEvent getAmbientSound() { return ModSounds.TOWER_SPIRIT_AMBIENT.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return ModSounds.TOWER_SPIRIT_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.TOWER_SPIRIT_DEATH.get(); }
    @Override public SoundSource getSoundSource() { return SoundSource.NEUTRAL; }
    @Override public float getVoicePitch() { return 1.0F; }
    @Override protected boolean shouldStayCloseToLeashHolder() { return false; }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override public int getMaxSpawnClusterSize() { return 1; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("StillTimeout", this.stillTimeout);
        if (this.hasRestriction()) tag.putLong("WanderCenter", this.getRestrictCenter().asLong());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.stillTimeout = Mth.clamp(tag.getInt("StillTimeout"), 0, 10);
        if (tag.contains("WanderCenter")) this.restrictTo(BlockPos.of(tag.getLong("WanderCenter")), 64);
        this.setNoGravity(true);
    }

    /** 雪球吸引直接驱动飞行，保持七格接近距离，不依赖地面寻路。 */
    private class SnowballTemptGoal extends Goal {
        private Player player;
        SnowballTemptGoal() { this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        private boolean holdsSnowball(Player p) {
            return !p.isSpectator() && (p.getMainHandItem().is(Items.SNOWBALL) || p.getOffhandItem().is(Items.SNOWBALL));
        }
        @Override public boolean canUse() {
            if (staysStill()) return false;
            this.player = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(16), this::holdsSnowball)
                    .stream().filter(p -> distanceToSqr(p) <= 256)
                    .min(java.util.Comparator.comparingDouble(p -> distanceToSqr(p))).orElse(null);
            return this.player != null;
        }
        @Override public boolean canContinueToUse() {
            return !staysStill() && this.player != null && this.player.isAlive()
                    && this.holdsSnowball(this.player) && distanceToSqr(this.player) <= 256;
        }
        @Override public void tick() {
            getLookControl().setLookAt(this.player, 10, 40);
            if (distanceToSqr(this.player) > 49) {
                moveControl.setWantedPosition(this.player.getX(), this.player.getY() + 0.5, this.player.getZ(), 1);
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.5));
                moveControl.setWantedPosition(getX(), getY(), getZ(), 1);
            }
        }
        @Override public void stop() { this.player = null; }
    }

    /** 每次最多尝试六十四个空中目标，优先靠近地面或墙面。 */
    private class WanderGoal extends Goal {
        WanderGoal() { this.setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() {
            if (staysStill()) return false;
            if (!moveControl.hasWanted()) return true;
            double distance = new Vec3(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ())
                    .distanceToSqr(position());
            return distance < 1 || distance > 3600;
        }
        @Override public boolean canContinueToUse() { return false; }
        @Override public void start() {
            Vec3 target = position();
            for (int i = 0; i < 64; i++) {
                Vec3 candidate = position().add((random.nextFloat() * 2 - 1) * 16,
                        (random.nextFloat() * 2 - 1) * 16, (random.nextFloat() * 2 - 1) * 16);
                if (hasRestriction() && !isWithinRestriction(BlockPos.containing(candidate))) continue;
                target = candidate;
                BlockPos pos = BlockPos.containing(candidate);
                if (!level().isEmptyBlock(pos)) continue;
                boolean nearBlock = false;
                for (var direction : net.minecraft.core.Direction.values()) {
                    for (int n = 1; n < 16; n++) {
                        if (!level().isEmptyBlock(pos.relative(direction, n))) { nearBlock = true; break; }
                    }
                    if (nearBlock) {
                    moveControl.setWantedPosition(target.x, target.y, target.z, 1);
                    return;
                }
                }
                if (nearBlock) {
                    moveControl.setWantedPosition(target.x, target.y, target.z, 1);
                    return;
                }
            }
            BlockPos pos = BlockPos.containing(target);
            int surface = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
            if (surface < target.y && surface > level().getMinBuildHeight()) {
                target = new Vec3(target.x, getY() - Math.abs(getY() - target.y), target.z);
            }
            moveControl.setWantedPosition(target.x, target.y, target.z, 1);
        }
    }

    /** 按飞行速度周期加速，沿途检测体积碰撞并避开流体及危险方块。 */
    private class SpiritMoveControl extends MoveControl {
        private int floatDuration;
        SpiritMoveControl() { super(TowerSpirit.this); }
        @Override public void tick() {
            if (staysStill()) { this.operation = Operation.WAIT; setDeltaMovement(Vec3.ZERO); return; }
            if (this.operation != Operation.MOVE_TO || this.floatDuration-- > 0) return;
            this.floatDuration += random.nextInt(5) + 2;
            Vec3 delta = new Vec3(this.wantedX - getX(), this.wantedY - getY(), this.wantedZ - getZ());
            if (delta.lengthSqr() < 1) { this.operation = Operation.WAIT; return; }
            Vec3 step = delta.normalize();
            AABB box = getBoundingBox();
            for (int i = 1; i <= Mth.ceil(delta.length()); i++) {
                AABB next = box.move(step.scale(i));
                if (!level().noCollision(TowerSpirit.this, next)) { this.operation = Operation.WAIT; return; }
                for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(next.minX, next.minY, next.minZ),
                        BlockPos.containing(next.maxX, next.maxY, next.maxZ))) {
                    var state = level().getBlockState(pos);
                    if (!state.getFluidState().isEmpty() || state.is(AVOIDS)) {
                        this.operation = Operation.WAIT; return;
                    }
                }
            }
            setDeltaMovement(getDeltaMovement().add(step.scale(getAttributeValue(Attributes.FLYING_SPEED) * 5 / 3)));
            float yaw = -(float) Mth.atan2(delta.x, delta.z) * Mth.RAD_TO_DEG;
            setYRot(yaw);
            yBodyRot = yaw;
            yHeadRot = yaw;
        }
    }
}
