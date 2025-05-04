package stitched_carpet.mixins.bees;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stitched_carpet.StitchedCarpetSettings;
import stitched_carpet.util.RainCheckAccessor;

// From FxMorin/carpet-fixes
// Fixes MC-178119

/**
 * Fix makes it so that the bee only enters hive if either its raining where it currently is or its raining next
 * to the hive. Instead of sitting in the rain and contemplating life
 */
@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin extends Entity implements RainCheckAccessor {

    public BeeEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    @Nullable
    private BlockPos hivePos;


    @Unique
    public boolean rainCheck(World world) {
        // the game reads rainLevel on world load or advanceWeatherCycle(), which is only called if hasSkyLight() is true
        // for the dimension, so the Nether is treated as raining. See MC-178119 bug report for details.

        // change to check for rain on the block the bee, its beehive, or block beehive is facing instead
        if (StitchedCarpetSettings.beeFixes) {
            if (world.hasRain(this.getBlockPos())) {
                return true;
            }
            if (this.hivePos != null) {
                BlockPos pos = this.hivePos;
                BlockState state = this.world.getBlockState(pos);
                if (state.getBlock() instanceof BeehiveBlock) {
                    return this.world.hasRain(pos) || this.world.hasRain(pos.offset(this.world.getBlockState(pos).get(BeehiveBlock.FACING)));
                }
            }
            return false;
        }
        return world.isRaining();
    }

    @Redirect(
            method = "canEnterHive",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;isRaining()Z"
            )
    )
    private boolean canEnterHiveFix(World world) {
        return this.rainCheck(world);
    }

    @Mixin(BeeEntity.PollinateGoal.class)
    abstract static class PollinateGoalMixin {

        @Final
        @Shadow
        BeeEntity field_20377;

        @Redirect(
                method = "canBeeStart",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/World;isRaining()Z"
                )
        )
        private boolean canBeeStartFix(World world) {
            return ((RainCheckAccessor) field_20377).rainCheck(world);
        }

        @Redirect(
                method = "canBeeContinue",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/world/World;isRaining()Z"
                )
        )
        private boolean canBeeContinueFix(World world) {
            return ((RainCheckAccessor) field_20377).rainCheck(world);
        }

    }

}
