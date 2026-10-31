package github.gold_block.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.gold_block.registry.ModStructurePlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import twilightforest.init.TFLandmark;
import twilightforest.util.LegacyLandmarkPlacements;

/** 在错开的地标网格中，以中型矿山的抽选规则决定结构候选位置。 */
public class MediumHillFrequencyPlacement extends RandomSpreadStructurePlacement {

    public static final Codec<MediumHillFrequencyPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt").forGetter(MediumHillFrequencyPlacement::salt),
            Codec.intRange(-4, 4).fieldOf("offset_x").forGetter(placement -> placement.offsetX),
            Codec.intRange(-4, 4).fieldOf("offset_z").forGetter(placement -> placement.offsetZ)
    ).apply(instance, MediumHillFrequencyPlacement::new));

    /** 平移结构候选位置，避免与其他结构共用同一个地标中心。 */
    private final int offsetX;
    private final int offsetZ;

    public MediumHillFrequencyPlacement(int salt, int offsetX, int offsetZ) {
        super(16, 8, RandomSpreadType.LINEAR, salt);
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
    }

    /** 按网格区域返回唯一候选点，供结构定位命令搜索。 */
    @Override
    public ChunkPos getPotentialStructureChunk(long seed, int chunkX, int chunkZ) {
        BlockPos center = LegacyLandmarkPlacements.getNearestCenterXZ(
                Math.floorDiv(chunkX, 16) * 16, Math.floorDiv(chunkZ, 16) * 16);
        return new ChunkPos((center.getX() >> 4) + this.offsetX, (center.getZ() >> 4) + this.offsetZ);
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int chunkX, int chunkZ) {
        return this.isSelectedChunk(state.getLevelSeed(), chunkX, chunkZ);
    }

    /** 各结构独立抽选，保留中型矿山的权重及固定地标槽位排除规则。 */
    public boolean isSelectedChunk(long seed, int chunkX, int chunkZ) {
        int referenceX = chunkX - this.offsetX;
        int referenceZ = chunkZ - this.offsetZ;
        return LegacyLandmarkPlacements.chunkHasLandmarkCenter(referenceX, referenceZ)
                && LegacyLandmarkPlacements.pickVarietyLandmark(referenceX, referenceZ, seed + this.salt())
                == TFLandmark.MEDIUM_HILL;
    }

    @Override
    public StructurePlacementType<?> type() {
        return ModStructurePlacements.MEDIUM_HILL_FREQUENCY.get();
    }
}
