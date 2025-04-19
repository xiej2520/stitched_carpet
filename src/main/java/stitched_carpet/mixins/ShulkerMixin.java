package stitched_carpet.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;
import stitched_carpet.util.TeleportableEntity;
import stitched_carpet.util.WorldExt;

import java.util.List;
import java.util.Optional;


// Shulker rules
// Code originally from minitweaks and farmable-shulkers and has been modified

@Mixin(ShulkerEntity.class)
public abstract class ShulkerMixin extends GolemEntity {
    @Final
    @Shadow
    protected static TrackedData<Byte> COLOR;
    @Final
    @Shadow
    protected static TrackedData<Optional<BlockPos>> ATTACHED_BLOCK;
    @Final
    @Shadow
    protected static TrackedData<Direction> ATTACHED_FACE;
    @Final
    @Shadow
    protected static TrackedData<Byte> PEEK_AMOUNT;
    @Shadow
    private float field_7339; // prevOpenProgress
    @Shadow
    private float field_7337; // openProgress
    @Shadow
    private BlockPos field_7345 = null; // prevAttachedBlock
    @Shadow
    private int field_7340; // teleportLerpTimer

    protected ShulkerMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static Box getIntersectionBox(Direction direction, float prevOffset, float offset) {
        double max = Math.max(prevOffset, offset);
        double min = Math.min(prevOffset, offset);
        return new Box(BlockPos.ORIGIN).stretch(
                direction.getOffsetX() * max,
                direction.getOffsetY() * max,
                direction.getOffsetZ() * max
        ).shrink(
                -direction.getOffsetX() * (1.0D + min),
                -direction.getOffsetY() * (1.0D + min),
                -direction.getOffsetZ() * (1.0D + min)
        );
    }

    @Shadow
    protected abstract boolean method_7124(); // isClosed() in 1.16+

    @Shadow
    protected abstract boolean method_7127(); // tryTeleport() in 1.16+

    @Shadow
    public abstract Direction getAttachedFace();

    @Shadow
    public abstract int getPeekAmount();

    @Shadow
    public abstract DyeColor getColor();

    @Unique
    private void setColor(ShulkerEntity shulkerEntity, DyeColor color) {
        DataTracker dataTracker = shulkerEntity.getDataTracker();
        byte colorId = (byte) (color != null ? color.getId() : 16);

        dataTracker.set(COLOR, colorId);
    }

    // shulker cloning from 1.17
    @Inject(method = "damage", at = @At("RETURN"))
    private void cloneShulker(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // check if return value is true and rule is enabled
        if (cir.getReturnValue() && StitchedCarpetSettings.shulkerCloning && source.isProjectile()) {
            Entity sourceEntity = source.getSource();
            if (sourceEntity != null && sourceEntity.getType() == EntityType.SHULKER_BULLET) {
                this.spawnClone();
            }
        }
    }

    @Unique
    private void spawnClone() {
        Vec3d pos = this.getPos();
        Box box = this.getBoundingBox();
        // if shulker is open, try to teleport, and spawn new shulker if successful
        if (!this.method_7124() && this.method_7127()) {
            // get odds of successfully cloning based on how many shulkers are within 8 blocks (17x17x17 area)
            int shulkersCount = this.world.getEntities(EntityType.SHULKER, box.expand(8.0D), Entity::isAlive).size();
            float cloneOdds = (float) (shulkersCount - 1) / 5.0F;
            if (cloneOdds <= this.world.random.nextFloat()) {
                ShulkerEntity shulkerEntity = EntityType.SHULKER.create(this.world);
                if (shulkerEntity == null) {
                    return;
                }
                DyeColor dyeColor = this.getColor();
                if (dyeColor != null) {
                    setColor(shulkerEntity, dyeColor);
                }

                ((TeleportableEntity) shulkerEntity).refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
                this.world.spawnEntity(shulkerEntity);
            }
        }
    }

    // 1.16 shulker MC-159773 fixes and related methods
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tickFix(CallbackInfo ci) {
        if (StitchedCarpetSettings.shulkerBehaviorFix) {
            super.tick();
            BlockPos blockPos = this.dataTracker.get(ATTACHED_BLOCK).orElse(null);
            if (blockPos == null && !this.world.isClient) {
                blockPos = new BlockPos(this);
                this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos));
            }

            if (this.hasVehicle()) {
                blockPos = null;
                float f = this.getVehicle().yaw;
                this.yaw = f;
                this.bodyYaw = f;
                this.prevBodyYaw = f;
                this.field_7340 = 0;
            } else if (!this.world.isClient) {
                // bugfix for MC-159773
                // tryAttachOrTeleport()
                if (!this.canStay(blockPos, this.getAttachedFace())) {
                    Direction attachSide = this.findAttachSide(blockPos);
                    if (attachSide != null) {
                        this.dataTracker.set(ATTACHED_FACE, attachSide);
                    } else {
                        this.method_7127();
                    }
                }
            }

            // tickOpenProgress()
            float f = (float) this.getPeekAmount() * 0.01F;
            this.field_7339 = this.field_7337;
            if (this.field_7337 > f) {
                this.field_7337 = MathHelper.clamp(this.field_7337 - 0.05F, f, 1.0F);
            } else if (this.field_7337 < f) {
                this.field_7337 = MathHelper.clamp(this.field_7337 + 0.05F, 0.0F, f);
            }

            if (blockPos != null) {
                if (this.world.isClient) {
                    if (this.field_7340 > 0 && this.field_7345 != null) {
                        --this.field_7340;
                    } else {
                        this.field_7345 = blockPos;
                    }
                }

                // moveEntities()
                this.resetPosition((double) blockPos.getX() + (double) 0.5F, blockPos.getY(), (double) blockPos.getZ() + (double) 0.5F);
                double d = (double) 0.5F - (double) MathHelper.sin((0.5F + this.field_7337) * (float) Math.PI) * (double) 0.5F;
                double e = (double) 0.5F - (double) MathHelper.sin((0.5F + this.field_7339) * (float) Math.PI) * (double) 0.5F;
                Direction direction3 = this.getAttachedFace().getOpposite();
                this.setBoundingBox((new Box(this.getX() - (double) 0.5F, this.getY(), this.getZ() - (double) 0.5F, this.getX() + (double) 0.5F, this.getY() + (double) 1.0F, this.getZ() + (double) 0.5F)).stretch((double) direction3.getOffsetX() * d, (double) direction3.getOffsetY() * d, (double) direction3.getOffsetZ() * d));
                double g = d - e;
                if (g > (double) 0.0F) {
                    List<Entity> list = this.world.getEntities(this, this.getBoundingBox());
                    if (!list.isEmpty()) {
                        for (Entity entity : list) {
                            if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
                                entity.move(MovementType.SHULKER, new Vec3d(g * (double) direction3.getOffsetX(), g * (double) direction3.getOffsetY(), g * (double) direction3.getOffsetZ()));
                            }
                        }
                    }
                }
            }
            ci.cancel(); // end tick() call if shulkerBehaviorFix active
        }
    }

    @Inject(method = "method_7127", at = @At("HEAD"), cancellable = true)
    private void tryTeleport(CallbackInfoReturnable<Boolean> cir) {
        if (StitchedCarpetSettings.shulkerBehaviorFix) {
            if (!this.isAiDisabled() && this.isAlive()) {
                BlockPos blockPos = this.getBlockPos();

                for (int i = 0; i < 5; ++i) {
                    BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
                    if (blockPos2.getY() > 0
                            && this.world.isAir(blockPos2)
                            && this.world.getWorldBorder().contains(blockPos2)
                            // bugfix for MC-159773, MC-183884
                            && WorldExt.isSpaceEmpty(world, this, new Box(blockPos2).contract(1.0E-6))) {

                        Direction attachSide = this.findAttachSide(blockPos2);
                        if (attachSide != null) {
                            this.dataTracker.set(ATTACHED_FACE, attachSide);
                            this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                            this.dataTracker.set(ATTACHED_BLOCK, Optional.of(blockPos2));
                            this.dataTracker.set(PEEK_AMOUNT, (byte) 0);
                            this.setTarget(null);

                            cir.setReturnValue(true); // end method_7127() call
                            return;
                        }
                    }
                }

                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private boolean canStay(BlockPos pos, Direction attachSide) {
        if (this.isBlockEmpty(pos)) {
            Direction opposite = attachSide.getOpposite();
            if (WorldExt.isDirectionSolid(world, pos.offset(attachSide), this, opposite)) {
                // fixes MC-183884 by making intersection box slightly smaller to not intersect neighbor shulkers
                Box box = getIntersectionBox(opposite, -1.0F, 1.0F).offset(pos).contract(1.0E-6);
                return WorldExt.isSpaceEmpty(world, this, box);
            }
        }
        return false;
    }

    @Unique
    private boolean isBlockEmpty(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        return blockState.isAir() || (blockState.getBlock() == Blocks.MOVING_PISTON && pos.equals(this.getBlockPos()));
    }

    @Unique
    @Nullable
    private Direction findAttachSide(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.canStay(pos, direction)) {
                return direction;
            }
        }
        return null;
    }

    @Inject(method = "updatePosition(DDD)V", at = @At(value = "HEAD"))
    private void setAttachedBlock(double x, double y, double z, CallbackInfo ci) {
        if (StitchedCarpetSettings.shulkerBehaviorFix) {
            // Fixes MC-139265 by setting a shulker's ATTACHED_BLOCK on the first tick
            // when it teleports to another dimension (at least I think that's
            // what's happening?)
            if (this.dataTracker != null && this.age == 0) {
                Optional<BlockPos> pos = this.dataTracker.get(ATTACHED_BLOCK);
                Optional<BlockPos> newPos = Optional.of(new BlockPos(x, y, z));
                if (!newPos.equals(pos)) {
                    this.dataTracker.set(ATTACHED_BLOCK, newPos);
                }
            }
        }
    }
}
