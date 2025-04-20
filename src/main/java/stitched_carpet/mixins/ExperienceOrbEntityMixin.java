package stitched_carpet.mixins;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stitched_carpet.StitchedCarpetSettings;

import java.util.Map;
import java.util.function.Predicate;

import static net.minecraft.enchantment.EnchantmentHelper.getLevel;


// Backports 1.16 behavior of xp orbs only mending damaged items, instead of
// being wasted on full-durability mending items.

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin extends Entity {

    @Shadow
    private int amount;

    @Shadow
    public int pickupDelay;

    private ExperienceOrbEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void modifyOnPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (StitchedCarpetSettings.mendingOnlyDamaged) {
            if (!this.world.isClient) {
                if (this.pickupDelay == 0 && player.experiencePickUpDelay == 0) {
                    player.experiencePickUpDelay = 2;
                    player.sendPickup(this, 1);
                    var entry = chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
                    if (entry != null) {
                        ItemStack itemStack = entry.getValue();
                        if (!itemStack.isEmpty() && itemStack.isDamaged()) {
                            int i = Math.min(this.getMendingRepairAmount(this.amount), itemStack.getDamage());
                            this.amount -= this.getMendingRepairCost(i);
                            itemStack.setDamage(itemStack.getDamage() - i);
                        }
                    }

                    if (this.amount > 0) {
                        player.addExperience(this.amount);
                    }

                    this.remove();
                }
            }
            ci.cancel();
        }
    }

    @Nullable
    @Unique
    private static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
        if (map.isEmpty()) {
            return null;
        } else {
            var list = Lists.<Map.Entry<EquipmentSlot, ItemStack>>newArrayList();

            for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
                ItemStack itemStack = entry.getValue();
                if (!itemStack.isEmpty() && getLevel(enchantment, itemStack) > 0 && condition.test(itemStack)) {
                    list.add(entry);
                }
            }

            return list.isEmpty() ? null : list.get(entity.getRandom().nextInt(list.size()));
        }
    }

    @Unique
    private int getMendingRepairCost(int repairAmount) {
        return repairAmount / 2;
    }

    @Unique
    private int getMendingRepairAmount(int experienceAmount) {
        return experienceAmount * 2;
    }
}
