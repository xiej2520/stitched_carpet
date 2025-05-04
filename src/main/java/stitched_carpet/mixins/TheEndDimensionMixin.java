package stitched_carpet.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.TheEndDimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;
import stitched_carpet.util.BlockPosParser;

@Mixin(TheEndDimension.class)
public abstract class TheEndDimensionMixin {

    @Inject(method = "getForcedSpawnPoint", at = @At("HEAD"), cancellable = true)
    public void getForcedSpawnPoint(CallbackInfoReturnable<BlockPos> cir) {
        if (!StitchedCarpetSettings.endPlatformSpawnPoint.equals("default")) {
            // set return value if optional value present
            BlockPosParser.parseBlockPos(StitchedCarpetSettings.endPlatformSpawnPoint).ifPresent(cir::setReturnValue);
        }
    }

    //@Mutable
    //@Shadow
    //@Final
    //public static BlockPos SPAWN_POINT;
    //@Inject(method = "<clinit>", at = @At("TAIL"))
    //private static void modifySpawnPoint(CallbackInfo ci) {
    //    SPAWN_POINT = new BlockPos(0, 100, 0);
    //}
}
