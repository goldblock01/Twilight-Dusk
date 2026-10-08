package github.gold_block.mixin;

import github.gold_block.item.flask.FlaskBehavior;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.item.BrittleFlaskItem;

import java.util.Optional;

@Mixin(Item.class)
public abstract class ItemTooltipMixin {

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$flaskTooltip(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if ((Object) this instanceof BrittleFlaskItem flask) {
            cir.setReturnValue(FlaskBehavior.tooltip(stack, flask.canBreak()));
        }
    }
}
