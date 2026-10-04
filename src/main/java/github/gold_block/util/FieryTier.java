package github.gold_block.util;

import github.gold_block.TwilightDusk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class FieryTier {

    private static final ResourceLocation TWILIGHT_FIERY_INGOT =
            new ResourceLocation("twilightforest", "fiery_ingot");

    public static final Tier FIERY = TierSortingRegistry.registerTier(
            new ForgeTier(4, 1024, 9.0F, 4.0F, 10,
                    BlockTags.create(new ResourceLocation(TwilightDusk.MODID, "needs_fiery_tool")),
                    () -> Ingredient.of(ForgeRegistries.ITEMS.getValue(TWILIGHT_FIERY_INGOT))),
            new ResourceLocation(TwilightDusk.MODID, "fiery"),
            List.of(Tiers.NETHERITE),
            List.of());
}
