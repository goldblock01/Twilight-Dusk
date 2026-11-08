package github.gold_block.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import twilightforest.entity.monster.SkeletonDruid;
import twilightforest.entity.projectile.NatureBolt;
import twilightforest.init.TFSounds;
import twilightforest.init.TFItems;

/** A neutral, living stronghold goblin using Twilight Forest's druid spellcasting. */
public class GoblinDruid extends SkeletonDruid {
    public GoblinDruid(EntityType<? extends GoblinDruid> type, Level level) {
        super(type, level);
        // Also equip /summon-created mobs, which do not always finalize spawning.
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TFItems.IRONWOOD_HOE.get()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SkeletonDruid.createAttributes().add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Keep wandering and druid spellcasting, without undead sun avoidance.
        goalSelector.removeAllGoals(goal -> goal instanceof RestrictSunGoal || goal instanceof FleeSunGoal);
        // Retain HurtByTargetGoal only: do not hunt players, golems or turtles on sight.
        targetSelector.removeAllGoals(goal -> goal instanceof NearestAttackableTargetGoal<?>);
    }

    @Override
    public void reassessWeaponGoal() {
        if (!level().isClientSide && getMainHandItem().getItem() instanceof HoeItem) {
            // The skeleton constructor first installs melee AI while the hand is empty.
            // It must not compete with the inherited druid's ranged goal after equipping a hoe.
            goalSelector.removeAllGoals(goal -> goal instanceof MeleeAttackGoal || goal instanceof RangedBowAttackGoal<?>);
        }
        super.reassessWeaponGoal();
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TFItems.IRONWOOD_HOE.get()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Upgrade the previous version's default weapon in existing worlds, too.
        if (getMainHandItem().is(Items.GOLDEN_HOE)) {
            ItemStack replacement = new ItemStack(TFItems.IRONWOOD_HOE.get());
            if (getMainHandItem().hasTag()) replacement.setTag(getMainHandItem().getTag().copy());
            setItemSlot(EquipmentSlot.MAINHAND, replacement);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // SkeletonDruid's step event aliases minecraft:entity.skeleton.step.
        // Use the actual block's footsteps instead of the skeleton's bone rattle.
        SoundType sound = state.getSoundType(level(), pos, this);
        playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (!(getMainHandItem().getItem() instanceof HoeItem)) {
            super.performRangedAttack(target, power);
            return;
        }
        // Reuse the actual TF projectile: its damage, poison and terrain rules remain in TF.
        NatureBolt bolt = new NatureBolt(level(), this);
        double dx = target.getX() - getX();
        double dz = target.getZ() - getZ();
        // Adapt the vanilla druid's fixed vertical offset to this smaller caster's release point.
        double dy = target.getY(0.3333333333333333D) - bolt.getY();
        bolt.shoot(dx, dy + Math.sqrt(dx * dx + dz * dz) * 0.2D, dz, 0.6F, 6.0F);
        playSound(TFSounds.SKELETON_DRUID_SHOOT.get(), 1.0F,
                1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
        level().addFreshEntity(bolt);
    }

    @Override
    protected SoundEvent getAmbientSound() { return TFSounds.GOBLIN_KNIGHT_AMBIENT.get(); }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return TFSounds.GOBLIN_KNIGHT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() { return TFSounds.GOBLIN_KNIGHT_DEATH.get(); }

    @Override
    public MobType getMobType() { return MobType.UNDEFINED; }

    @Override
    protected boolean isSunBurnTick() { return false; }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        // The eyes are below the hat, about 18.5 pixels above the ground.
        return dimensions.height * (18.5F / 22.0F);
    }
}
