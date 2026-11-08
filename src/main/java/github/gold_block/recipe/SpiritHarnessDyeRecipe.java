package github.gold_block.recipe;

import github.gold_block.item.SpiritHarnessItem;
import github.gold_block.registry.ModItems;
import github.gold_block.registry.ModSpiritRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** 任意颜色挽具加一份染料改色，保留原堆叠的命名等数据。 */
public class SpiritHarnessDyeRecipe extends CustomRecipe {
    public SpiritHarnessDyeRecipe(ResourceLocation id, CraftingBookCategory category) { super(id, category); }
    @Override public boolean matches(CraftingContainer grid, Level level) {
        int harnesses = 0, dyes = 0;
        for (int i = 0; i < grid.getContainerSize(); i++) {
            var stack = grid.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof SpiritHarnessItem) harnesses++;
            else if (stack.getItem() instanceof DyeItem) dyes++;
            else return false;
        }
        return harnesses == 1 && dyes == 1;
    }
    @Override public ItemStack assemble(CraftingContainer grid, RegistryAccess registries) {
        ItemStack original = ItemStack.EMPTY; DyeItem dye = null;
        for (int i = 0; i < grid.getContainerSize(); i++) {
            if (grid.getItem(i).getItem() instanceof SpiritHarnessItem) original = grid.getItem(i);
            if (grid.getItem(i).getItem() instanceof DyeItem found) dye = found;
        }
        if (original.isEmpty() || dye == null || !matches(grid, null)) return ItemStack.EMPTY;
        ItemStack result = new ItemStack(ModItems.SPIRIT_HARNESSES.get(dye.getDyeColor()).get());
        if (original.hasTag()) result.setTag(original.getTag().copy());
        return result;
    }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }
    @Override public RecipeSerializer<?> getSerializer() { return ModSpiritRecipes.HARNESS_DYE.get(); }
}
