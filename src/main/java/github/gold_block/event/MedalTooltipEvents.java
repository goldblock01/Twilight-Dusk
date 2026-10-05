package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.item.medal.MedalItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, value = Dist.CLIENT)
public class MedalTooltipEvents {

    private static final String MODIFIER_PREFIX = "curios.modifiers.";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof MedalItem)) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        stripModifierBlock(tooltip);
        stripRegistryName(tooltip, stack);
    }

    private static void stripModifierBlock(List<Component> tooltip) {
        int header = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            if (tooltip.get(i).getContents() instanceof TranslatableContents contents
                    && contents.getKey().startsWith(MODIFIER_PREFIX)) {
                header = i;
                break;
            }
        }
        if (header < 0) {
            return;
        }
        int from = header;
        if (from > 0 && tooltip.get(from - 1).getString().isEmpty()) {
            from--;
        }
        tooltip.subList(from, tooltip.size()).clear();
    }

    private static void stripRegistryName(List<Component> tooltip, ItemStack stack) {
        String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        for (int i = tooltip.size() - 1; i > 0; i--) {
            if (tooltip.get(i).getString().equals(id)) {
                tooltip.remove(i);
            }
        }
    }
}
