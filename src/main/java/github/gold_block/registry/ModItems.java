package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.item.FieryAxeItem;
import github.gold_block.item.FieryScytheItem;
import github.gold_block.item.FieryShovelItem;
import github.gold_block.item.PhantomAxeItem;
import github.gold_block.item.PhantomPickaxeItem;
import github.gold_block.item.PhantomSwordItem;
import github.gold_block.item.medal.BlazingMedalItem;
import github.gold_block.item.medal.FrigidMedalItem;
import github.gold_block.item.medal.KnightMedalItem;
import github.gold_block.item.medal.MedalItem;
import github.gold_block.item.medal.TwilightMedalItem;
import github.gold_block.util.FieryTier;
import github.gold_block.util.PhantomTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TwilightDusk.MODID);

    private static final Tier PHANTOM = PhantomTier.PHANTOM;
    private static final Tier FIERY = FieryTier.FIERY;
    private static final Rarity RARITY = Rarity.UNCOMMON;

    public static final RegistryObject<Item> PHANTOM_INGOT = ITEMS.register("phantom_ingot",
            () -> new Item(new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> PHANTOM_ARMOR_PLATE = ITEMS.register("phantom_armor_plate",
            () -> new Item(new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> PHANTOM_SWORD = ITEMS.register("phantom_sword",
            () -> new PhantomSwordItem(PHANTOM, 4, -2.4F, new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> PHANTOM_PICKAXE = ITEMS.register("phantom_pickaxe",
            () -> new PhantomPickaxeItem(PHANTOM, 2, -2.8F, new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> PHANTOM_AXE = ITEMS.register("phantom_axe",
            () -> new PhantomAxeItem(PHANTOM, 8.0F, -3.0F, new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> FIERY_AXE = ITEMS.register("fiery_axe",
            () -> new FieryAxeItem(FIERY, 8.0F, -3.0F, new Item.Properties().fireResistant().rarity(RARITY)));

    public static final RegistryObject<Item> FIERY_SHOVEL = ITEMS.register("fiery_shovel",
            () -> new FieryShovelItem(FIERY, 2.5F, -3.0F, new Item.Properties().fireResistant().rarity(RARITY)));

    public static final RegistryObject<Item> FIERY_SCYTHE = ITEMS.register("fiery_scythe",
            () -> new FieryScytheItem(FIERY, 5, -2.8F, new Item.Properties().fireResistant().rarity(RARITY)));

    public static final RegistryObject<Item> MEDAL = ITEMS.register("medal",
            () -> new MedalItem(new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> TWILIGHT_MEDAL = ITEMS.register("twilight_medal",
            () -> new TwilightMedalItem(new Item.Properties().rarity(RARITY)));

    public static final RegistryObject<Item> FRIGID_MEDAL = ITEMS.register("frigid_medal",
            () -> new FrigidMedalItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> BLAZING_MEDAL = ITEMS.register("blazing_medal",
            () -> new BlazingMedalItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> KNIGHT_MEDAL = ITEMS.register("knight_medal",
            () -> new KnightMedalItem(new Item.Properties().rarity(Rarity.EPIC)));
}
