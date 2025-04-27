package stitched_carpet.mixins;

import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;

// EnchantmentTarget.DIGGER
@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$13")
public class EnchantmentTargetDIGGER {

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    public void hoeAcceptableDigger(Item item, CallbackInfoReturnable<Boolean> cir) {
        if (StitchedCarpetSettings.hoeMiningTool && item instanceof HoeItem) {
            cir.setReturnValue(true);
        }
    }
}
