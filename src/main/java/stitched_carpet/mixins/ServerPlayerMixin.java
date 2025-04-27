package stitched_carpet.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.util.math.BlockPos;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {
    @Redirect(
            method = "changeDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
            )
    )
    private boolean makeObsidianPlatform(ServerWorld serverWorld2, BlockPos blockPos, BlockState blockState) {
        if (StitchedCarpetSettings.endPlatformGeneration != StitchedCarpetSettings.EndPlatformOptions.NONE) {
            if (StitchedCarpetSettings.endPlatformDropsBlocks && serverWorld2.getBlockState(blockPos) != blockState) {
                serverWorld2.breakBlock(blockPos, true);
            }
            return serverWorld2.setBlockState(blockPos, blockState);
        }
        return false;
    }
}
