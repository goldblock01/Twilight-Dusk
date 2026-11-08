package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TwilightDusk.MODID);

    public static final RegistryObject<SoundEvent> TOWER_SPIRIT_AMBIENT = register("tower_spirit_ambient");
    public static final RegistryObject<SoundEvent> TOWER_SPIRIT_HURT = register("tower_spirit_hurt");
    public static final RegistryObject<SoundEvent> TOWER_SPIRIT_DEATH = register("tower_spirit_death");

    public static final RegistryObject<SoundEvent> SPIRIT_HARNESS_EQUIP = register("tower_spirit_harness_equip");
    public static final RegistryObject<SoundEvent> SPIRIT_HARNESS_UNEQUIP = register("tower_spirit_harness_unequip");
    public static final RegistryObject<SoundEvent> SPIRIT_GOGGLES_UP = register("tower_spirit_goggles_up");
    public static final RegistryObject<SoundEvent> SPIRIT_GOGGLES_DOWN = register("tower_spirit_goggles_down");
    public static final RegistryObject<SoundEvent> GHASTLING_SPAWN = register("tower_ghastling_spawn");
    public static final RegistryObject<SoundEvent> GHASTLING_AMBIENT = register("tower_ghastling_ambient");
    public static final RegistryObject<SoundEvent> GHASTLING_HURT = register("tower_ghastling_hurt");
    public static final RegistryObject<SoundEvent> GHASTLING_DEATH = register("tower_ghastling_death");

    public static final RegistryObject<SoundEvent> SPEAR_USE = register("spear_use");
    public static final RegistryObject<SoundEvent> SPEAR_ATTACK = register("spear_attack");
    public static final RegistryObject<SoundEvent> SPEAR_HIT = register("spear_hit");

    public static final RegistryObject<SoundEvent> DRIED_AMBIENT = register("block.dried_ghast.ambient");
    public static final RegistryObject<SoundEvent> DRIED_AMBIENT_WATER = register("block.dried_ghast.ambient_water");
    public static final RegistryObject<SoundEvent> DRIED_BREAK = register("block.dried_ghast.break");
    public static final RegistryObject<SoundEvent> DRIED_FALL = register("block.dried_ghast.fall");
    public static final RegistryObject<SoundEvent> DRIED_PLACE = register("block.dried_ghast.place");
    public static final RegistryObject<SoundEvent> DRIED_PLACE_IN_WATER = register("block.dried_ghast.place_in_water");
    public static final RegistryObject<SoundEvent> DRIED_STEP = register("block.dried_ghast.step");
    public static final RegistryObject<SoundEvent> DRIED_TRANSITION = register("block.dried_ghast.transition");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TwilightDusk.MODID, name)));
    }
}
