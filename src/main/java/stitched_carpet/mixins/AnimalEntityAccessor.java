package stitched_carpet.mixins;

import net.minecraft.entity.passive.AnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalEntity.class)
public interface AnimalEntityAccessor {

    @Accessor
    int getLoveTicks();
}
