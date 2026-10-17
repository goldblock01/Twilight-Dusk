package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import github.gold_block.util.EventGuard;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class NagaCurvedSwordEvents {

    private static final float PARRY_CHANCE = 0.1F;
    private static final float PARRY_REDUCTION = 0.1F;
    private static final float SPRINT_BONUS = 0.1F;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        EventGuard.run(() -> {
            LivingEntity target = event.getEntity();
            if (target.level().isClientSide()) {
                return;
            }

            if (target.getMainHandItem().is(ModItems.NAGA_CURVED_SWORD.get())
                    && target.getRandom().nextFloat() < PARRY_CHANCE) {
                event.setAmount(event.getAmount() * (1.0F - PARRY_REDUCTION));
            }

            if (event.getSource().getDirectEntity() instanceof Player attacker
                    && attacker.isSprinting()
                    && attacker.getMainHandItem().is(ModItems.NAGA_CURVED_SWORD.get())) {
                event.setAmount(event.getAmount() * (1.0F + SPRINT_BONUS));
            }
        });
    }
}
