package github.gold_block.item;

import github.gold_block.registry.ModSounds;
import github.gold_block.util.Tooltips;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import twilightforest.util.TwilightItemTier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class KnightSpearItem extends SwordItem {

    public static final float MIN_REACH = 2.0F;
    public static final float MAX_REACH = 4.5F;
    public static final float MIN_CREATIVE_REACH = 2.0F;
    public static final float MAX_CREATIVE_REACH = 6.5F;
    public static final float HITBOX_MARGIN = 0.125F;
    public static final float MINIMUM_ATTACK_CHARGE = 1.0F;
    public static final float KNOCKBACK_MIN_SPEED = 5.1F;
    public static final float DAMAGE_MIN_RELATIVE_SPEED = 4.6F;
    public static final int CONTACT_COOLDOWN_TICKS = 10;
    public static final int KINETIC_HIT_EVENT = 100;

    private static final float KNOCKBACK_BASE = 0.4F;

    private static final Map<UUID, Object2LongMap<UUID>> RECENT_STABS = new HashMap<>();
    private static final Map<UUID, Object2LongMap<UUID>> RECENT_IMPACTS = new HashMap<>();
    private static final Map<UUID, Vec3> LAST_POSITIONS = new HashMap<>();

    private static boolean suppressingKnockback;

    private final ChargeProfile profile;
    private final float armoredMagicDamage;
    private final int stabDurationTicks;

    public KnightSpearItem(Properties properties) {
        this(TwilightItemTier.KNIGHTMETAL, 1, 1.0F, ChargeProfile.knight(), 2.0F, properties);
    }

    protected KnightSpearItem(Tier tier, int bonusDamage, float attackSpeed, ChargeProfile profile,
                              float armoredMagicDamage, Properties properties) {
        super(tier, bonusDamage, attackSpeed - 4.0F, properties);
        this.profile = profile;
        this.armoredMagicDamage = armoredMagicDamage;
        this.stabDurationTicks = 20;
    }

    public static boolean isSuppressingKnockback() {
        return suppressingKnockback;
    }

    public float getArmoredMagicDamage() {
        return armoredMagicDamage;
    }

    public ChargeProfile getProfile() {
        return profile;
    }

    public int getStabDurationTicks() {
        return stabDurationTicks;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isUsingItem()) {
            player.startUsingItem(hand);
            if (!level.isClientSide()) {
                LAST_POSITIONS.remove(player.getUUID());
            }
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.SPEAR_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remaining) {
        if (level.isClientSide() || !(living instanceof Player player)) {
            return;
        }
        this.chargeTick(player, stack, remaining);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int remaining) {
        if (!level.isClientSide()) {
            RECENT_STABS.remove(living.getUUID());
            RECENT_IMPACTS.remove(living.getUUID());
            LAST_POSITIONS.remove(living.getUUID());
        }
    }

    public static boolean isAttackReady(Player player) {
        return player.getAttackStrengthScale(5.0F) >= MINIMUM_ATTACK_CHARGE;
    }

    public void performJab(Player player, ItemStack stack) {
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean hit = false;
        for (Entity target : sweep(player, attackRange(player), target -> true)) {
            hit |= this.stab(player, target, damage, true, true, false);
        }
        player.resetAttackStrengthTicker();
        if (hit) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.SPEAR_HIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(),
                ModSounds.SPEAR_ATTACK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.swing(InteractionHand.MAIN_HAND, false);
    }

    private void chargeTick(Player player, ItemStack stack, int ticksRemaining) {
        Vec3 motion = this.sampledSpeed(player);
        int elapsed = stack.getUseDuration() - ticksRemaining;
        if (elapsed < this.profile.delayTicks()) {
            return;
        }
        int sinceDelay = elapsed - this.profile.delayTicks();
        if (sinceDelay > Math.max(this.profile.damageTicks(),
                Math.max(this.profile.dismountTicks(), this.profile.knockbackTicks()))) {
            return;
        }

        Vec3 look = player.getLookAngle();
        double attackerSpeed = look.dot(motion);

        long now = player.level().getGameTime();
        Object2LongMap<UUID> stabbed = recent(RECENT_STABS, player.getUUID());
        Object2LongMap<UUID> impacted = recent(RECENT_IMPACTS, player.getUUID());
        stabbed.values().removeIf(time -> now - time >= CONTACT_COOLDOWN_TICKS);
        impacted.values().removeIf(time -> now - time >= CONTACT_COOLDOWN_TICKS);

        boolean affected = false;
        for (Entity target : sweep(player, attackRange(player), target -> target.isPickable())) {
            UUID id = target.getUUID();
            boolean recentDamage = stabbed.containsKey(id);
            boolean recentImpact = impacted.containsKey(id);

            double targetSpeed = look.dot(this.motionOf(target));
            double relativeSpeed = Math.max(0.0D, attackerSpeed - targetSpeed);
            boolean dismounts = !recentImpact && this.profile.dismountTicks() >= 0
                    && sinceDelay <= this.profile.dismountTicks()
                    && attackerSpeed >= this.profile.dismountMinSpeed();
            boolean knockback = !recentImpact && this.profile.knockbackTicks() >= 0
                    && sinceDelay <= this.profile.knockbackTicks()
                    && attackerSpeed >= KNOCKBACK_MIN_SPEED
                    && relativeSpeed >= 0.0D;
            boolean dealsDamage = !recentDamage && this.profile.damageTicks() >= 0
                    && sinceDelay <= this.profile.damageTicks()
                    && attackerSpeed >= 0.0D
                    && relativeSpeed >= DAMAGE_MIN_RELATIVE_SPEED;
            if (!dismounts && !knockback && !dealsDamage) {
                continue;
            }
            if (dealsDamage) {
                stabbed.put(id, now);
            }
            if (knockback || dismounts) {
                impacted.put(id, now);
            }

            float amount = (float) player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            if (dealsDamage) {
                amount += (float) Mth.floor(relativeSpeed * this.profile.damageMultiplier());
            }
            affected |= this.stab(player, target, amount, dealsDamage, knockback, dismounts);
        }

        if (affected) {
            player.level().broadcastEntityEvent(player, (byte) KINETIC_HIT_EVENT);
        }
    }

    private Vec3 sampledSpeed(Player player) {
        Vec3 position = player.position();
        Vec3 last = LAST_POSITIONS.put(player.getUUID(), position);
        if (last == null) {
            return player.getDeltaMovement().scale(20.0D);
        }
        Vec3 delta = position.subtract(last);
        if (delta.lengthSqr() > 64.0D) {
            return Vec3.ZERO;
        }
        return delta.scale(20.0D);
    }

    private static Object2LongMap<UUID> recent(Map<UUID, Object2LongMap<UUID>> store, UUID player) {
        return store.computeIfAbsent(player, key -> new Object2LongOpenHashMap<>());
    }

    public boolean stab(Player attacker, Entity target, float damage,
                        boolean dealsDamage, boolean dealsKnockback, boolean dismounts) {
        if (!target.isPickable() || !target.isAlive() || target.isSpectator()) {
            return false;
        }
        ItemStack weapon = attacker.getMainHandItem();
        DamageSource source = attacker.damageSources().playerAttack(attacker);
        float bonus = EnchantmentHelper.getDamageBonus(weapon,
                target instanceof LivingEntity living ? living.getMobType() : MobType.UNDEFINED);
        boolean activeUse = attacker.isUsingItem() && attacker.getUsedItemHand() == InteractionHand.MAIN_HAND;
        float scale = attacker.getAttackStrengthScale(0.5F);
        if (!activeUse) {
            bonus *= scale;
            damage *= 0.2F + scale * scale * 0.8F;
        }

        float total = dealsDamage ? damage + bonus : 0.0F;
        boolean hurt = false;
        if (dealsDamage) {
            suppressingKnockback = true;
            try {
                hurt = target.hurt(source, total);
            } finally {
                suppressingKnockback = false;
            }
        }

        if (dealsKnockback) {
            float strength = KNOCKBACK_BASE + (float) EnchantmentHelper.getKnockbackBonus(attacker) * 0.5F;
            if (strength > 0.0F) {
                float yaw = attacker.getYRot() * Mth.DEG_TO_RAD;
                if (target instanceof LivingEntity livingTarget) {
                    livingTarget.knockback(strength, Mth.sin(yaw), -Mth.cos(yaw));
                } else {
                    target.push(-Mth.sin(yaw) * strength, 0.1D, Mth.cos(yaw) * strength);
                }
                attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                attacker.setSprinting(false);
            }
        }

        boolean dismounted = false;
        if (dismounts && target.isPassenger()) {
            Entity vehicle = target.getVehicle();
            target.stopRiding();
            dismounted = target.getVehicle() != vehicle;
        }

        if (!hurt && !dealsKnockback && !dismounted) {
            return false;
        }

        attacker.setLastHurtMob(target);
        if (target instanceof LivingEntity livingTarget) {
            weapon.hurtEnemy(livingTarget, attacker);
            if (hurt) {
                int fireAspect = EnchantmentHelper.getFireAspect(attacker);
                if (fireAspect > 0 && !livingTarget.isOnFire()) {
                    livingTarget.setSecondsOnFire(fireAspect * 4);
                }
                EnchantmentHelper.doPostHurtEffects(livingTarget, attacker);
                EnchantmentHelper.doPostDamageEffects(attacker, livingTarget);
            }
        }

        attacker.causeFoodExhaustion(0.1F);
        return true;
    }

    private static List<Entity> sweep(LivingEntity attacker, AttackReach reach, Predicate<Entity> filter) {
        Level level = attacker.level();
        Vec3 look = attacker.getLookAngle();
        Vec3 eye = attacker.getEyePosition();
        Vec3 near = eye.add(look.scale(reach.min()));
        double forwardMovement = attacker.getDeltaMovement().dot(look);
        Vec3 far = eye.add(look.scale(reach.max() + Math.max(0.0D, forwardMovement)));

        BlockHitResult blockHit = level.clip(new ClipContext(eye, far,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, attacker));
        if (blockHit.getType() != HitResult.Type.MISS) {
            far = blockHit.getLocation();
            if (eye.distanceToSqr(far) < eye.distanceToSqr(near)) {
                return List.of();
            }
        }

        AABB area = AABB.ofSize(near, HITBOX_MARGIN, HITBOX_MARGIN, HITBOX_MARGIN)
                .expandTowards(far.subtract(near))
                .inflate(1.0D);

        List<Entity> hits = new ArrayList<>();
        for (Entity entity : level.getEntities(attacker, area, filter)) {
            if (!canHit(attacker, entity)) {
                continue;
            }
            AABB box = entity.getBoundingBox();
            if (box.contains(near)) {
                hits.add(entity);
                continue;
            }
            if (box.clip(near, far).isPresent()) {
                hits.add(entity);
                continue;
            }
            Optional<Vec3> outsideHit = box.inflate(HITBOX_MARGIN).clip(near, far);
            if (outsideHit.isEmpty()) {
                continue;
            }
            Vec3 outsidePosition = outsideHit.get();
            Vec3 towardsTarget = box.getCenter();
            BlockHitResult wallHit = level.clip(new ClipContext(outsidePosition, towardsTarget,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, attacker));
            if (wallHit.getType() != HitResult.Type.MISS) {
                towardsTarget = wallHit.getLocation();
            }
            if (box.clip(outsidePosition, towardsTarget).isPresent()) {
                hits.add(entity);
            }
        }
        return hits;
    }

    public static AttackReach attackRange(Player player) {
        return new AttackReach(minReach(player), maxReach(player));
    }

    public static float minReach(Player player) {
        return player.isCreative() ? MIN_CREATIVE_REACH : MIN_REACH;
    }

    public static float maxReach(Player player) {
        return player.isCreative() ? MAX_CREATIVE_REACH : MAX_REACH;
    }

    public record AttackReach(float min, float max) {
    }

    private static boolean canHit(LivingEntity attacker, Entity target) {
        if (target == attacker || target.isSpectator() || !target.isAlive()) {
            return false;
        }
        if (!target.isPickable()) {
            return false;
        }
        if (target instanceof Player targetPlayer) {
            if (!(attacker instanceof Player attackerPlayer) || !attackerPlayer.canHarmPlayer(targetPlayer)) {
                return false;
            }
        }
        return !attacker.isPassengerOfSameVehicle(target);
    }

    private Vec3 motionOf(Entity entity) {
        Entity root = entity instanceof Player || !entity.isPassenger() ? entity : entity.getRootVehicle();
        return root.getDeltaMovement().scale(20.0D);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }

    public record ChargeProfile(int delayTicks, float damageMultiplier,
                                int dismountTicks, int knockbackTicks, int damageTicks,
                                float dismountMinSpeed) {

        public static ChargeProfile of(float delay, float dismountTime, float dismountSpeed,
                                       float knockbackTime, float damageTime, float multiplier) {
            return new ChargeProfile((int) (delay * 20.0F), multiplier,
                    (int) (dismountTime * 20.0F), (int) (knockbackTime * 20.0F),
                    (int) (damageTime * 20.0F), dismountSpeed);
        }

        public static ChargeProfile knight() {
            return of(0.4F, 2.5F, 7.0F, 5.5F, 8.75F, 0.95F);
        }
    }
}
