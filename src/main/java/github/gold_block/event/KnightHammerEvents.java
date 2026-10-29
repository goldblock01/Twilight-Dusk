package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModEffects;
import github.gold_block.registry.ModItems;
import github.gold_block.util.DerivedDamageSource;
import github.gold_block.util.EventGuard;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class KnightHammerEvents {

    private static final double RADIUS = 2.5D;
    private static final double HALF_HEIGHT = 0.5D;
    private static final int STUN_DURATION = 40;
    private static final Map<Player, Attack> ATTACKS = new WeakHashMap<>();

    private KnightHammerEvents() {
    }

    /** 在重置攻击冷却前，记录本次攻击最终的暴击判定及下落高度。 */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCriticalHit(CriticalHitEvent event) {
        EventGuard.run(() -> {
            Player player = event.getEntity();
            if (player.level().isClientSide()) {
                return;
            }
            ATTACKS.remove(player);
            if (!player.getMainHandItem().is(ModItems.KNIGHT_HAMMER.get())) {
                return;
            }
            boolean critical = event.getResult() == Event.Result.ALLOW
                    || event.getResult() == Event.Result.DEFAULT && event.isVanillaCritical();
            float charge = player.getAttackStrengthScale(0.5F);
            ATTACKS.put(player, new Attack(event.getTarget(), player.level().getGameTime(),
                    Mth.clamp(player.fallDistance - 3.0F, 0.0F, 6.0F), critical,
                    (0.2F + charge * charge * 0.8F) * (critical ? event.getDamageModifier() : 1.0F)));
        });
    }

    /** 高度加成属于普通攻击，在护甲结算之前加入，并沿用攻击冷却及暴击倍率。 */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        EventGuard.run(() -> {
            DamageSource source = event.getSource();
            if (event.getEntity().level().isClientSide() || source instanceof DerivedDamageSource
                    || !source.is(DamageTypes.PLAYER_ATTACK)
                    || !(source.getDirectEntity() instanceof Player player)
                    || !player.getMainHandItem().is(ModItems.KNIGHT_HAMMER.get())) {
                return;
            }
            Attack attack = ATTACKS.get(player);
            if (attack != null && attack.matches(player, event.getEntity()) && !attack.bonusApplied) {
                attack.bonusApplied = true;
                event.setAmount(event.getAmount() + attack.heightBonus * attack.multiplier);
            }
        });
    }

    /** 只有主攻击实际命中后才产生范围伤害；空挥、格挡、无敌帧拒绝命中不会触发。 */
    public static void finishAttack(Player player, LivingEntity target) {
        Attack attack = ATTACKS.remove(player);
        if (attack == null || !attack.matches(player, target) || !attack.critical) {
            return;
        }
        float damage = 2.0F + attack.heightBonus * 0.5F;
        Vec3 origin = player.position();
        // 以玩家脚下位置为中心：直径 5 格、高度 1 格的完整圆柱。
        AABB bounds = new AABB(origin.x - RADIUS, origin.y - HALF_HEIGHT, origin.z - RADIUS,
                origin.x + RADIUS, origin.y + HALF_HEIGHT, origin.z + RADIUS);
        DamageSource source = new DerivedDamageSource(player.damageSources().playerAttack(player));
        for (LivingEntity victim : player.level().getEntitiesOfClass(LivingEntity.class, bounds)) {
            if (victim == player || !victim.isAlive() || player.isAlliedTo(victim)) {
                continue;
            }
            double dx = victim.getX() - origin.x;
            double dz = victim.getZ() - origin.z;
            if (dx * dx + dz * dz > RADIUS * RADIUS) {
                continue;
            }
            // 主目标也承受一次范围物理伤害，避免被刚才普通攻击产生的无敌帧吞掉。
            int invulnerableTime = victim.invulnerableTime;
            if (victim == target) {
                victim.invulnerableTime = 0;
            }
            try {
                if (victim.hurt(source, damage)) {
                    victim.addEffect(new MobEffectInstance(ModEffects.DIZZINESS.get(), STUN_DURATION), player);
                }
            } finally {
                if (victim == target && victim.invulnerableTime < invulnerableTime) {
                    victim.invulnerableTime = invulnerableTime;
                }
            }
        }
    }

    private static final class Attack {
        private final Entity target;
        private final long tick;
        private final float heightBonus;
        private final boolean critical;
        private final float multiplier;
        private boolean bonusApplied;

        private Attack(Entity target, long tick, float heightBonus, boolean critical, float multiplier) {
            this.target = target;
            this.tick = tick;
            this.heightBonus = heightBonus;
            this.critical = critical;
            this.multiplier = multiplier;
        }

        private boolean matches(Player player, Entity victim) {
            return target == victim && tick == player.level().getGameTime();
        }
    }
}
