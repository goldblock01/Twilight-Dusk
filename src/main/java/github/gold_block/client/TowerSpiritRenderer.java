package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import github.gold_block.TwilightDusk;
import github.gold_block.entity.TowerSpirit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class TowerSpiritRenderer extends MobRenderer<TowerSpirit, TowerSpiritModel> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(TwilightDusk.MODID, "tower_spirit"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TwilightDusk.MODID, "textures/entity/tower_spirit.png");

    public TowerSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new TowerSpiritModel(context.bakeLayer(LAYER)), 2.0F);
    }
    @Override public ResourceLocation getTextureLocation(TowerSpirit spirit) { return TEXTURE; }
    @Override protected void scale(TowerSpirit spirit, PoseStack pose, float partialTick) { pose.scale(4, 4, 4); }
}
