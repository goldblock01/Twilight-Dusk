package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 沿用霜镐的定量修复方式：每枚金锭恢复25耐久。 */
@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class MagicSwordRepairEvents {
    private static final int REPAIR_PER_INGOT = 25;

    private MagicSwordRepairEvents() { }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!left.is(ModItems.MAGIC_SWORD.get()) || !right.is(Items.GOLD_INGOT)
                || !left.isDamaged()) {
            return;
        }
        int damage = left.getDamageValue();
        int used = Math.min(right.getCount(), (damage + REPAIR_PER_INGOT - 1) / REPAIR_PER_INGOT);
        ItemStack output = left.copy();
        output.setDamageValue(Math.max(0, damage - used * REPAIR_PER_INGOT));
        if (event.getName() != null) {
            if (event.getName().isBlank()) {
                output.resetHoverName();
            } else {
                output.setHoverName(Component.literal(event.getName()));
            }
        }
        event.setOutput(output);
        event.setMaterialCost(used);
        event.setCost(Math.max(1, Math.min(used, 39)));
    }
}
