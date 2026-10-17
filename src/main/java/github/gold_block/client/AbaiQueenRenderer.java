package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.TFModelLayers;
import twilightforest.client.model.entity.SnowQueenModel;
import twilightforest.client.model.entity.newmodels.NewSnowQueenModel;
import twilightforest.entity.boss.SnowQueen;

public class AbaiQueenRenderer extends HumanoidMobRenderer<SnowQueen, HumanoidModel<SnowQueen>> {

    private static final ResourceLocation QUEEN_TEXTURE = TwilightForestMod.getModelTexture("snowqueen.png");

    public AbaiQueenRenderer(EntityRendererProvider.Context context) {
        super(context, queenModel(context), 0.625F);
        this.addLayer(new AbaiQueenLayer(this));
    }

    private static HumanoidModel<SnowQueen> queenModel(EntityRendererProvider.Context context) {
        return AbaiQueenSkin.jappa()
                ? new NewSnowQueenModel(context.bakeLayer(TFModelLayers.SNOW_QUEEN))
                : new SnowQueenModel(context.bakeLayer(TFModelLayers.NEW_SNOW_QUEEN));
    }

    @Override
    public ResourceLocation getTextureLocation(SnowQueen queen) {
        return AbaiQueenSkin.isAbaiQueen(queen) ? AbaiQueenSkin.TEXTURE : QUEEN_TEXTURE;
    }

    @Override
    protected void scale(SnowQueen queen, PoseStack stack, float partialTicks) {
        stack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public void render(SnowQueen queen, float yaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {
        boolean showQueen = !AbaiQueenSkin.isAbaiQueen(queen);
        HumanoidModel<SnowQueen> model = this.getModel();
        model.head.visible = showQueen;
        model.hat.visible = showQueen;
        model.body.visible = showQueen;
        model.rightArm.visible = showQueen;
        model.leftArm.visible = showQueen;
        model.rightLeg.visible = showQueen;
        model.leftLeg.visible = showQueen;
        super.render(queen, yaw, partialTicks, stack, buffer, light);
    }
}
