package github.gold_block.item;

import github.gold_block.registry.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public interface PhantomEnchantRestriction extends IForgeItem {

    @Override
    default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return !ForgeRegistries.ENCHANTMENTS.tags().getTag(ModTags.BANNED_ENCHANTS).contains(enchantment);
    }

    @Override
    default boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        for (Enchantment banned : ForgeRegistries.ENCHANTMENTS.tags().getTag(ModTags.BANNED_ENCHANTS)) {
            if (EnchantmentHelper.getEnchantments(book).keySet().stream().anyMatch(e -> Objects.equals(e, banned))) {
                return false;
            }
        }
        return true;
    }
}
