package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModEntities;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.SpawnPlacements;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {

    @SubscribeEvent
    public static void onRegisterAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DUSK_FROG.get(), Frog.createAttributes().build());
        event.put(ModEntities.DUSK_TADPOLE.get(), Tadpole.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.DUSK_FROG.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Frog::checkFrogSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
