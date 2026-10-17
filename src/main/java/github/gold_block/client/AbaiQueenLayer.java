package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import twilightforest.TwilightForestMod;
import twilightforest.client.model.entity.SnowQueenModel;
import twilightforest.client.model.entity.newmodels.NewSnowQueenModel;
import twilightforest.entity.boss.SnowQueen;

public class AbaiQueenLayer extends RenderLayer<SnowQueen, HumanoidModel<SnowQueen>> {

    private static final ResourceLocation CROWN_TEXTURE = TwilightForestMod.getModelTexture("snowqueen.png");

    private final AbaiQueenModel skin;
    private final ModelPart crown;

    public AbaiQueenLayer(RenderLayerParent<SnowQueen, HumanoidModel<SnowQueen>> parent) {
        super(parent);
        this.skin = new AbaiQueenModel(AbaiQueenModel.create().bakeRoot());
        this.crown = AbaiQueenSkin.jappa()
                ? NewSnowQueenModel.create().bakeRoot().getChild("hat")
                : SnowQueenModel.create().bakeRoot().getChild("head");
        this.crown.skipDraw = true;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, SnowQueen queen,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (!AbaiQueenSkin.isAbaiQueen(queen)) {
            return;
        }

        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(queen.hurtTime > 0));
        VertexConsumer skinBuffer = buffer.getBuffer(RenderType.entityTranslucent(AbaiQueenSkin.TEXTURE));
        this.skin.setupAnim(queen, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.skin.renderToBuffer(poseStack, skinBuffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        ModelPart head = this.skin.head;
        this.crown.x = head.x;
        this.crown.y = head.y;
        this.crown.z = head.z;
        this.crown.xRot = head.xRot;
        this.crown.yRot = head.yRot;
        this.crown.zRot = head.zRot;
        this.crown.render(poseStack,
                buffer.getBuffer(RenderType.entityCutoutNoCull(CROWN_TEXTURE)),
                light, overlay);
    }
}
