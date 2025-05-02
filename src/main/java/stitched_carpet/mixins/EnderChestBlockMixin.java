package stitched_carpet.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSimpleFullBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean ignoreBoxBlocked(BlockState instance, BlockView view, BlockPos pos) {
        if (StitchedCarpetSettings.alwaysOpenBox == StitchedCarpetSettings.BoxOpenOptions.CHEST || StitchedCarpetSettings.alwaysOpenBox == StitchedCarpetSettings.BoxOpenOptions.BOTH) {
            return false;
        } else {
            return instance.isSimpleFullBlock(view, pos);
        }
    }
}
