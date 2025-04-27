package stitched_carpet.mixins;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stitched_carpet.StitchedCarpetSettings;

import java.util.stream.Stream;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;remove()V"
            )
    )
    public void onKilled(CallbackInfoReturnable<Boolean> cir) {
        if (StitchedCarpetSettings.shulkerBoxItemsDropContents
                && (this.getStack().getItem() instanceof BlockItem)
                && (((BlockItem) this.getStack().getItem()).getBlock() instanceof ShulkerBoxBlock)) {
            CompoundTag compoundTag = this.getStack().getTag();
            if (compoundTag != null) {
                ListTag listTag = compoundTag.getCompound("BlockEntityTag").getList("Items", 10);
                Stream<ItemStack> stream = listTag.stream().map(CompoundTag.class::cast).map(ItemStack::fromTag);
                World world = this.getEntityWorld();
                if (!world.isClient)
                    stream.forEach((itemStack) ->
                            world.spawnEntity(new ItemEntity(world, this.getX(), this.getY(),
                                    this.getZ(), itemStack))
                    );
            }
        }
    }
}
