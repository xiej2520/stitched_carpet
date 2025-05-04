package stitched_carpet.mixins;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import stitched_carpet.StitchedCarpetSettings;

import java.util.Set;


@Mixin(HoeItem.class)
public abstract class HoeItemToolMixin extends ToolItem {

    @Unique
    private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of(
            Blocks.NETHER_WART_BLOCK,
            Blocks.HAY_BLOCK,
            Blocks.DRIED_KELP_BLOCK,
            Blocks.SPONGE,
            Blocks.WET_SPONGE,
            Blocks.JUNGLE_LEAVES,
            Blocks.OAK_LEAVES,
            Blocks.SPRUCE_LEAVES,
            Blocks.DARK_OAK_LEAVES,
            Blocks.ACACIA_LEAVES,
            Blocks.BIRCH_LEAVES
    );

    public HoeItemToolMixin(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (StitchedCarpetSettings.hoeMiningTool && EFFECTIVE_BLOCKS.contains(state.getBlock())) {
            return this.getMaterial().getMiningSpeed();
        }
        return 1.0F;
    }

    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (StitchedCarpetSettings.hoeMiningTool && !world.isClient && state.getHardness(world, pos) != 0.0F) {
            stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }

        return true;
    }
}
