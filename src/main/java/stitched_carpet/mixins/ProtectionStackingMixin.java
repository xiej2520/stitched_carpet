package stitched_carpet.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionStackingMixin extends Enchantment {

    private ProtectionStackingMixin(Weight weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Inject(method = "differs", at = @At("HEAD"), cancellable = true)
    private void modifyDiffers(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        // Gets rid of protection exclusive check
        if (StitchedCarpetSettings.protectionStacking) {
            cir.setReturnValue(super.differs(other));
        }
    }
}
