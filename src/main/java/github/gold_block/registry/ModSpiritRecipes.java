package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.recipe.SpiritHarnessDyeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSpiritRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TwilightDusk.MODID);
    public static final RegistryObject<SimpleCraftingRecipeSerializer<SpiritHarnessDyeRecipe>> HARNESS_DYE =
            SERIALIZERS.register("spirit_harness_dye", () -> new SimpleCraftingRecipeSerializer<>(SpiritHarnessDyeRecipe::new));
    private ModSpiritRecipes() { }
}
