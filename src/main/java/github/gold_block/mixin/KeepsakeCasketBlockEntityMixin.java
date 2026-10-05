package github.gold_block.mixin;
import github.gold_block.event.CasketFinalForm;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.block.entity.KeepsakeCasketBlockEntity;

@Mixin(KeepsakeCasketBlockEntity.class)
public class KeepsakeCasketBlockEntityMixin implements CasketFinalForm {

    @Unique
    private static final String FINAL_FORM_TAG = "twilight_dusk_final_form";

    @Unique
    private boolean twilight_dusk$finalForm;

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void twilight_dusk$loadFinalForm(CompoundTag tag, CallbackInfo ci) {
        twilight_dusk$finalForm = tag.getBoolean(FINAL_FORM_TAG);
    }

    @Inject(method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void twilight_dusk$saveFinalForm(CompoundTag tag, CallbackInfo ci) {
        if (twilight_dusk$finalForm) {
            tag.putBoolean(FINAL_FORM_TAG, true);
        }
    }

    @Override
    public boolean twilight_dusk$isFinalForm() {
        return twilight_dusk$finalForm;
    }

    @Override
    public void twilight_dusk$setFinalForm(boolean finalForm) {
        twilight_dusk$finalForm = finalForm;
    }
}
