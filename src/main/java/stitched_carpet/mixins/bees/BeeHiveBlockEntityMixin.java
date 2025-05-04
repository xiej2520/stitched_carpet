package stitched_carpet.mixins.bees;

import com.google.common.collect.Lists;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetServer;
import stitched_carpet.StitchedCarpetSettings;
import stitched_carpet.mixins.AnimalEntityAccessor;

import java.util.Iterator;
import java.util.List;

@Mixin(BeehiveBlockEntity.class)
public abstract class BeeHiveBlockEntityMixin extends BlockEntity {

    @Shadow
    private final List<BeehiveBlockEntity.Bee> bees = Lists.newArrayList();
    @Shadow
    @Nullable
    private BlockPos flowerPos = null;
    public BeeHiveBlockEntityMixin(BlockEntityType<?> type) {
        super(type);
    }

    @Shadow
    protected abstract boolean hasFlowerPos();

    // In 1.15, BeehiveBlockEntity.bee.ticksInHive is only used to compare against bee.minOccupationTIcks,
    // and stops incrementing if ticksInHive > minOccuptationTicks, even if the bee stays in the hive.
    // Changed to use ticksInHive to age the bee and decrease its breeding delay
    // Fixes MC-166135

    @Unique
    private void ageBee(int ticks, BeeEntity bee) {
        StitchedCarpetServer.LOGGER.info("aging {} by {} ticks; {}", bee, ticks, bee.getBreedingAge());
        int i = bee.getBreedingAge();
        if (i < 0) {
            bee.setBreedingAge(Math.min(0, i + ticks));
        } else if (i > 0) {
            bee.setBreedingAge(Math.max(0, i - ticks));
        }

        bee.setLoveTicks(Math.max(0, ((AnimalEntityAccessor) bee).getLoveTicks() - ticks));
        bee.resetPollinationTicks();
        StitchedCarpetServer.LOGGER.info("new bee {} age {}", bee, bee.getBreedingAge());
    }

    @Inject(method = "method_21854", at = @At("HEAD"), cancellable = true)
    private void tryReleaseBeeFix(BlockState blockState, List<Entity> list, BeehiveBlockEntity.BeeState beeState, BeehiveBlockEntity.Bee bee, CallbackInfoReturnable<Boolean> cir) {
        if (StitchedCarpetSettings.beeFixes) {
            cir.setReturnValue(releaseBeeFix(blockState, bee, list, beeState));
        }
    }

    @Inject(method = "tickBees", at = @At("HEAD"), cancellable = true)
    private void tickBeesFix(CallbackInfo ci) {
        if (StitchedCarpetSettings.beeFixes) {
            Iterator<BeehiveBlockEntity.Bee> iterator = this.bees.iterator();
            BlockState blockState = this.getCachedState();

            while (iterator.hasNext()) {
                BeehiveBlockEntity.Bee bee = (BeehiveBlockEntity.Bee) iterator.next();
                if (bee.ticksInHive > bee.minOccupationTIcks) {
                    CompoundTag compoundTag = bee.entityData;
                    BeehiveBlockEntity.BeeState beeState = compoundTag.getBoolean("HasNectar")
                            ? BeehiveBlockEntity.BeeState.HONEY_DELIVERED
                            : BeehiveBlockEntity.BeeState.BEE_RELEASED;
                    if (this.releaseBeeFix(blockState, bee, null, beeState)) {
                        iterator.remove();
                    }
                }
                // moved out of else clause, should increase even if > minOccupationTicks
                bee.ticksInHive++;
            }
            ci.cancel();
        }
    }


    // is raining in front of hive or is night in Overworld
    @Unique
    private boolean overworldCannotLeave(BlockState state) {
        if (this.world.getDimension().getType() == DimensionType.OVERWORLD) {
            if (this.world.isNight()) {
                return true;
            }
            return this.world.hasRain(this.pos) || this.world.hasRain(pos.offset(state.get(BeehiveBlock.FACING)));
        }
        return false;

    }

    // needed to add BeehiveBlockEntity.Bee to parameters
    @Unique
    private boolean releaseBeeFix(BlockState state, BeehiveBlockEntity.Bee bee, List<Entity> list, BeehiveBlockEntity.BeeState beeState) {
        // only do night or raining checks in Overworld, Fixes MC-168329
        // also allow bees to leave hive in Overworld biomes where it isn't raining, fix MC-178119
        if (beeState != BeehiveBlockEntity.BeeState.EMERGENCY && overworldCannotLeave(state)) {
            return false;
        } else {
            BlockPos blockPos = this.getPos();
            CompoundTag compoundTag = bee.entityData;
            compoundTag.remove("Passengers");
            compoundTag.remove("Leash");
            compoundTag.remove("UUID");
            Direction direction = state.get(BeehiveBlock.FACING);
            BlockPos blockPos2 = blockPos.offset(direction);
            boolean bl = !this.world.getBlockState(blockPos2).getCollisionShape(this.world, blockPos2).isEmpty();
            if (bl && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
                return false;
            } else {
                Entity entity = EntityType.loadEntityWithPassengers(compoundTag, this.world, entityx -> entityx);
                if (entity != null) {
                    if (!entity.getType().isTaggedWith(EntityTypeTags.BEEHIVE_INHABITORS)) {
                        return false;
                    } else {
                        if (entity instanceof BeeEntity beeEntity) {
                            if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.random.nextFloat() < 0.9F) {
                                beeEntity.setFlowerPos(this.flowerPos);
                            }

                            if (beeState == BeehiveBlockEntity.BeeState.HONEY_DELIVERED) {
                                beeEntity.onHoneyDelivered();
                                if (state.getBlock().matches(BlockTags.BEEHIVES)) {
                                    int i = BeehiveBlockEntity.getHoneyLevel(state);
                                    if (i < 5) {
                                        int j = this.world.random.nextInt(100) == 0 ? 2 : 1;
                                        if (i + j > 5) {
                                            j--;
                                        }

                                        this.world.setBlockState(this.getPos(), state.with(BeehiveBlock.HONEY_LEVEL, i + j));
                                    }
                                }
                            }

                            // important part
                            this.ageBee(bee.ticksInHive, beeEntity);
                            if (list != null) {
                                list.add(beeEntity);
                            }

                            float f = entity.getWidth();
                            double d = bl ? 0.0 : 0.55 + f / 2.0F;
                            double e = blockPos.getX() + 0.5 + d * direction.getOffsetX();
                            double g = blockPos.getY() + 0.5 - entity.getHeight() / 2.0F;
                            double h = blockPos.getZ() + 0.5 + d * direction.getOffsetZ();
                            entity.refreshPositionAndAngles(e, g, h, entity.yaw, entity.pitch);
                        }

                        this.world.playSound(null, blockPos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return this.world.spawnEntity(entity);
                    }
                } else {
                    return false;
                }
            }
        }
    }
}
