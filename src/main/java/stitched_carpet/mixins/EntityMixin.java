package stitched_carpet.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import stitched_carpet.util.TeleportableEntity;

// from Kira-NT/farmable-shulkers
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
    public void refreshPositionAfterTeleport(double x, double y, double z) {
        this.updatePositionAndAngles(x, y, z, this.yaw, this.pitch);
    }

}

