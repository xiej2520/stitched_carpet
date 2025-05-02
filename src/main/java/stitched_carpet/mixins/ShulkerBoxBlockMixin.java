package stitched_carpet.mixins;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import stitched_carpet.StitchedCarpetSettings;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;doesNotCollide(Lnet/minecraft/util/math/Box;)Z"))
    private boolean ignoreBoxBlocked(World world, Box box) {
        if (StitchedCarpetSettings.alwaysOpenBox == StitchedCarpetSettings.BoxOpenOptions.SHULKER || StitchedCarpetSettings.alwaysOpenBox == StitchedCarpetSettings.BoxOpenOptions.BOTH) {
            return true;
        } else {
            return world.doesNotCollide(box);
        }
    }
}
