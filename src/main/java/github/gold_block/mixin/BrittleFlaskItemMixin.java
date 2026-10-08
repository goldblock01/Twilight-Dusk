package github.gold_block.mixin;

import github.gold_block.item.flask.FlaskBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.item.BrittleFlaskItem;

import java.util.List;

@Mixin(BrittleFlaskItem.class)
public abstract class BrittleFlaskItemMixin {

    @Shadow
    public abstract boolean canBreak();

    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$stackPotion(ItemStack stack, ItemStack other, Slot slot, ClickAction action,
                                           Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
        if (action == ClickAction.SECONDARY && other.getItem() instanceof PotionItem) {
            cir.setReturnValue(FlaskBehavior.stackPotion(stack, other, player, this.canBreak()));
        }
    }

    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$barWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(FlaskBehavior.barWidth(stack, this.canBreak()));
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$hideTooltipText(ItemStack stack, Level level, List<Component> tooltip,
                                               TooltipFlag flag, CallbackInfo ci) {
        ci.cancel();
    }

    public int getMaxStackSize(ItemStack stack) {
        return FlaskBehavior.maxStackSize(stack, this.canBreak());
    }
}
