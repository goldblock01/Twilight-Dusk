package github.gold_block.twilightlootr.init;

import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.item.BossChestItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TLItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TwilightDusk.MODID);

    public static final RegistryObject<Item> BOSS_CHEST = ITEMS.register("boss_chest",
            () -> new BossChestItem(TLBlocks.BOSS_CHEST.get(), new Item.Properties()));
}
