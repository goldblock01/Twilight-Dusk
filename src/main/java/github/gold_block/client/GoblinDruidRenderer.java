package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.entity.GoblinDruid;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class GoblinDruidRenderer extends MobRenderer<GoblinDruid, GoblinDruidModel> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(TwilightDusk.MODID, "goblin_druid"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            TwilightDusk.MODID, "textures/entity/goblin_druid.png");

    public GoblinDruidRenderer(EntityRendererProvider.Context context) {
        super(context, new GoblinDruidModel(context.bakeLayer(LAYER)), 0.35F);
        addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(GoblinDruid mob) { return TEXTURE; }
}
