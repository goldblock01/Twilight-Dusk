package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.util.AbaiQueenNames;
import net.minecraft.resources.ResourceLocation;
import twilightforest.client.JappaPackReloadListener;
import twilightforest.entity.boss.SnowQueen;

public final class AbaiQueenSkin {

    public static final ResourceLocation TEXTURE =
            new ResourceLocation(TwilightDusk.MODID, "textures/entity/abai_queen.png");

    private AbaiQueenSkin() {
    }

    public static boolean isAbaiQueen(SnowQueen queen) {
        return AbaiQueenNames.isNamed(queen);
    }

    public static boolean jappa() {
        return JappaPackReloadListener.INSTANCE.uncachedJappaPackCheck().getAsBoolean();
    }
}
