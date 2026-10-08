package github.gold_block.entity;

import github.gold_block.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import twilightforest.init.TFStructures;

public class DuskFrog extends Frog {

    private static final ResourceKey<Biome> TWILIGHT_SWAMP = ResourceKey.create(Registries.BIOME,
            new ResourceLocation("twilightforest", "swamp"));
    private static final ResourceKey<Biome> TWILIGHT_LAKE = ResourceKey.create(Registries.BIOME,
            new ResourceLocation("twilightforest", "lake"));

    public DuskFrog(EntityType<? extends Frog> type, Level level) {
        super(type, level);
    }

    @Override
    protected net.minecraft.world.entity.ai.Brain.Provider<Frog> brainProvider() {
        return net.minecraft.world.entity.ai.Brain.provider(MEMORY_TYPES, DuskFrogAi.SENSOR_TYPES);
    }

    @Override
    protected net.minecraft.world.entity.ai.Brain<?> makeBrain(com.mojang.serialization.Dynamic<?> dynamic) {
        return DuskFrogAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, SpawnGroupData data, net.minecraft.nbt.CompoundTag tag) {
        SpawnGroupData groupData = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.setVariant(pickVariant(level));
        DuskFrogAi.initMemories(this, level.getRandom());
        return groupData;
    }

    private FrogVariant pickVariant(ServerLevelAccessor level) {
        BlockPos pos = this.blockPosition();
        if (level instanceof ServerLevel serverLevel
                && serverLevel.structureManager().getStructureWithPieceAt(pos, TFStructures.LABYRINTH).isValid()) {
            return ModEntities.LABYRINTH_VARIANT.get();
        }
        Holder<Biome> biome = level.getBiome(pos);
        if (biome.is(TWILIGHT_SWAMP)) {
            return ModEntities.SWAMP_VARIANT.get();
        }
        if (biome.is(TWILIGHT_LAKE)) {
            return ModEntities.LAKE_VARIANT.get();
        }
        return ModEntities.SWAMP_VARIANT.get();
    }

    public static boolean checkDuskFrogSpawnRules(EntityType<DuskFrog> type, LevelAccessor level,
                                                  MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.FROGS_SPAWNABLE_ON)
                && level.getRawBrightness(pos, 0) > 4;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        DuskFrog baby = ModEntities.DUSK_FROG.get().create(level);
        if (baby != null) {
            DuskFrogAi.initMemories(baby, level.getRandom());
        }
        return baby;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return DuskFrogAi.BREEDING_ITEM.test(stack);
    }
}
