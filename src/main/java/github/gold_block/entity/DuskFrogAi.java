package github.gold_block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.Croak;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.LongJumpMidJump;
import net.minecraft.world.entity.ai.behavior.LongJumpToPreferredBlock;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.TryFindLand;
import net.minecraft.world.entity.ai.behavior.TryFindLandNearWater;
import net.minecraft.world.entity.ai.behavior.TryLaySpawnOnWaterNearLand;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.ShootTongue;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.entity.Mob;

import github.gold_block.registry.ModBlocks;
import github.gold_block.registry.ModEntities;
import net.minecraft.core.BlockPos;
import twilightforest.init.TFBlocks;

public class DuskFrogAi {

    public static final Ingredient BREEDING_ITEM = Ingredient.of(
            TFBlocks.FIREFLY.get().asItem(), TFBlocks.CICADA.get().asItem());

    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25F;
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);

    public static final ImmutableList<SensorType<? extends Sensor<? super Frog>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.HURT_BY,
            SensorType.FROG_ATTACKABLES,
            ModEntities.DUSK_FROG_TEMPTATIONS.get(),
            SensorType.IS_IN_WATER);

    public static void initMemories(Frog frog, RandomSource random) {
        frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(random));
    }

    public static Brain<?> makeBrain(Brain<Frog> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initSwimActivity(brain);
        initLaySpawnActivity(brain);
        initTongueActivity(brain);
        initJumpActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Frog> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new AnimalPanic(2.0F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(Brain<Frog> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(0, new AnimalMakeLove(ModEntities.DUSK_FROG.get(), 1.0F)),
                Pair.of(1, new FollowTemptation(speed -> SPEED_MULTIPLIER_WHEN_TEMPTED)),
                Pair.of(2, StartAttacking.create(DuskFrogAi::canAttack,
                        frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(3, TryFindLand.create(6, 1.0F)),
                Pair.of(4, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(Pair.of(RandomStroll.stroll(1.0F), 1),
                                Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                                Pair.of(new Croak(), 3),
                                Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 2))))),
                ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initSwimActivity(Brain<Frog> brain) {
        brain.addActivityWithConditions(Activity.SWIM, ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(1, new FollowTemptation(speed -> SPEED_MULTIPLIER_WHEN_TEMPTED)),
                Pair.of(2, StartAttacking.create(DuskFrogAi::canAttack,
                        frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(3, TryFindLand.create(8, 1.5F)),
                Pair.of(5, new GateBehavior<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                        ImmutableSet.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL,
                        ImmutableList.of(Pair.of(RandomStroll.swim(0.75F), 1),
                                Pair.of(RandomStroll.stroll(1.0F, true), 1),
                                Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                                Pair.of(BehaviorBuilder.triggerIf(Entity::isInWaterOrBubble), 5))))),
                ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_PRESENT)));
    }

    private static void initLaySpawnActivity(Brain<Frog> brain) {
        brain.addActivityWithConditions(Activity.LAY_SPAWN, ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(1, StartAttacking.create(DuskFrogAi::canAttack,
                        frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(2, TryFindLandNearWater.create(8, 1.0F)),
                Pair.of(3, TryLaySpawnOnWaterNearLand.create(ModBlocks.DUSK_FROGSPAWN.get())),
                Pair.of(4, new RunOne<>(ImmutableList.of(Pair.of(RandomStroll.stroll(1.0F), 2),
                        Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                        Pair.of(new Croak(), 2),
                        Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 1))))),
                ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT)));
    }

    private static void initJumpActivity(Brain<Frog> brain) {
        brain.addActivityWithConditions(Activity.LONG_JUMP, ImmutableList.of(
                Pair.of(0, new LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.FROG_STEP)),
                Pair.of(1, new LongJumpToPreferredBlock<>(TIME_BETWEEN_LONG_JUMPS, 2, 4, 1.5F,
                        speed -> SoundEvents.FROG_LONG_JUMP, BlockTags.FROG_PREFER_JUMP_TO, 0.5F,
                        DuskFrogAi::isAcceptableLandingSpot))),
                ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)));
    }

    private static void initTongueActivity(Brain<Frog> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.TONGUE, 0, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                new ShootTongue(SoundEvents.FROG_TONGUE, SoundEvents.FROG_EAT)), MemoryModuleType.ATTACK_TARGET);
    }

    private static <E extends Mob> boolean isAcceptableLandingSpot(E frog, BlockPos pos) {
        Level level = frog.level();
        BlockPos below = pos.below();
        if (level.getFluidState(pos).isEmpty() && level.getFluidState(below).isEmpty()
                && level.getFluidState(pos.above()).isEmpty()) {
            BlockState state = level.getBlockState(pos);
            BlockState stateBelow = level.getBlockState(below);
            if (!state.is(BlockTags.FROG_PREFER_JUMP_TO) && !stateBelow.is(BlockTags.FROG_PREFER_JUMP_TO)) {
                BlockPathTypes pathType = WalkNodeEvaluator.getBlockPathTypeStatic(level, pos.mutable());
                BlockPathTypes pathTypeBelow = WalkNodeEvaluator.getBlockPathTypeStatic(level, below.mutable());
                return pathType != BlockPathTypes.TRAPDOOR && (!state.isAir() || pathTypeBelow != BlockPathTypes.TRAPDOOR)
                        ? LongJumpToRandomPos.defaultAcceptableLandingSpot(frog, pos)
                        : true;
            }
            return true;
        }
        return false;
    }

    private static boolean canAttack(Frog frog) {
        return !BehaviorUtils.isBreeding(frog);
    }

    public static void updateActivity(Frog frog) {
        frog.getBrain().setActiveActivityToFirstValid(
                ImmutableList.of(Activity.TONGUE, Activity.LAY_SPAWN, Activity.LONG_JUMP, Activity.SWIM, Activity.IDLE));
    }
}
