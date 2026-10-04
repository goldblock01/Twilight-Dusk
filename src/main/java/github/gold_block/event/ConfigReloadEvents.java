package github.gold_block.event;

import github.gold_block.Config;
import github.gold_block.TwilightDusk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class ConfigReloadEvents {

    private static final int CHECK_INTERVAL = 100;

    private ConfigReloadEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % CHECK_INTERVAL != 0) {
            return;
        }
        Config.reloadIfFileChanged();
    }
}
