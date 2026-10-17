package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.util.AbaiQueenNames;
import github.gold_block.util.EventGuard;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.entity.boss.SnowQueen;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class SnowQueenEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EventGuard.run(() -> {
            if (!(event.getEntity() instanceof SnowQueen queen) || queen.level().isClientSide()) {
                return;
            }
            if (AbaiQueenNames.isNamed(queen)) {
                queen.spawnAtLocation(abaiPorkchop());
            }
        });
    }

    private static ItemStack abaiPorkchop() {
        ItemStack porkchop = new ItemStack(Items.PORKCHOP);
        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("冰冰冰"))));
        porkchop.getOrCreateTagElement("display").put("Lore", lore);
        return porkchop;
    }
}
