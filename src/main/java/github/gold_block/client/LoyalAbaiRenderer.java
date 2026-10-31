package github.gold_block.client;

import github.gold_block.entity.LoyalAbai;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LoyalAbaiRenderer extends HumanoidMobRenderer<LoyalAbai, PlayerModel<LoyalAbai>> {

    public LoyalAbaiRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(LoyalAbai abai) {
        return AbaiQueenSkin.TEXTURE;
    }

    @Override
    protected RenderType getRenderType(LoyalAbai abai, boolean visible, boolean translucent, boolean glowing) {
        return visible ? RenderType.entityTranslucent(AbaiQueenSkin.TEXTURE) : null;
    }
}
