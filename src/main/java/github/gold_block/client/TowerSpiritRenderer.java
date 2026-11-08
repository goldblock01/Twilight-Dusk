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
    public static final ModelLayerLocation BABY_LAYER = new ModelLayerLocation(new ResourceLocation(TwilightDusk.MODID, "tower_ghastling"), "main");
    private final TowerSpiritModel adultModel;
    private final TowerSpiritModel babyModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TwilightDusk.MODID, "textures/entity/tower_spirit.png");

    public TowerSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new TowerSpiritModel(context.bakeLayer(LAYER)), 2.0F);
        this.adultModel = this.model;
        this.babyModel = new TowerSpiritModel(context.bakeLayer(BABY_LAYER));
        addLayer(new SpiritHarnessLayer(this, context.bakeLayer(SpiritHarnessLayer.LAYER)));
    }
    @Override public void render(TowerSpirit spirit, float yaw, float partial, com.mojang.blaze3d.vertex.PoseStack pose,
            net.minecraft.client.renderer.MultiBufferSource buffer, int light) {
        this.model = spirit.isBaby() ? babyModel : adultModel;
        this.shadowRadius = spirit.isBaby() ? 0.475F : 2.0F;
        super.render(spirit, yaw, partial, pose, buffer, light);
    }
    @Override public ResourceLocation getTextureLocation(TowerSpirit spirit) { return TEXTURE; }
    @Override protected void scale(TowerSpirit spirit, PoseStack pose, float partialTick) { float scale = spirit.isBaby() ? 0.95F : 4.0F; pose.scale(scale, scale, scale); }
}
