package stitched_carpet.mixins.instantmining;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(Block.class)
public class InstantMineGoldMixin {
    @Inject(at = @At("HEAD"), method = "calcBlockBreakingDelta(Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", cancellable = true)
    public void checkGoldBlock(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        if (StitchedCarpetSettings.instantMiningGold
                && state.getBlock().equals(Blocks.GOLD_BLOCK)
                && player.isUsingEffectiveTool(state)) {
            ci.setReturnValue(100.0F);
        }
    }
}
