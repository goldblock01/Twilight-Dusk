package github.gold_block.entity;

import github.gold_block.item.SpiritHarnessItem;
import github.gold_block.registry.ModEntities;
import github.gold_block.registry.ModSounds;
import java.util.Comparator;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** 高塔乐魂的年龄成长、飞行与四人挽具骑乘。 */
public class TowerSpirit extends Animal {
    private static final TagKey<Block> AVOIDS =
            TagKey.create(Registries.BLOCK,
                    new ResourceLocation("twilight_dusk", "tower_spirit_avoids"));
    private int stillTimeout;
    private boolean ascending;
    private boolean tickingAge;

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
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0) {
            @Override public boolean canUse() { return isBaby() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isBaby() && super.canContinueToUse(); }
        });
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25,
                Ingredient.of(Items.SNOWBALL, twilightforest.init.TFItems.CARMINITE.get(),
                        twilightforest.init.TFBlocks.CARMINITE_BLOCK.get().asItem()), false) {
            @Override public boolean canUse() { return isBaby() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isBaby() && super.canContinueToUse(); }
        });
        this.goalSelector.addGoal(4, new BabyFollowGoal(false));
        this.goalSelector.addGoal(5, new BabyFollowGoal(true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0) {
            @Override public boolean canUse() { return isBaby() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isBaby() && super.canContinueToUse(); }
        });
        this.goalSelector.addGoal(3, new FloatGoal(this) {
            @Override public boolean canUse() { return !isBaby() && !isVehicle() && !staysStill() && super.canUse(); }
        });
        this.goalSelector.addGoal(4, new SnowballTemptGoal());
        this.goalSelector.addGoal(5, new WanderGoal());
    }

    private static final EntityDataAccessor<Boolean> DATA_STILL =
            SynchedEntityData.defineId(TowerSpirit.class, EntityDataSerializers.BOOLEAN);
    @Override protected void defineSynchedData() {
        super.defineSynchedData(); entityData.define(DATA_STILL, false);
    }
    /** 年龄符号区分幼体与成年，缺省年龄为成年。 */
    @Override public AgeableMob getBreedOffspring(ServerLevel level,
            AgeableMob partner) { return ModEntities.TOWER_SPIRIT.get().create(level); }
    @Override public boolean canFallInLove() { return false; }
    @Override public boolean isFood(ItemStack stack) {
        return false;
    }
    @Override public boolean canBreatheUnderwater() { return isBaby() || super.canBreatheUnderwater(); }
    @Override public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(isBaby() ? 0.95F : 4.0F, isBaby() ? 0.95F : 4.0F);
    }
    @Override protected float getStandingEyeHeight(Pose pose,
            EntityDimensions dimensions) { return dimensions.height * 0.65F; }
    @Override protected Component getTypeName() {
        return Component.translatable(isBaby()
                ? "entity.twilight_dusk.tower_ghastling" : "entity.twilight_dusk.tower_spirit");
    }
    @Override protected PathNavigation createNavigation(Level level) {
        var navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false); navigation.setCanFloat(true); navigation.setCanPassDoors(true);
        return navigation;
    }
    @Override protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.moveControl = isBaby() ? new BabyMoveControl() : new SpiritMoveControl();
        refreshDimensions();
    }
    /** 幼体在生物更新期间保持年龄，成长仅由砷铅铁块交互触发。 */
    @Override public void setAge(int age) {
        if (tickingAge && getAge() < 0) return;
        super.setAge(age);
    }
    /** 普通食物与年龄加速接口不推进幼体成长。 */
    @Override public void ageUp(int seconds, boolean forced) { }
    /** 用既有胸部装备槽承载挽具，复用装备同步与存档，完整保留物品数据。 */
    public boolean hasHarness() { return getItemBySlot(EquipmentSlot.CHEST).getItem()
            instanceof SpiritHarnessItem; }
    public int getHarnessColor() {
        return hasHarness() ? ((SpiritHarnessItem)
                getItemBySlot(EquipmentSlot.CHEST).getItem()).color().getId() : -1;
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (isBaby() && stack.is(twilightforest.init.TFBlocks.CARMINITE_BLOCK.get().asItem())) {
            if (level() instanceof ServerLevel server) {
                setAge(0);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                server.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                        getX(), getY() + getBbHeight() / 2, getZ(), 15, 0.5, 0.5, 0.5, 0.02);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!isBaby() && stack.getItem() instanceof SpiritHarnessItem && !hasHarness()) {
            if (!level().isClientSide) {
                setItemSlot(EquipmentSlot.CHEST, stack.copyWithCount(1));
                setDropChance(EquipmentSlot.CHEST, 2.0F);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                playSound(ModSounds.SPIRIT_HARNESS_EQUIP.get(), 1, 1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!isBaby() && hasHarness() && stack.is(Items.SHEARS)) {
            if (isVehicle()) return InteractionResult.PASS;
            if (!level().isClientSide) {
                spawnAtLocation(getItemBySlot(EquipmentSlot.CHEST));
                setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
                stack.hurtAndBreak(1, player, owner -> owner.broadcastBreakEvent(hand));
                playSound(ModSounds.SPIRIT_HARNESS_UNEQUIP.get(), 1, 1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!isBaby() && hasHarness() && !player.isSecondaryUseActive() && canAddPassenger(player)) {
            if (!level().isClientSide) player.startRiding(this);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }
    @Override protected boolean canAddPassenger(Entity passenger) {
        return !isBaby() && hasHarness() && passenger instanceof Player && getPassengers().size() < 4;
    }
    @Override public LivingEntity getControllingPassenger() {
        return hasHarness() && !isBaby() && !staysStill() && getFirstPassenger() instanceof Player player ? player : null;
    }
    @Override protected void addPassenger(Entity passenger) {
        boolean first = !isVehicle(); super.addPassenger(passenger);
        stillTimeout = 0;
        entityData.set(DATA_STILL, false);
        if (!level().isClientSide && first) playSound(ModSounds.SPIRIT_GOGGLES_DOWN.get(), 1, 1);
    }
    /** 乘客离开时立即停止两侧运动，并进入头顶停留缓冲。 */
    @Override protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        stillTimeout = 10;
        entityData.set(DATA_STILL, true);
        stopHoverMotion();
        if (!level().isClientSide && !isVehicle()) playSound(ModSounds.SPIRIT_GOGGLES_UP.get(), 1, 1);
    }

    /** 停留时清空驾驶输入、惯性和导航目标，保留服务端位置校正。 */
    private void stopHoverMotion() {
        ascending = false;
        setXxa(0); setYya(0); setZza(0);
        setDeltaMovement(Vec3.ZERO);
        getNavigation().stop();
        moveControl.setWantedPosition(getX(), getY(), getZ(), 0);
    }

    /** 停留状态同步到达时清除客户端剩余的移动状态。 */
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_STILL.equals(key) && staysStill()) stopHoverMotion();
    }
    /** 四个座位按身体朝向旋转，驾驶位始终为乘客列表的第一位。 */
    @Override protected void positionRider(Entity passenger,
            Entity.MoveFunction move) {
        int seat = getPassengers().indexOf(passenger);
        if (seat < 0) return;
        double[][] offsets = {{0, 1.7}, {-1.7, 0}, {0, -1.7}, {1.7, 0}};
        // 超出标准座位的乘客置于中心，仍由乘客系统管理其生命周期。
        Vec3 offset = new Vec3(seat < offsets.length ? offsets[seat][0] : 0, 4.0,
                seat < offsets.length ? offsets[seat][1] : 0).yRot(-getYRot() * Mth.DEG_TO_RAD);
        move.accept(passenger, getX() + offset.x, getY() + offset.y + passenger.getMyRidingOffset(), getZ() + offset.z);
    }
    @Override public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(getX(), getY() + getBbHeight() + 0.05, getZ());
    }
    @Override public boolean canBeCollidedWith() { return !isBaby() && isAlive(); }
    @Override public boolean canCollideWith(Entity other) {
        return !isBaby() && other instanceof Player && !isPassengerOfSameVehicle(other);
    }
    /** 上升输入仅供本地驾驶计算使用，位置同步由原版座驾移动通道处理。 */
    public void setAscending(boolean ascending) { this.ascending = ascending; }
    @Override protected Vec3 getRiddenInput(Player player, Vec3 input) {
        float forward = player.zza;
        float pitch = player.getXRot() * Mth.DEG_TO_RAD;
        double multiplier = forward < 0 ? -0.5 : forward > 0 ? 1 : 0;
        double vertical = -Math.sin(pitch) * multiplier + (ascending ? 0.5 : 0);
        return new Vec3(player.xxa, vertical, Math.cos(pitch) * multiplier)
                .scale(3.9F * getAttributeValue(Attributes.MOVEMENT_SPEED));
    }
    @Override protected float getRiddenSpeed(Player player) { return (float) getAttributeValue(Attributes.FLYING_SPEED) * 5 / 3; }
    @Override protected void tickRidden(Player player, Vec3 input) {
        float yaw = getYRot() + Mth.wrapDegrees(player.getYRot() - getYRot()) * 0.08F;
        setRot(yaw, player.getXRot() * 0.5F); yBodyRot = yaw; yHeadRot = yaw;
    }

    public boolean staysStill() { return entityData.get(DATA_STILL); }

    /** 头顶站立者触发停留，已登乘者不重复触发停留。 */
    @Override
    public void aiStep() {
        tickingAge = true;
        try { super.aiStep(); } finally { tickingAge = false; }
        if (this.level().isClientSide) return;
        if (this.stillTimeout > 0 && this.tickCount > 60) this.stillTimeout--;
        AABB box = this.getBoundingBox();
        AABB above = new AABB(box.minX - 1, box.maxY, box.minZ - 1,
                box.maxX + 1, box.maxY + box.getYsize() / 2, box.maxZ + 1);
        if (!isBaby() && !this.level().getEntitiesOfClass(Player.class, above, p -> !p.isSpectator() && p.getRootVehicle() != this).isEmpty()) {
            this.stillTimeout = 10;
        }
        entityData.set(DATA_STILL, this.stillTimeout > 0);
        if (this.staysStill()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.getNavigation().stop();
            this.setYRot(this.getYRot() - wrapQuarterTurn(this.getYRot()));
        }
        // 活动中心在脱离缓冲范围后重设，避免外力移动后持续返回旧位置。
        if (!this.isLeashed() && !this.isVehicle() && (!this.hasRestriction()
                || !this.getRestrictCenter().closerThan(this.blockPosition(), 80))) {
            this.restrictTo(this.blockPosition(), 64);
        }
        if (this.isAlive() && this.hurtTime == 0 && this.getHealth() < this.getMaxHealth()) {
            BlockPos pos = this.blockPosition();
            boolean precipitation = this.level().isRaining() && this.level().canSeeSky(pos)
                    && this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) <= pos.getY()
                    && this.level().getBiome(pos).value().getPrecipitationAt(pos)
                    != Biome.Precipitation.NONE;
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
        if (!isBaby() && staysStill()) {
            setDeltaMovement(Vec3.ZERO);
            calculateEntityAnimation(false);
            return;
        }
        if (this.isControlledByLocalInstance()) {
            this.moveRelative((float) this.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F, input);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.91));
        }
        this.calculateEntityAnimation(false);
    }

    @Override protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) { }
    @Override public boolean onClimbable() { return false; }
    @Override protected SoundEvent getAmbientSound() { return isBaby() ? ModSounds.GHASTLING_AMBIENT.get() : ModSounds.TOWER_SPIRIT_AMBIENT.get(); }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return isBaby() ? ModSounds.GHASTLING_HURT.get() : ModSounds.TOWER_SPIRIT_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return isBaby() ? ModSounds.GHASTLING_DEATH.get() : ModSounds.TOWER_SPIRIT_DEATH.get(); }
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
        entityData.set(DATA_STILL, this.stillTimeout > 0);
        this.moveControl = isBaby() ? new BabyMoveControl() : new SpiritMoveControl();
    }

    /** 幼体优先跟随可见玩家，其次跟随成年同类，三格内停留。 */
    private class BabyFollowGoal extends Goal {
        private final boolean adult;
        private LivingEntity target;
        BabyFollowGoal(boolean adult) {
            this.adult = adult;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }
        @Override public boolean canUse() {
            if (!isBaby() || isLeashed() || isPassenger()) return false;
            var box = getBoundingBox().inflate(16);
            target = adult
                    ? level().getEntitiesOfClass(TowerSpirit.class, box,
                            spirit -> spirit != TowerSpirit.this && spirit.isAlive() && !spirit.isBaby()
                                    && distanceToSqr(spirit) <= 256 && getSensing().hasLineOfSight(spirit))
                            .stream().min(Comparator.comparingDouble(TowerSpirit.this::distanceToSqr)).orElse(null)
                    : level().getEntitiesOfClass(Player.class, box,
                            player -> player.isAlive() && !player.isSpectator() && distanceToSqr(player) <= 256
                                    && getSensing().hasLineOfSight(player))
                            .stream().min(Comparator.comparingDouble(TowerSpirit.this::distanceToSqr)).orElse(null);
            return target != null && distanceToSqr(target) > 9;
        }
        @Override public boolean canContinueToUse() {
            return isBaby() && !isLeashed() && !isPassenger() && target != null && target.isAlive()
                    && (!(target instanceof Player player) || !player.isSpectator())
                    && distanceToSqr(target) > 9 && distanceToSqr(target) <= 256
                    && getSensing().hasLineOfSight(target);
        }
        @Override public void tick() {
            getLookControl().setLookAt(target, 10, 40);
            getNavigation().moveTo(target, 1.1);
        }
        @Override public void stop() {
            target = null;
            getNavigation().stop();
        }
    }

    /** 幼体沿三维寻路目标平滑加速，抵达或取消目标后消除输入并减速悬停。 */
    private class BabyMoveControl extends MoveControl {
        BabyMoveControl() { super(TowerSpirit.this); }
        @Override public void tick() {
            setXxa(0); setYya(0); setZza(0);
            if (operation != Operation.MOVE_TO || isPassenger()) {
                setDeltaMovement(getDeltaMovement().scale(0.8));
                return;
            }
            operation = Operation.WAIT;
            Vec3 delta = new Vec3(wantedX - getX(), wantedY - getY(), wantedZ - getZ());
            if (delta.lengthSqr() < 0.04) {
                setDeltaMovement(getDeltaMovement().scale(0.5));
                return;
            }
            double speed = getAttributeValue(Attributes.FLYING_SPEED) * speedModifier * 3;
            Vec3 velocity = delta.normalize().scale(Math.min(speed, delta.length()));
            setDeltaMovement(getDeltaMovement().lerp(velocity, 0.25));
            float yaw = -(float) Mth.atan2(delta.x, delta.z) * Mth.RAD_TO_DEG;
            setYRot(getYRot() + Mth.wrapDegrees(yaw - getYRot()) * 0.2F);
            yBodyRot = getYRot(); yHeadRot = getYRot();
        }
    }

    /** 三种诱引物品共享判定，持任意一种均可吸引幼体和成年个体。 */
    private boolean isTemptingFood(ItemStack stack) {
        return stack.is(Items.SNOWBALL) || stack.is(twilightforest.init.TFItems.CARMINITE.get())
                || stack.is(twilightforest.init.TFBlocks.CARMINITE_BLOCK.get().asItem());
    }

    /** 成年坐骑的第三人称基础距离，随实体宽度放大。 */
    public double getPassengerCameraDistance() { return Math.max(8.0, getBbWidth() * 2.0); }

    /** 雪球吸引直接驱动飞行，保持七格接近距离，不依赖地面寻路。 */
    private class SnowballTemptGoal extends Goal {
        private Player player;
        SnowballTemptGoal() { this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        /** 任一手持诱引食物即可生效，未装挽具时也接受挽具诱引。 */
        private boolean holdsSnowball(Player p) {
            return p.isAlive() && !p.isSpectator() && (isTemptingFood(p.getMainHandItem())
                    || isTemptingFood(p.getOffhandItem()) || !hasHarness()
                    && (p.getMainHandItem().getItem() instanceof SpiritHarnessItem
                    || p.getOffhandItem().getItem() instanceof SpiritHarnessItem));
        }
        @Override public boolean canUse() {
            if (isBaby() || isVehicle() || staysStill()) return false;
            this.player = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(16), this::holdsSnowball)
                    .stream().filter(p -> distanceToSqr(p) <= 256)
                    .min(Comparator.comparingDouble(p -> distanceToSqr(p))).orElse(null);
            return this.player != null;
        }
        @Override public boolean canContinueToUse() {
            return !isBaby() && !isVehicle() && !staysStill() && this.player != null && this.player.isAlive()
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
            if (isBaby() || isVehicle() || staysStill()) return false;
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
                BlockPos pos = BlockPos.containing(candidate);
                if (!level().hasChunkAt(pos) || !level().isEmptyBlock(pos)) continue;
                target = candidate;
                boolean nearBlock = false;
                for (var direction : Direction.values()) {
                    for (int n = 1; n < 16; n++) {
                        BlockPos neighbor = pos.relative(direction, n);
                        if (!level().hasChunkAt(neighbor)) break;
                        if (!level().isEmptyBlock(neighbor)) { nearBlock = true; break; }
                    }
                    if (nearBlock) break;
                }
                if (nearBlock) {
                    moveControl.setWantedPosition(target.x, target.y, target.z, 1);
                    return;
                }
            }
            BlockPos pos = BlockPos.containing(target);
            if (!level().hasChunkAt(pos)) return;
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
            if (isVehicle() || staysStill()) { this.operation = Operation.WAIT; setDeltaMovement(Vec3.ZERO); return; }
            if (this.operation != Operation.MOVE_TO || this.floatDuration-- > 0) return;
            this.floatDuration += random.nextInt(5) + 2;
            Vec3 delta = new Vec3(this.wantedX - getX(), this.wantedY - getY(), this.wantedZ - getZ());
            if (delta.lengthSqr() < 1) { this.operation = Operation.WAIT; return; }
            if (delta.lengthSqr() > 4096) { this.operation = Operation.WAIT; return; }
            Vec3 step = delta.normalize();
            AABB box = getBoundingBox();
            for (int i = 1; i <= Mth.ceil(delta.length()); i++) {
                AABB next = box.move(step.scale(i));
                // 路径只检查已加载区域内的碰撞与方块状态。
                if (!level().hasChunksAt(BlockPos.containing(next.minX, next.minY, next.minZ),
                        BlockPos.containing(next.maxX, next.maxY, next.maxZ))) {
                    this.operation = Operation.WAIT; return;
                }
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
