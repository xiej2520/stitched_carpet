package stitched_carpet.mixins;

import net.minecraft.block.ChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {

    @Inject(method = "isChestBlocked", at = @At("HEAD"), cancellable = true)
    private static void ignoreChestBlocked(IWorld world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (StitchedCarpetSettings.alwaysOpenBox == StitchedCarpetSettings.BoxOpenOptions.CHEST || StitchedCarpetSettings.alwaysOpenBox == StitchedCarpetSettings.BoxOpenOptions.BOTH) {
            cir.setReturnValue(false);
        }
    }
}
