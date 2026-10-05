package github.gold_block.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import github.gold_block.registry.ModEntities;
import github.gold_block.registry.ModItems;

public class DuskTadpole extends AbstractFish {

    public static final float HITBOX_WIDTH = 0.4F;
    public static final float HITBOX_HEIGHT = 0.3F;
    private static final int TICKS_TO_BE_FROG = 24000;

    private static final ImmutableList<SensorType<? extends Sensor<? super DuskTadpole>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY,
            SensorType.FROG_TEMPTATIONS);
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.IS_PANICKING);

    private int age;

    public DuskTadpole(EntityType<? extends AbstractFish> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected Brain.Provider<DuskTadpole> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return DuskTadpoleAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public Brain<DuskTadpole> getBrain() {
        return (Brain<DuskTadpole>) super.getBrain();
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TADPOLE_FLOP;
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("duskTadpoleBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("duskTadpoleActivityUpdate");
        DuskTadpoleAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.setAge(this.age + 1);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Age", this.age);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAge(tag.getInt("Age"));
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.TADPOLE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.TADPOLE_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isFood(stack)) {
            this.feed(player, stack);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.DUSK_TADPOLE_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_TADPOLE;
    }

    private boolean isFood(ItemStack stack) {
        return Frog.TEMPTATION_ITEM.test(stack);
    }

    private void feed(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(this.getTicksLeftUntilAdult()));
        this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
    }

    private int getTicksLeftUntilAdult() {
        return Math.max(0, TICKS_TO_BE_FROG - this.age);
    }

    private void ageUp(int seconds) {
        this.setAge(this.age + seconds * 20);
    }

    private void setAge(int age) {
        this.age = age;
        if (this.age >= TICKS_TO_BE_FROG) {
            this.growIntoFrog();
        }
    }

    private void growIntoFrog() {
        if (this.level() instanceof ServerLevel serverLevel) {
            DuskFrog frog = ModEntities.DUSK_FROG.get().create(this.level());
            if (frog != null) {
                frog.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                frog.finalizeSpawn(serverLevel, this.level().getCurrentDifficultyAt(frog.blockPosition()),
                        MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
                frog.setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    frog.setCustomName(this.getCustomName());
                    frog.setCustomNameVisible(this.isCustomNameVisible());
                }
                frog.setPersistenceRequired();
                this.playSound(SoundEvents.TADPOLE_GROW_UP, 0.15F, 1.0F);
                serverLevel.addFreshEntityWithPassengers(frog);
                this.discard();
            }
        }
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }
}
