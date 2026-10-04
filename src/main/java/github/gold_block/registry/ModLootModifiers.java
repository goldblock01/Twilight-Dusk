package github.gold_block.registry;

import com.mojang.serialization.Codec;
import github.gold_block.TwilightDusk;
import github.gold_block.loot.FieryToolSmeltingModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TwilightDusk.MODID);

    public static final RegistryObject<Codec<FieryToolSmeltingModifier>> FIERY_TOOL_SMELTING =
            LOOT_MODIFIERS.register("fiery_tool_smelting", () -> FieryToolSmeltingModifier.CODEC);
}
