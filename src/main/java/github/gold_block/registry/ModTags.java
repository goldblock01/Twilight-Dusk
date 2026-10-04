package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModTags {

    public static final TagKey<Item> KEPT_ON_DEATH = ItemTags.create(new ResourceLocation(TwilightDusk.MODID, "kept_on_death"));

    public static final TagKey<Enchantment> BANNED_ENCHANTS = TagKey.create(Registries.ENCHANTMENT,
            new ResourceLocation(TwilightDusk.MODID, "banned_enchants"));
}
