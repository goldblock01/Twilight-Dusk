package github.gold_block.mixin;

import github.gold_block.util.LichEquipment;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.Lich;

@Mixin(Mob.class)
public abstract class LichEquipmentSlotMixin {
    /** 在装备写入时替换金剑，覆盖近战启动与权杖切回武器两个入口。 */
    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void twilight_dusk$lichSword(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if ((Object) this instanceof Lich lich && LichEquipment.shouldReplace(lich, slot, stack)) {
            lich.setItemSlot(slot, LichEquipment.createSword());
            lich.setDropChance(slot, 0.0F);
            ci.cancel();
        }
    }
}
