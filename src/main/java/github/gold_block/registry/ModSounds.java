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

    public static final RegistryObject<SoundEvent> SPEAR_USE = register("spear_use");
    public static final RegistryObject<SoundEvent> SPEAR_ATTACK = register("spear_attack");
    public static final RegistryObject<SoundEvent> SPEAR_HIT = register("spear_hit");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TwilightDusk.MODID, name)));
    }
}
