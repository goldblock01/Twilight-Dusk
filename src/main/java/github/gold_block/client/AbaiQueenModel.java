package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import twilightforest.entity.boss.SnowQueen;

public class AbaiQueenModel extends HumanoidModel<SnowQueen> {

    private final ModelPart jacket;
    private final ModelPart leftSleeve;
    private final ModelPart rightSleeve;
    private final ModelPart leftPants;
    private final ModelPart rightPants;
    private final ModelPart skirt;

    public AbaiQueenModel(ModelPart root) {
        super(root);
        this.young = false;
        this.jacket = root.getChild("jacket");
        this.leftSleeve = root.getChild("left_sleeve");
        this.rightSleeve = root.getChild("right_sleeve");
        this.leftPants = root.getChild("left_pants");
        this.rightPants = root.getChild("right_pants");
        this.skirt = root.getChild("skirt");
    }

    public static LayerDefinition create() {
        MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, false);
        mesh.getRoot().addOrReplaceChild("skirt", CubeListBuilder.create()
                .texOffs(16, 16)
                .addBox(-4.5F, 10.0F, -2.5F, 9.0F, 14.0F, 5.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        super.renderToBuffer(poseStack, buffer, light, overlay, red, green, blue, alpha);
        this.jacket.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
        this.leftSleeve.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
        this.rightSleeve.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
        this.leftPants.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
        this.rightPants.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
        this.skirt.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(SnowQueen entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.jacket.copyFrom(this.body);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.skirt.copyFrom(this.body);
    }
}
