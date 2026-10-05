package github.gold_block.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import github.gold_block.entity.DuskTadpole;

public class DuskTadpoleModel extends AgeableListModel<DuskTadpole> {

    private final ModelPart root;
    private final ModelPart tail;

    public DuskTadpoleModel(ModelPart part) {
        super(true, 8.0F, 3.35F);
        this.root = part;
        this.tail = part.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 22.0F, -3.0F));
        part.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 7.0F),
                PartPose.offset(0.0F, 22.0F, 0.0F));
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.root);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.tail);
    }

    @Override
    public void setupAnim(DuskTadpole entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float speed = entity.isInWater() ? 1.0F : 1.5F;
        this.tail.yRot = -speed * 0.25F * Mth.sin(0.3F * ageInTicks);
    }
}
