package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import github.gold_block.registry.ModTags;
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
public final class MagicSwordEvents {

    private static final float MAGIC_CONVERSION_RATIO = 0.25F;
    private static final float MAGIC_DAMAGE_REDUCTION = 0.2F;

    private MagicSwordEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        EventGuard.run(() -> {
            LivingEntity victim = event.getEntity();
            if (victim.level().isClientSide()) {
                return;
            }
            DamageSource source = event.getSource();
            // 减伤也处理追加的魔法伤害；只将攻击侧的转换排除在递归之外。
            if (source.is(ModTags.MAGIC) && victim.getMainHandItem().is(ModItems.MAGIC_SWORD.get())) {
                event.setAmount(event.getAmount() * (1.0F - MAGIC_DAMAGE_REDUCTION));
            }
            if (source instanceof DerivedDamageSource
                    || !(source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)
                    || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO))
                    || !(source.getDirectEntity() instanceof LivingEntity attacker)
                    || attacker == victim || !attacker.getMainHandItem().is(ModItems.MAGIC_SWORD.get())) {
                return;
            }
            float converted = event.getAmount() * MAGIC_CONVERSION_RATIO;
            if (converted <= 0.0F) {
                return;
            }
            // 从物理部分扣除同等数值，转换不会凭空多加一份伤害。
            event.setAmount(event.getAmount() - converted);
            DamageSource magic = new DerivedDamageSource(attacker.damageSources().indirectMagic(attacker, attacker));
            int invulnerableTime = victim.invulnerableTime;
            victim.invulnerableTime = 0;
            try {
                victim.hurt(magic, converted);
            } finally {
                if (victim.invulnerableTime < invulnerableTime) {
                    victim.invulnerableTime = invulnerableTime;
                }
            }
        });
    }
}
