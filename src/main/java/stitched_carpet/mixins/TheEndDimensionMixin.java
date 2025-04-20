package stitched_carpet.mixins;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.TheEndDimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetServer;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(TheEndDimension.class)
public abstract class TheEndDimensionMixin {

    @Inject(method = "getForcedSpawnPoint", at = @At("HEAD"), cancellable = true)
    public void getForcedSpawnPoint(CallbackInfoReturnable<BlockPos> cir) {
        if (!StitchedCarpetSettings.endPlatformSpawnPoint.equals("default")) {
            try {
                String[] split = StitchedCarpetSettings.endPlatformSpawnPoint.split(",");
                double x = Double.parseDouble(split[0]);
                double y = Double.parseDouble(split[1]);
                double z = Double.parseDouble(split[2]);

                cir.setReturnValue(new BlockPos(x, y, z));
            } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                StitchedCarpetServer.LOGGER.error("Invalid coordinates for end platform", StitchedCarpetSettings.endPlatformSpawnPoint);
            }
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
