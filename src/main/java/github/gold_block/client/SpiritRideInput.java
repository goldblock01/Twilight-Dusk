package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.entity.TowerSpirit;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 读取本地驾驶者的上升按键；座驾位置沿用原版的驾驶同步。 */
@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, value = Dist.CLIENT)
public final class SpiritRideInput {
    @SubscribeEvent public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        var minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getVehicle() instanceof TowerSpirit spirit) {
            spirit.setAscending(spirit.getControllingPassenger() == minecraft.player && minecraft.screen == null && minecraft.options.keyJump.isDown());
        }
    }
}
