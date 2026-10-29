package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.item.KnightSpearItem;
import github.gold_block.util.DerivedDamageSource;
import github.gold_block.util.EventGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class KnightSpearEvents {

    private static final float SPRINT_BONUS = 0.25F;

    private KnightSpearEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onSprintAttack(LivingHurtEvent event) {
        EventGuard.run(() -> {
            LivingEntity attacker = spearAttacker(event);
            if (attacker instanceof Player player && player.isSprinting()) {
                event.setAmount(event.getAmount() * (1.0F + SPRINT_BONUS));
            }
        });
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
                // 固定的额外魔法伤害单独结算，不跟随冲刺倍率再次放大。
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
