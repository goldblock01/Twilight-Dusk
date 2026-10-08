package github.gold_block.mixin;

import github.gold_block.registry.ModItems;
import github.gold_block.util.PhantomKnightEquipment;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import twilightforest.entity.boss.KnightPhantom;

@Mixin(KnightPhantom.class)
public abstract class KnightPhantomMixin {

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void twilight_dusk$equip(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        PhantomKnightEquipment.upgrade((KnightPhantom) (Object) this);
    }

    @Inject(method = "setNumber", at = @At("TAIL"), remap = false)
    private void twilight_dusk$setNumber(int number, CallbackInfo ci) {
        PhantomKnightEquipment.upgrade((KnightPhantom) (Object) this);
    }

    @Inject(method = "isSwordKnight", at = @At("RETURN"), cancellable = true, remap = false)
    private void twilight_dusk$isSwordKnight(CallbackInfoReturnable<Boolean> cir) {
        phantom(cir, ModItems.PHANTOM_SWORD);
    }

    @Inject(method = "isAxeKnight", at = @At("RETURN"), cancellable = true, remap = false)
    private void twilight_dusk$isAxeKnight(CallbackInfoReturnable<Boolean> cir) {
        phantom(cir, ModItems.PHANTOM_AXE);
    }

    @Inject(method = "isPickKnight", at = @At("RETURN"), cancellable = true, remap = false)
    private void twilight_dusk$isPickKnight(CallbackInfoReturnable<Boolean> cir) {
        phantom(cir, ModItems.PHANTOM_PICKAXE);
    }

    private void phantom(CallbackInfoReturnable<Boolean> cir, RegistryObject<Item> weapon) {
        if (!cir.getReturnValue() && PhantomKnightEquipment.holds((KnightPhantom) (Object) this, weapon)) {
            cir.setReturnValue(true);
        }
    }
}
