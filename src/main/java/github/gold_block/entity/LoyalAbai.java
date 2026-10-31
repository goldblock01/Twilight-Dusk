package github.gold_block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.entity.monster.LoyalZombie;
import twilightforest.init.TFMobEffects;

public class LoyalAbai extends LoyalZombie {

    private static final String OWNER_NAME = "syhxgolden";
    private static final int FROST_TICKS = 60;

    private boolean retaliating;

    public LoyalAbai(EntityType<? extends LoyalAbai> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(TFMobEffects.FROSTY.get(), FROST_TICKS, 0), this);
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide() && !this.retaliating && this.shouldRetaliate(source)) {
            this.retaliating = true;
            try {
                this.swing(InteractionHand.MAIN_HAND, true);
                this.doHurtTarget(source.getEntity());
            } finally {
                this.retaliating = false;
            }
        }
        return hurt;
    }

    private boolean shouldRetaliate(DamageSource source) {
        if (!(source.getEntity() instanceof Player player) || player != this.getOwner()) {
            return false;
        }
        return !player.getName().getString().equalsIgnoreCase(OWNER_NAME);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundType sound = state.getSoundType(this.level(), pos, this);
        this.playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
    }
}
