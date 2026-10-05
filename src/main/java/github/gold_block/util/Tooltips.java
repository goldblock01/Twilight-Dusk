package github.gold_block.util;

import github.gold_block.TwilightDusk;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class Tooltips {

    private static final String HOLD_SHIFT = "tooltip." + TwilightDusk.MODID + ".hold_shift";
    private static final String EMPTY = "";

    private Tooltips() {
    }

    public static void append(Item item, List<Component> tooltip) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        String prefix = "tooltip." + TwilightDusk.MODID + "." + (key == null ? EMPTY : key.getPath());
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.literal(Language.getInstance().getOrDefault(HOLD_SHIFT)));
            return;
        }
        for (String line : Language.getInstance().getOrDefault(prefix + ".desc").split("\n")) {
            tooltip.add(Component.literal(line));
        }
    }
}
