package github.gold_block.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.gold_block.entity.TowerSpirit;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

/** 按挽具原版布局绘制束带与护目镜，驾驶时将护目镜放下。 */
public class SpiritHarnessLayer extends RenderLayer<TowerSpirit, TowerSpiritModel> {
    public static final net.minecraft.client.model.geom.ModelLayerLocation LAYER = new net.minecraft.client.model.geom.ModelLayerLocation(
            new ResourceLocation("twilight_dusk", "tower_spirit_harness"), "main");
    private final HarnessModel harness;
    public SpiritHarnessLayer(RenderLayerParent<TowerSpirit, TowerSpiritModel> parent, ModelPart root) { super(parent); this.harness = new HarnessModel(root); }
    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition(); var root = mesh.getRoot();
        root.addOrReplaceChild("harness", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-8, -16, -8, 16, 16, 16), PartPose.offset(0, 24, 0));
        root.addOrReplaceChild("goggles", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-8, -2.5F, -2.5F, 16, 5, 5, new CubeDeformation(0.15F)), PartPose.offset(0, 14, -5.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }
    @Override public void render(PoseStack pose, MultiBufferSource buffer, int light, TowerSpirit spirit,
                                  float swing, float amount, float partial, float age, float yaw, float pitch) {
        if (spirit.isBaby() || !spirit.hasHarness()) return;
        harness.setupAnim(spirit, swing, amount, age, yaw, pitch);
        ResourceLocation texture = new ResourceLocation("twilight_dusk", "textures/entity/harness/"
                + DyeColor.byId(spirit.getHarnessColor()).getName() + "_harness.png");
        harness.renderToBuffer(pose, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)), light,
                OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
    private static class HarnessModel extends EntityModel<TowerSpirit> {
        private final ModelPart root, goggles;
        HarnessModel(ModelPart root) { this.root = root; this.goggles = root.getChild("goggles"); }
        @Override public void setupAnim(TowerSpirit spirit, float swing, float amount, float age, float yaw, float pitch) {
            goggles.xRot = spirit.isVehicle() ? 0 : -0.7854F;
            goggles.y = spirit.isVehicle() ? 14 : 9;
        }
        @Override public void renderToBuffer(PoseStack pose, VertexConsumer vertex, int light, int overlay,
                                             float red, float green, float blue, float alpha) {
            root.render(pose, vertex, light, overlay, red, green, blue, alpha);
        }
    }
}
