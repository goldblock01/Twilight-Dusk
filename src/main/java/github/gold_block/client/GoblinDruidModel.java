package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import github.gold_block.entity.GoblinDruid;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;
import java.util.Set;
import net.minecraft.world.entity.HumanoidArm;

/** Approved Blockbench silhouette with articulated robe panels; one unit equals one texture pixel. */
public class GoblinDruidModel extends HumanoidModel<GoblinDruid> {
    public GoblinDruidModel(ModelPart root) { super(root); }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F)
                .texOffs(14, 12).addBox(-4.0F, -5.0F, -3.5F, 8.0F, 1.0F, 7.0F)
                .texOffs(44, 12).addBox(-2.5F, -8.0F, -2.5F, 5.0F, 3.0F, 5.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        head.addOrReplaceChild("hat_upper", CubeListBuilder.create().texOffs(64, 12)
                .addBox(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.2443461F));
        head.addOrReplaceChild("hat_tip", CubeListBuilder.create().texOffs(76, 12)
                .addBox(-0.5F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.2443461F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 4.0F)
                .texOffs(80, 12).addBox(-3.5F, 0.0F, -1.5F, 7.0F, 8.0F, 3.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(20, 0).addBox(-2.0F, 0.0F, -1.5F, 2.0F, 8.0F, 3.0F)
                .texOffs(112, 0).addBox(-2.5F, -2.0F, -2.0F, 3.0F, 8.0F, 4.0F), PartPose.offset(-3.5F, 10.0F, 0.0F));
        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(30, 0).addBox(0.0F, 0.0F, -1.5F, 2.0F, 8.0F, 3.0F)
                .texOffs(0, 12).addBox(-0.5F, -2.0F, -2.0F, 3.0F, 8.0F, 4.0F), PartPose.offset(3.5F, 10.0F, 0.0F));
        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F), PartPose.offset(-2.0F, 16.0F, 0.0F));
        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(52, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F), PartPose.offset(2.0F, 16.0F, 0.0F));
        // Split the old rigid lower robe at the centre seam and attach each half to
        // its leg. The hem now follows walking, retaliation and riding automatically.
        // Disjoint face sets crop the original 8-pixel-wide atlas island exactly;
        // no new texture, stretching or coplanar inner seam faces are needed.
        right_leg.addOrReplaceChild("robe", CubeListBuilder.create()
                .texOffs(88, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,
                        Set.of(Direction.NORTH, Direction.WEST, Direction.DOWN))
                .texOffs(96, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,
                        Set.of(Direction.SOUTH))
                .texOffs(92, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,
                        Set.of(Direction.UP)), PartPose.ZERO);
        left_leg.addOrReplaceChild("robe", CubeListBuilder.create()
                .texOffs(92, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,
                        Set.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.DOWN))
                .texOffs(96, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F,
                        Set.of(Direction.UP)), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(GoblinDruid mob, float swing, float amount, float age, float yaw, float pitch) {
        // HumanoidModel's default animation rewrites joints to player proportions.
        head.yRot = yaw * Mth.DEG_TO_RAD;
        head.xRot = pitch * Mth.DEG_TO_RAD;
        rightLeg.xRot = Mth.cos(swing * 0.6662F) * 0.9F * amount;
        leftLeg.xRot = Mth.cos(swing * 0.6662F + Mth.PI) * 0.9F * amount;
        rightArm.xRot = Mth.cos(swing * 0.6662F + Mth.PI) * 0.7F * amount;
        leftArm.xRot = Mth.cos(swing * 0.6662F) * 0.7F * amount;
        rightArm.yRot = leftArm.yRot = 0.0F;
        rightArm.zRot = 0.025F;
        leftArm.zRot = -0.025F;
        if (!mob.getMainHandItem().isEmpty()) {
            ModelPart hand = mob.getMainArm() == HumanoidArm.RIGHT ? rightArm : leftArm;
            hand.xRot = hand.xRot * 0.5F - (mob.getTarget() != null ? 1.0F : 0.25F);
        }
        if (riding) {
            rightLeg.xRot = leftLeg.xRot = -1.15F;
        }
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack pose) {
        getArm(side).translateAndRotate(pose);
        // Standard held-item layer expects a 12-pixel arm; this goblin has 8-pixel arms.
        pose.translate(0.0F, -4.0F / 16.0F, 0.0F);
    }
}
