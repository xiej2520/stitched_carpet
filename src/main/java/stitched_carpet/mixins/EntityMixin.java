package stitched_carpet.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import stitched_carpet.StitchedCarpetSettings;
import stitched_carpet.util.TeleportableEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements TeleportableEntity {
    @Shadow
    public float yaw;

    @Shadow
    public float pitch;

    @Shadow
    public abstract void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch);

    /**
     * {@inheritDoc}
     */
    // from Kira-NT/farmable-shulkers
    public void refreshPositionAfterTeleport(double x, double y, double z) {
        this.updatePositionAndAngles(x, y, z, this.yaw, this.pitch);
    }


    // based off of Carpet-TCTC-Additions MixinEntity, not sure how they got it working in 1.15.2
    @Inject(
            method = "changeDimension",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/server/world/ServerWorld;getForcedSpawnPoint()Lnet/minecraft/util/math/BlockPos;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void makeObsidianPlatform(
            DimensionType newDimension,
            CallbackInfoReturnable<Entity> cir,
            @Local(ordinal = 1) ServerWorld serverWorld2,
            @Local BlockPos blockPos
    ) {
        if (StitchedCarpetSettings.endPlatformGeneration == StitchedCarpetSettings.EndPlatformOptions.ALL && newDimension == DimensionType.THE_END) {
            int i = blockPos.getX();
            int j = blockPos.getY() - 2;
            int k = blockPos.getZ();
            BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((blockPos2) -> {
                if (StitchedCarpetSettings.endPlatformDropsBlocks && serverWorld2.getBlockState(blockPos2) != Blocks.AIR.getDefaultState()) {
                    serverWorld2.breakBlock(blockPos2, true);
                }
                serverWorld2.setBlockState(blockPos2, Blocks.AIR.getDefaultState());
            });
            BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2).forEach((blockPos2) -> {
                if (StitchedCarpetSettings.endPlatformDropsBlocks && serverWorld2.getBlockState(blockPos2) != Blocks.OBSIDIAN.getDefaultState()) {
                    serverWorld2.breakBlock(blockPos2, true);
                }
                serverWorld2.setBlockState(blockPos2, Blocks.OBSIDIAN.getDefaultState());
            });
        }
    }

}
