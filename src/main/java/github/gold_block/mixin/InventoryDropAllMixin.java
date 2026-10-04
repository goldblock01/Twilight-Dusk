package github.gold_block.mixin;

import github.gold_block.registry.ModTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryDropAllMixin {

    @Inject(method = "dropAll", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$dropAllExceptKeptOnDeath(CallbackInfo ci) {
        Inventory inventory = (Inventory) (Object) this;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || stack.is(ModTags.KEPT_ON_DEATH)) {
                continue;
            }
            inventory.player.drop(stack, true, false);
            inventory.setItem(slot, ItemStack.EMPTY);
        }
        ci.cancel();
    }
}
