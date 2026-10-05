package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.entity.DuskFrog;
import github.gold_block.registry.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.entity.monster.MazeSlime;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class FrogEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(event.getSource().getDirectEntity() instanceof DuskFrog frog) || !(victim instanceof MazeSlime)) {
            return;
        }
        if (frog.level().isClientSide()) {
            return;
        }
        int count = 1 + frog.getRandom().nextInt(2);
        victim.spawnAtLocation(new ItemStack(ModItems.MAZE_GEL.get(), count));
    }
}
