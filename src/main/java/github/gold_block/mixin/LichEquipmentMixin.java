package github.gold_block.mixin;

import github.gold_block.util.LichEquipment;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.entity.boss.Lich;

@Mixin(Lich.class)
public abstract class LichEquipmentMixin {
    /** 阶段和分身状态读取完成后恢复专属武器。 */
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void twilight_dusk$restoreSword(CompoundTag tag, CallbackInfo ci) {
        LichEquipment.restore((Lich) (Object) this);
    }
}
