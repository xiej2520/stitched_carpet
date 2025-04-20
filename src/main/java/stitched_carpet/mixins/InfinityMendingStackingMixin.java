package stitched_carpet.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(InfinityEnchantment.class)
public abstract class InfinityMendingStackingMixin extends Enchantment {

    private InfinityMendingStackingMixin(Weight weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Inject(method = "differs", at = @At("HEAD"), cancellable = true)
    private void modifyDiffers(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (StitchedCarpetSettings.infinityMendingStacking) {
            // Gets rid of infinity/mending exclusive check
            cir.setReturnValue(super.differs(other));
        }
    }
}
