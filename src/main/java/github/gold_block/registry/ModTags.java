package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;

public class ModTags {

    public static final TagKey<Item> KEPT_ON_DEATH = ItemTags.create(new ResourceLocation(TwilightDusk.MODID, "kept_on_death"));

    public static final TagKey<Enchantment> BANNED_ENCHANTS = TagKey.create(Registries.ENCHANTMENT,
            new ResourceLocation(TwilightDusk.MODID, "banned_enchants"));

    public static final TagKey<DamageType> FREEZING = TagKey.create(Registries.DAMAGE_TYPE,
            new ResourceLocation(TwilightDusk.MODID, "freezing"));

    public static final TagKey<Biome> COLD_BIOMES = TagKey.create(Registries.BIOME,
            new ResourceLocation("minecraft", "is_cold"));

    public static final TagKey<Biome> HOT_BIOMES = TagKey.create(Registries.BIOME,
            new ResourceLocation("minecraft", "is_hot"));
}
