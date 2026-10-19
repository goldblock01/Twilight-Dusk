package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.init.TLItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

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
                acceptEnchanted(output, ModItems.FROSTY_PICK.get(),
                        new EnchantmentInstance(Enchantments.BLOCK_EFFICIENCY, 3),
                        new EnchantmentInstance(Enchantments.BLOCK_FORTUNE, 2),
                        new EnchantmentInstance(Enchantments.UNBREAKING, 3));
                acceptEnchanted(output, ModItems.NAGA_CURVED_SWORD.get(),
                        new EnchantmentInstance(Enchantments.MOB_LOOTING, 3));
                output.accept(ModItems.MEDAL.get());
                output.accept(ModItems.TWILIGHT_MEDAL.get());
                output.accept(ModItems.FRIGID_MEDAL.get());
                output.accept(ModItems.BLAZING_MEDAL.get());
                output.accept(ModItems.KNIGHT_MEDAL.get());
                if (TLItems.BOSS_CHEST.isPresent()) {
                    output.accept(TLItems.BOSS_CHEST.get());
                }
                output.accept(TFBlocks.KEEPSAKE_CASKET.get().asItem());
                output.accept(TFItems.BRITTLE_FLASK.get());
                output.accept(TFItems.GREATER_FLASK.get());
                output.accept(ModItems.DUSK_FROG_SPAWN_EGG.get());
                output.accept(ModItems.DUSK_FROGSPAWN.get());
                output.accept(ModItems.DUSK_TADPOLE_BUCKET.get());
                output.accept(ModItems.MAZE_GEL.get());
                output.accept(ModItems.EXTREME_COLD_ICE_BONES.get());
                output.accept(ModItems.RUSTED_IRON_SHEET.get());
                output.accept(ModItems.AURORA_ICE_HEART.get());
                output.accept(ModItems.TIME_WATCH.get());
                output.accept(ModItems.BURST_SCEPTER.get());
                acceptEnchanted(output, ModItems.TOWER_DESTROYER.get(),
                        new EnchantmentInstance(Enchantments.BLOCK_EFFICIENCY, 4),
                        new EnchantmentInstance(Enchantments.BLOCK_FORTUNE, 2),
                        new EnchantmentInstance(Enchantments.UNBREAKING, 3));
            })
            .build());

    private static void acceptEnchanted(CreativeModeTab.Output output, Item item, EnchantmentInstance... instances) {
        ItemStack stack = new ItemStack(item);
        for (EnchantmentInstance instance : instances) {
            stack.enchant(instance.enchantment, instance.level);
        }
        output.accept(stack);
    }
}
