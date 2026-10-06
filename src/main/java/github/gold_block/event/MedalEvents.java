package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import github.gold_block.registry.ModTags;
import github.gold_block.util.CurioUtil;
import github.gold_block.util.DerivedDamageSource;
import github.gold_block.util.EventGuard;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class MedalEvents {

    private static final ResourceKey<DamageType> FROST_CONVERSION = ResourceKey.create(Registries.DAMAGE_TYPE,
            new ResourceLocation(TwilightDusk.MODID, "frost_conversion"));

    private static final float FROST_CONVERSION_RATIO = 0.2F;
    private static final float FROST_RESISTANCE = 0.7F;
    private static final float CLIMATE_RESISTANCE = 0.1F;
    private static final float BONUS_DAMAGE = 0.2F;
    private static final int SLOWNESS_DURATION = 100;
    private static final int WEAKNESS_DURATION = 60;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EventGuard.run(() -> {
            LivingEntity victim = event.getEntity();
            if (victim.level().isClientSide()) {
                return;
            }
            DamageSource source = event.getSource();
            if (source instanceof DerivedDamageSource) {
                return;
            }
            float converted = 0.0F;
            if (source.getDirectEntity() instanceof LivingEntity attacker && attacker != victim) {
                converted = attack(event, victim, attacker);
            }
            defence(event, victim, source);
            settle(victim, source, converted);
        });
    }

    private static float attack(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker) {
        if (CurioUtil.isWearing(attacker, ModItems.KNIGHT_MEDAL.get())) {
            if (victim.getArmorValue() > 0) {
                event.setAmount(event.getAmount() * (1.0F + BONUS_DAMAGE));
            }
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0), attacker);
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, 0), attacker);
        }
        if (CurioUtil.isWearing(attacker, ModItems.BLAZING_MEDAL.get()) && victim.isOnFire()) {
            event.setAmount(event.getAmount() * (1.0F + BONUS_DAMAGE));
        }
        float converted = 0.0F;
        if (CurioUtil.isWearing(attacker, ModItems.FRIGID_MEDAL.get())) {
            converted = event.getAmount() * FROST_CONVERSION_RATIO;
            if (converted > 0.0F) {
                event.setAmount(event.getAmount() - converted);
            }
        }
        return converted;
    }

    private static void settle(LivingEntity victim, DamageSource source, float converted) {
        float magic = ToolEvents.pendingBonus;
        ToolEvents.pendingBonus = 0.0F;
        float extra = converted + magic;
        if (extra <= 0.0F || !(source.getDirectEntity() instanceof LivingEntity attacker)) {
            return;
        }
        DamageSource strike;
        if (converted > 0.0F) {
            Holder<DamageType> holder = victim.level().registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(FROST_CONVERSION);
            strike = new DerivedDamageSource(holder, attacker, attacker);
        } else {
            strike = new DerivedDamageSource(attacker.damageSources().indirectMagic(attacker, attacker));
        }
        int invulnerableTime = victim.invulnerableTime;
        victim.invulnerableTime = 0;
        victim.hurt(strike, extra);
        if (victim.invulnerableTime < invulnerableTime) {
            victim.invulnerableTime = invulnerableTime;
        }
    }

    private static void defence(LivingHurtEvent event, LivingEntity victim, DamageSource source) {
        if (CurioUtil.isWearing(victim, ModItems.FRIGID_MEDAL.get())) {
            if (source.is(ModTags.FREEZING)) {
                event.setAmount(event.getAmount() * (1.0F - FROST_RESISTANCE));
            }
            if (inBiome(victim, ModTags.COLD_BIOMES)) {
                event.setAmount(event.getAmount() * (1.0F - CLIMATE_RESISTANCE));
            }
        }
        if (CurioUtil.isWearing(victim, ModItems.BLAZING_MEDAL.get()) && inBiome(victim, ModTags.HOT_BIOMES)) {
            event.setAmount(event.getAmount() * (1.0F - CLIMATE_RESISTANCE));
        }
    }

    private static boolean inBiome(LivingEntity entity, TagKey<Biome> tag) {
        return entity.level().getBiome(entity.blockPosition()).is(tag);
    }
}
