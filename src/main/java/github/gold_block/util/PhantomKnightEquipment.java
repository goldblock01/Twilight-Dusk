package github.gold_block.util;

import github.gold_block.Config;
import github.gold_block.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import twilightforest.entity.boss.KnightPhantom;
import twilightforest.init.TFItems;

public final class PhantomKnightEquipment {

    private PhantomKnightEquipment() {
    }

    public static void upgrade(KnightPhantom knight) {
        if (!Config.phantomKnightTools() || clientSide(knight)) {
            return;
        }
        Item phantom = phantomOf(knight.getMainHandItem());
        if (phantom != null) {
            knight.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(phantom));
        }
    }

    public static boolean holds(KnightPhantom knight, RegistryObject<Item> weapon) {
        return knight.getMainHandItem().is(weapon.get());
    }

    public static Item phantomOf(ItemStack weapon) {
        if (weapon.is(TFItems.KNIGHTMETAL_SWORD.get())) {
            return ModItems.PHANTOM_SWORD.get();
        }
        if (weapon.is(TFItems.KNIGHTMETAL_AXE.get())) {
            return ModItems.PHANTOM_AXE.get();
        }
        if (weapon.is(TFItems.KNIGHTMETAL_PICKAXE.get())) {
            return ModItems.PHANTOM_PICKAXE.get();
        }
        return null;
    }

    private static boolean clientSide(KnightPhantom knight) {
        return knight.level() != null && knight.level().isClientSide();
    }
}
