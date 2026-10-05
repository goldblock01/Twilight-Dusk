package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.init.TLItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TwilightDusk.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + TwilightDusk.MODID))
            .icon(() -> new ItemStack(ModItems.PHANTOM_SWORD.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.PHANTOM_ARMOR_PLATE.get());
                output.accept(ModItems.PHANTOM_INGOT.get());
                output.accept(ModItems.PHANTOM_SWORD.get());
                output.accept(ModItems.PHANTOM_PICKAXE.get());
                output.accept(ModItems.PHANTOM_AXE.get());
                output.accept(ModItems.FIERY_AXE.get());
                output.accept(ModItems.FIERY_SHOVEL.get());
                output.accept(ModItems.FIERY_SCYTHE.get());
                output.accept(ModItems.MEDAL.get());
                output.accept(ModItems.TWILIGHT_MEDAL.get());
                output.accept(ModItems.FRIGID_MEDAL.get());
                output.accept(ModItems.BLAZING_MEDAL.get());
                output.accept(ModItems.KNIGHT_MEDAL.get());
                output.accept(TLItems.BOSS_CHEST.get());
            })
            .build());
}
