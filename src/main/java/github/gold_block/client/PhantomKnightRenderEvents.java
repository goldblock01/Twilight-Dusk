package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.util.PhantomKnightEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.entity.boss.KnightPhantom;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, value = Dist.CLIENT)
public final class PhantomKnightRenderEvents {

    private static final Map<LivingEntity, ItemStack> HELD = new WeakHashMap<>();

    @SubscribeEvent
    public static void onRenderPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity knight = event.getEntity();
        if (!(knight instanceof KnightPhantom)) {
            return;
        }
        Item phantom = PhantomKnightEquipment.phantomOf(knight.getMainHandItem());
        if (phantom == null) {
            return;
        }
        HELD.putIfAbsent(knight, knight.getMainHandItem());
        knight.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(phantom));
    }

    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
        ItemStack held = HELD.remove(event.getEntity());
        if (held != null) {
            event.getEntity().setItemSlot(EquipmentSlot.MAINHAND, held);
        }
    }
}
