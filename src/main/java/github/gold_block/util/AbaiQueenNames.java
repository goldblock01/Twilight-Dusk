package github.gold_block.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Locale;
import java.util.Set;

public final class AbaiQueenNames {

    private static final Set<String> NAMES = Set.of("abai queen", "阿白女王");

    private AbaiQueenNames() {
    }

    public static boolean isNamed(Entity entity) {
        Component name = entity.getCustomName();
        return name != null && NAMES.contains(name.getString().trim().toLowerCase(Locale.ROOT));
    }
}
