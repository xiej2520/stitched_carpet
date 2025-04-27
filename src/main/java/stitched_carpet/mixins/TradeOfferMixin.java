package stitched_carpet.mixins;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(TradeOffer.class)
public abstract class TradeOfferMixin {

    @Shadow
    public abstract void resetUses();

    @Inject(method = "use", at = @At("TAIL"))
    private void infiniteTrading(CallbackInfo ci) {
        if (StitchedCarpetSettings.infiniteTrades) {
            this.resetUses();
        }
    }
}
