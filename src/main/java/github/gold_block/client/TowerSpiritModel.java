package github.gold_block.client;

import github.gold_block.entity.TowerSpirit;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

/** 成年善灵的双层身体与九条触须，使用六十四像素贴图布局。 */
public class TowerSpiritModel extends HierarchicalModel<TowerSpirit> {
    private final ModelPart root;
    private final ModelPart[] tentacles = new ModelPart[9];

    public TowerSpiritModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        ModelPart body = root.getChild("body");
        body.xScale = body.yScale = body.zScale = 0.9375F;
        for (int i = 0; i < 9; i++) this.tentacles[i] = root.getChild("body").getChild("tentacle" + i);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition body = mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-8, -8, -8, 16, 16, 16), PartPose.offset(0, 16, 0));
        body.addOrReplaceChild("inner_body", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-8, -16, -8, 16, 16, 16, new CubeDeformation(-0.5F)), PartPose.offset(0, 8, 0));
        float[] x = {-3.75F, 1.25F, 6.25F, -6.25F, -1.25F, 3.75F, -3.75F, 1.25F, 6.25F};
        int[] lengths = {5, 7, 4, 5, 5, 7, 8, 8, 5};
        for (int i = 0; i < 9; i++) {
            body.addOrReplaceChild("tentacle" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1, 0, -1, 2, lengths[i], 2), PartPose.offset(x[i], 7, (i / 3 - 1) * 5));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override public ModelPart root() { return this.root; }
    @Override public void setupAnim(TowerSpirit spirit, float limbSwing, float limbAmount, float age, float yaw, float pitch) {
        for (int i = 0; i < 9; i++) this.tentacles[i].xRot = 0.2F * Mth.sin(age * 0.3F + i) + 0.4F;
    }
}
