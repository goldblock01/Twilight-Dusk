package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class FrostyPickRepairEvents {

    private static final int ICE_REPAIR = 10;
    private static final int PACKED_ICE_REPAIR = 95;
    private static final int BLUE_ICE_REPAIR = 950;
    private static final int MAX_COST = 39;

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty() || !left.is(ModItems.FROSTY_PICK.get())) {
            return;
        }

        int damage = left.getDamageValue();
        int per = repairPerItem(right);
        if (damage <= 0 || per <= 0) {
            return;
        }

        int used = Math.min(right.getCount(), Mth.ceil(damage / (float) per));
        ItemStack output = left.copy();
        output.setDamageValue(damage - Math.min(damage, used * per));
        if (event.getName() != null && !event.getName().isEmpty()) {
            output.setHoverName(Component.literal(event.getName()));
        }

        event.setOutput(output);
        event.setMaterialCost(used);
        event.setCost(Math.max(1, Math.min(used, MAX_COST)));
    }

    private static int repairPerItem(ItemStack stack) {
        if (stack.is(Items.ICE)) {
            return ICE_REPAIR;
        }
        if (stack.is(Items.PACKED_ICE)) {
            return PACKED_ICE_REPAIR;
        }
        if (stack.is(Items.BLUE_ICE)) {
            return BLUE_ICE_REPAIR;
        }
        return 0;
    }
}
