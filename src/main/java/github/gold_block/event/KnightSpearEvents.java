package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.item.KnightSpearItem;
import github.gold_block.util.DerivedDamageSource;
import github.gold_block.util.EventGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class KnightSpearEvents {

    private KnightSpearEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onArmoredTarget(LivingHurtEvent event) {
        EventGuard.run(() -> {
            LivingEntity attacker = spearAttacker(event);
            LivingEntity victim = event.getEntity();
            if (attacker == null || victim.getArmorValue() <= 0) {
                return;
            }
            KnightSpearItem spear = (KnightSpearItem) attacker.getMainHandItem().getItem();
            DamageSource magic = new DerivedDamageSource(attacker.damageSources().indirectMagic(attacker, attacker));
            int invulnerableTime = victim.invulnerableTime;
            victim.invulnerableTime = 0;
            try {
                victim.hurt(magic, spear.getArmoredMagicDamage());
            } finally {
                if (victim.invulnerableTime < invulnerableTime) {
                    victim.invulnerableTime = invulnerableTime;
                }
            }
        });
    }

    private static LivingEntity spearAttacker(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (event.getEntity().level().isClientSide() || event.getAmount() <= 0.0F
                || source instanceof DerivedDamageSource
                || !(source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO))
                || !(source.getDirectEntity() instanceof LivingEntity attacker)
                || attacker == event.getEntity()
                || !(attacker.getMainHandItem().getItem() instanceof KnightSpearItem)) {
            return null;
        }
        return attacker;
    }
}
