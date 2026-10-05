package github.gold_block.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import github.gold_block.entity.DuskTadpole;

public class DuskTadpoleRenderer extends MobRenderer<DuskTadpole, DuskTadpoleModel> {

    private static final ResourceLocation TADPOLE_TEXTURE =
            new ResourceLocation("textures/entity/tadpole/tadpole.png");

    public DuskTadpoleRenderer(EntityRendererProvider.Context context) {
        super(context, new DuskTadpoleModel(context.bakeLayer(ModelLayers.TADPOLE)), 0.14F);
    }

    @Override
    public ResourceLocation getTextureLocation(DuskTadpole entity) {
        return TADPOLE_TEXTURE;
    }
}
