package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import github.gold_block.util.DerivedDamageSource;
import github.gold_block.util.EventGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class ToolEvents {

    static float pendingBonus;

    private static final float PHANTOM_SWORD_MAGIC_DAMAGE = 3.0F;
    private static final float PHANTOM_PICKAXE_BONUS_DAMAGE = 2.0F;
    private static final float PHANTOM_AXE_BONUS_DAMAGE = 3.0F;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        EventGuard.run(() -> {
            LivingEntity target = event.getEntity();
            if (target.level().isClientSide()) {
                return;
            }

            DamageSource source = event.getSource();
            if (source instanceof DerivedDamageSource) {
                return;
            }
            pendingBonus = 0.0F;
            if (!(source.getDirectEntity() instanceof LivingEntity attacker)) {
                return;
            }

            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.isEmpty()) {
                return;
            } else if (weapon.is(ModItems.PHANTOM_SWORD.get())) {
                if (target.getArmorValue() > 0) {
                    pendingBonus = PHANTOM_SWORD_MAGIC_DAMAGE;
                }
            } else if (weapon.is(ModItems.PHANTOM_PICKAXE.get())) {
                phantomPickaxe(event, target);
            } else if (weapon.is(ModItems.PHANTOM_AXE.get())) {
                phantomAxe(event, target);
            }
        });
    }

    private static void phantomPickaxe(LivingHurtEvent event, LivingEntity target) {
        if (target.getArmorValue() > 0) {
            event.setAmount(event.getAmount() + PHANTOM_PICKAXE_BONUS_DAMAGE);
        }
    }

    private static void phantomAxe(LivingHurtEvent event, LivingEntity target) {
        if (target.getArmorValue() == 0) {
            event.setAmount(event.getAmount() + PHANTOM_AXE_BONUS_DAMAGE);
        }
    }
}
