package github.gold_block.util;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class PhantomTier {

    public static final Tier PHANTOM = TierSortingRegistry.registerTier(
            new ForgeTier(4, 1024, 9.0F, 4.0F, 15,
                    BlockTags.create(new ResourceLocation(TwilightDusk.MODID, "needs_phantom_tool")),
                    () -> Ingredient.of(ModItems.PHANTOM_INGOT.get())),
            new ResourceLocation(TwilightDusk.MODID, "phantom"),
            List.of(Tiers.NETHERITE),
            List.of());
}
