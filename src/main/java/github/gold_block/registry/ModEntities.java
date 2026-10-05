package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.entity.DuskFrog;
import github.gold_block.entity.DuskFrogAi;
import github.gold_block.entity.DuskTadpole;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TwilightDusk.MODID);
    public static final DeferredRegister<FrogVariant> FROG_VARIANTS =
            DeferredRegister.create(Registries.FROG_VARIANT, TwilightDusk.MODID);
    public static final DeferredRegister<SensorType<?>> SENSORS =
            DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, TwilightDusk.MODID);

    public static final RegistryObject<EntityType<DuskFrog>> DUSK_FROG = ENTITY_TYPES.register("dusk_frog",
            () -> EntityType.Builder.of(DuskFrog::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.5F).clientTrackingRange(10).build("dusk_frog"));

    public static final RegistryObject<EntityType<DuskTadpole>> DUSK_TADPOLE = ENTITY_TYPES.register("dusk_tadpole",
            () -> EntityType.Builder.of(DuskTadpole::new, MobCategory.CREATURE)
                    .sized(DuskTadpole.HITBOX_WIDTH, DuskTadpole.HITBOX_HEIGHT).clientTrackingRange(10).build("dusk_tadpole"));

    public static final RegistryObject<FrogVariant> SWAMP_VARIANT = FROG_VARIANTS.register("swamp",
            () -> new FrogVariant(new ResourceLocation(TwilightDusk.MODID, "textures/entity/frog/swamp_frog.png")));

    public static final RegistryObject<FrogVariant> LAKE_VARIANT = FROG_VARIANTS.register("lake",
            () -> new FrogVariant(new ResourceLocation(TwilightDusk.MODID, "textures/entity/frog/lake_frog.png")));

    public static final RegistryObject<FrogVariant> LABYRINTH_VARIANT = FROG_VARIANTS.register("labyrinth",
            () -> new FrogVariant(new ResourceLocation(TwilightDusk.MODID, "textures/entity/frog/labyrinth_frog.png")));

    public static final RegistryObject<SensorType<TemptingSensor>> DUSK_FROG_TEMPTATIONS =
            SENSORS.register("dusk_frog_temptations",
                    () -> new SensorType<>(() -> new TemptingSensor(DuskFrogAi.BREEDING_ITEM)));
}
