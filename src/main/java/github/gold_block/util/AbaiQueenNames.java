package github.gold_block.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import twilightforest.init.TFItems;

import java.util.Locale;
import java.util.Set;

public final class AbaiQueenNames {

    private static final Set<String> NAMES = Set.of("abai queen", "阿白女王");
    private static final Set<String> SCEPTER_NAMES = Set.of("阿白权杖", "abai scepter");

    private AbaiQueenNames() {
    }

    public static boolean isNamed(Entity entity) {
        Component name = entity.getCustomName();
        return name != null && NAMES.contains(name.getString().trim().toLowerCase(Locale.ROOT));
    }

    public static boolean isAbaiScepter(ItemStack stack) {
        if (!stack.is(TFItems.ZOMBIE_SCEPTER.get())) {
            return false;
        }
        return SCEPTER_NAMES.contains(stack.getHoverName().getString().trim().toLowerCase(Locale.ROOT));
    }
}
