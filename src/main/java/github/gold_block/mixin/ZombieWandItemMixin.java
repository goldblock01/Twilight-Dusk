package github.gold_block.mixin;

import github.gold_block.entity.LoyalAbai;
import github.gold_block.registry.ModEntities;
import github.gold_block.util.AbaiQueenNames;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.init.TFSounds;
import twilightforest.item.ZombieWandItem;

@Mixin(ZombieWandItem.class)
public class ZombieWandItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void twilightDusk$summonLoyalAbai(Level level, Player player, InteractionHand hand,
                                              CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (!AbaiQueenNames.isAbaiScepter(stack) || level.isClientSide()) {
            return;
        }
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
            return;
        }
        BlockHitResult result = (BlockHitResult) player.pick(4.5D, 0.0F, false);
        if (result.getType() == HitResult.Type.MISS) {
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, false));
            return;
        }
        LoyalAbai abai = ModEntities.LOYAL_ABAI.get().create(level);
        if (abai == null) {
            return;
        }
        abai.moveTo(result.getLocation());
        if (!level.noCollision(abai, abai.getBoundingBox())) {
            cir.setReturnValue(InteractionResultHolder.pass(stack));
            return;
        }
        abai.spawnAnim();
        abai.setTame(true);
        abai.setOwnerUUID(player.getUUID());
        abai.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1));
        level.addFreshEntity(abai);
        level.gameEvent(player, GameEvent.ENTITY_PLACE, result.getBlockPos());
        stack.hurt(1, level.getRandom(), null);
        abai.playSound(TFSounds.LOYAL_ZOMBIE_SUMMON.get(), 1.0F, abai.getVoicePitch());
        cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, false));
    }
}
