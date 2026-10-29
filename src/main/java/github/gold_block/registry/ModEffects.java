package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.effect.DizzinessEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TwilightDusk.MODID);

    public static final RegistryObject<MobEffect> DIZZINESS = MOB_EFFECTS.register("dizziness", DizzinessEffect::new);
}
