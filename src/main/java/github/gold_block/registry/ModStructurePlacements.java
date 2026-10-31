package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.worldgen.MediumHillFrequencyPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** 注册可供结构集 JSON 使用的候选位置分布规则。 */
public class ModStructurePlacements {

    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENTS =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, TwilightDusk.MODID);

    public static final RegistryObject<StructurePlacementType<MediumHillFrequencyPlacement>> MEDIUM_HILL_FREQUENCY =
            STRUCTURE_PLACEMENTS.register("medium_hill_frequency", () -> () -> MediumHillFrequencyPlacement.CODEC);
}
