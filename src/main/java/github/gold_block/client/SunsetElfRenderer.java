package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.entity.SunsetElf;
import net.minecraft.client.renderer.entity.AllayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;

/** 沿用悦灵模型、动画和亮度，根据同步的外观编号选择贴图。 */
public class SunsetElfRenderer extends AllayRenderer {

    private static final ResourceLocation[] TEXTURES = {
            texture("purple"), texture("blue"), texture("green"), texture("yellow")
    };

    public SunsetElfRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static ResourceLocation texture(String color) {
        return new ResourceLocation(TwilightDusk.MODID, "textures/entity/sunset_elf/" + color + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(Allay allay) {
        return TEXTURES[((SunsetElf) allay).getVariant()];
    }
}
