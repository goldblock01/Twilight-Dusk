package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import github.gold_block.registry.ModTags;
import github.gold_block.util.CurioUtil;
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
    private static final float BONUS_DAMAGE_RATIO = 1.2F;
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
            attack(event, victim, source);
            defence(event, victim, source);
        });
    }

    private static void attack(LivingHurtEvent event, LivingEntity victim, DamageSource source) {
        if (source.is(FROST_CONVERSION)) {
            return;
        }
        if (!(source.getDirectEntity() instanceof LivingEntity attacker) || attacker == victim) {
            return;
        }
        if (CurioUtil.isWearing(attacker, ModItems.KNIGHT_MEDAL.get())) {
            if (victim.getArmorValue() > 0) {
                event.setAmount(event.getAmount() + event.getAmount() * BONUS_DAMAGE_RATIO);
            }
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOWNESS_DURATION, 0), attacker);
            victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, 0), attacker);
        }
        if (CurioUtil.isWearing(attacker, ModItems.BLAZING_MEDAL.get()) && victim.isOnFire()) {
            event.setAmount(event.getAmount() + event.getAmount() * BONUS_DAMAGE_RATIO);
        }
        if (CurioUtil.isWearing(attacker, ModItems.FRIGID_MEDAL.get())) {
            convertToFrost(event, victim, attacker);
        }
    }

    private static void convertToFrost(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker) {
        float converted = event.getAmount() * FROST_CONVERSION_RATIO;
        if (converted <= 0.0F) {
            return;
        }
        event.setAmount(event.getAmount() - converted);
        Holder<DamageType> holder = victim.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(FROST_CONVERSION);
        int invulnerableTime = victim.invulnerableTime;
        victim.invulnerableTime = 0;
        victim.hurt(new DamageSource(holder, attacker, attacker), converted);
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
