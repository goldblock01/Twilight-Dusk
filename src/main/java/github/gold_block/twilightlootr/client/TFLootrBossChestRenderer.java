package github.gold_block.twilightlootr.client;

import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.ChestType;

public class TFLootrBossChestRenderer extends ChestRenderer<TFLootrBossChestBlockEntity> {

    public static final Material MATERIAL = new Material(Sheets.CHEST_SHEET,
            new ResourceLocation("twilight_dusk", "entity/boss_chest"));
    public static final Material MATERIAL_OPENED = new Material(Sheets.CHEST_SHEET,
            new ResourceLocation("twilight_dusk", "entity/boss_chest_opened"));
    public static final Material MATERIAL_INVALID = new Material(Sheets.CHEST_SHEET,
            new ResourceLocation("twilight_dusk", "entity/boss_chest_invalid"));

    public TFLootrBossChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected Material getMaterial(TFLootrBossChestBlockEntity chest, ChestType type) {
        if (chest.isOpened()) {
            return MATERIAL_OPENED;
        }
        Player player = Minecraft.getInstance().player;
        if (player != null && chest.getOpeners().contains(player.getUUID())) {
            return MATERIAL_OPENED;
        }
        return MATERIAL;
    }
}
