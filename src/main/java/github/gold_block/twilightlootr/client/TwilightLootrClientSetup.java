package github.gold_block.twilightlootr.client;

import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.init.TLBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TwilightLootrClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (TLBlockEntities.BOSS_CHEST.isPresent()) {
            event.registerBlockEntityRenderer(TLBlockEntities.BOSS_CHEST.get(), TFLootrBossChestRenderer::new);
        }
    }
}
