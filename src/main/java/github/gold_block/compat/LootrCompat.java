package github.gold_block.compat;

import net.minecraftforge.fml.ModList;

public final class LootrCompat {

    public static final String MOD_ID = "lootr";

    private LootrCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
}
