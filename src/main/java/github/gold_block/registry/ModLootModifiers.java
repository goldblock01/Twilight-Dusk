package github.gold_block.registry;

import com.mojang.serialization.Codec;
import github.gold_block.TwilightDusk;
import github.gold_block.loot.AddItemModifier;
import github.gold_block.loot.FieryToolSmeltingModifier;
import github.gold_block.loot.TowerDestroyerReplaceModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TwilightDusk.MODID);

    public static final RegistryObject<Codec<FieryToolSmeltingModifier>> FIERY_TOOL_SMELTING =
            LOOT_MODIFIERS.register("fiery_tool_smelting", () -> FieryToolSmeltingModifier.CODEC);

    public static final RegistryObject<Codec<TowerDestroyerReplaceModifier>> TOWER_DESTROYER_REPLACE =
            LOOT_MODIFIERS.register("tower_destroyer_replace", () -> TowerDestroyerReplaceModifier.CODEC);

    /** 通用「往表里加一件东西」；具体加什么、对哪张表生效都由 JSON 决定 */
    public static final RegistryObject<Codec<AddItemModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register("add_item", () -> AddItemModifier.CODEC);
}
