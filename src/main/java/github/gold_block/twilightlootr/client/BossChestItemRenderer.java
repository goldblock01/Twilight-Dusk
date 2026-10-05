package github.gold_block.twilightlootr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import github.gold_block.twilightlootr.init.TLBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BossChestItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static BossChestItemRenderer instance;

    private final BlockEntityRenderDispatcher dispatcher;
    private final TFLootrBossChestBlockEntity chest;

    private BossChestItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
        super(dispatcher, models);
        this.dispatcher = dispatcher;
        this.chest = new TFLootrBossChestBlockEntity(BlockPos.ZERO, TLBlocks.BOSS_CHEST.get().defaultBlockState());
    }

    public static BossChestItemRenderer getInstance() {
        if (instance == null) {
            instance = new BossChestItemRenderer(
                    Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                    Minecraft.getInstance().getEntityModels());
        }
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.dispatcher.renderItem(this.chest, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
