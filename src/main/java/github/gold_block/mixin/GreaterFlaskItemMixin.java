package github.gold_block.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.item.GreaterFlaskItem;

import java.util.List;

@Mixin(GreaterFlaskItem.class)
public abstract class GreaterFlaskItemMixin {

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$hideTooltipText(ItemStack stack, Level level, List<Component> tooltip,
                                               TooltipFlag flag, CallbackInfo ci) {
        ci.cancel();
    }
}
