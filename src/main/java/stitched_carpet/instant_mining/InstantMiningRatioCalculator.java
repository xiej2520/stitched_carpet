package stitched_carpet.instant_mining;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.*;
import stitched_carpet.lists.BlockList;

import java.util.HashSet;

public interface InstantMiningRatioCalculator {
    float getInstantMiningRatio(BlockState blockState, Item mainHand);

    enum ToolInstantMiningRatioCalculator implements InstantMiningRatioCalculator {
        DiamondAxeWood(
                InstantMiningCarpetRuleKeys.instantMiningWood,
                AxeItem.class,
                2,
                3,
                ToolMaterials.DIAMOND,
                ToolMaterials.GOLD,
                new HashSet<>(BlockList.wood)),
        DiamondPickaxeConcrete(
                InstantMiningCarpetRuleKeys.instantMiningConcrete,
                PickaxeItem.class,
                2,
                3,
                ToolMaterials.DIAMOND,
                ToolMaterials.GOLD,
                new HashSet<>(BlockList.concrete));

        //NetheriteAxeWood(
        //        InstantMiningCarpetRuleKeys.netheriteAxeWood,
        //        AxeItem.class,
        //        2,
        //        3,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.GOLD,
        //        new HashSet<>(BlockList.wood)),
        //NetheritePickaxeBlueIce(
        //        InstantMiningCarpetRuleKeys.netheritePickaxeBlueIce,
        //        PickaxeItem.class,
        //        2,
        //        7,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.NETHERITE,
        //        new HashSet<>(BlockList.blueIce)),
        //NetheritePickaxeCobbledDeepslate(
        //        InstantMiningCarpetRuleKeys.netheritePickaxeDeepslate,
        //        PickaxeItem.class,
        //        2,
        //        10,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.NETHERITE,
        //        new HashSet<>(BlockList.cobbledDeepslate)),
        //NetheritePickaxeCobblestone(
        //        InstantMiningCarpetRuleKeys.netheritePickaxeCobblestone,
        //        PickaxeItem.class,
        //        2,
        //        3,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.GOLD,
        //        new HashSet<>(BlockList.cobblestone)),
        //NetheritePickaxeDeepslate(
        //        InstantMiningCarpetRuleKeys.netheritePickaxeDeepslate,
        //        PickaxeItem.class,
        //        2,
        //        8,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.NETHERITE,
        //        new HashSet<>(BlockList.deepslate)),
        //NetheritePickaxeEndStone(
        //        InstantMiningCarpetRuleKeys.netheritePickaxeEndStone,
        //        PickaxeItem.class,
        //        2,
        //        8,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.NETHERITE,
        //        new HashSet<>(BlockList.endStone)),
        //NetheritePickaxeNetherBricks(
        //        InstantMiningCarpetRuleKeys.netheritePickaxeNetherBricks,
        //        PickaxeItem.class,
        //        2,
        //        3,
        //        ToolMaterials.NETHERITE,
        //        ToolMaterials.GOLD,
        //        new HashSet<>(BlockList.netherBricks));

        private final String carpetRuleKey;
        private final int originalHasteLevel;
        private final int newHasteLevel;
        private final Class<? extends MiningToolItem> originalToolClass;
        private final ToolMaterial originalHasteToolMaterial;
        private final ToolMaterial newHasteToolMaterial;
        private final HashSet<Block> blocksThatCanBeInstantMined;

        ToolInstantMiningRatioCalculator(
                String carpetRuleKey,
                Class<? extends MiningToolItem> originalToolClass,
                int originalHasteLevel,
                int newHasteLevel,
                ToolMaterial originalHasteToolMaterial,
                ToolMaterial newHasteToolMaterial,
                HashSet<Block> blocksThatCanBeInstantMined
        ) {

            this.carpetRuleKey = carpetRuleKey;
            this.originalHasteLevel = originalHasteLevel;
            this.newHasteLevel = newHasteLevel;
            this.originalToolClass = originalToolClass;
            this.originalHasteToolMaterial = originalHasteToolMaterial;
            this.newHasteToolMaterial = newHasteToolMaterial;
            this.blocksThatCanBeInstantMined = blocksThatCanBeInstantMined;
        }

        @Override
        public float getInstantMiningRatio(BlockState blockState, Item mainHand) {
            return HasteInstantMiningRatioCalculator.getRatio(
                    this.originalHasteLevel,
                    this.newHasteLevel,
                    this.originalHasteToolMaterial,
                    this.newHasteToolMaterial);
        }

        public boolean canInstantMine(BlockState blockState, Item item) {
            if (!InstantMiningCarpetRuleAccessor.carpetRules.get(carpetRuleKey).get()
                    || !(item instanceof MiningToolItem miningToolItem)
                    || !(miningToolItem.getClass().equals(this.originalToolClass))
                    || !this.blocksThatCanBeInstantMined.contains(blockState.getBlock())) {
                return false;
            }

            return miningToolItem.getMaterial() == this.originalHasteToolMaterial;
        }
    }

    class HasteInstantMiningRatioCalculator {
        public static float getRatio(
                int originalHasteLevel,
                int newHasteLevel,
                ToolMaterial originalMiningToolMaterial,
                ToolMaterial newMiningToolMaterial) {
            float miningToolMaterialRatio =
                    newMiningToolMaterial.getMiningSpeed() / originalMiningToolMaterial.getMiningSpeed();
            // Formula for the ratio using the original and new haste levels
            float hasteRatio = (float) (5 + newHasteLevel) / (float) (5 + originalHasteLevel);
            return miningToolMaterialRatio * hasteRatio;
        }
    }

    class BlockBreakingSpeedRatioCalculator {
        public static float getBlockBreakingSpeedRatio(LivingEntity livingEntity, BlockState blockState) {
            int efficiencyLevel = EnchantmentHelper.getEfficiency(livingEntity);
            int hasteAmplifier = StatusEffectUtil.getHasteAmplifier(livingEntity);
            ItemStack mainHand = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);

            if (!StatusEffectUtil.hasHaste(livingEntity)
                    || mainHand.isEmpty()
                    || efficiencyLevel < 5
                    || hasteAmplifier < 1) {
                return 1.0f;
            }

            var instantMiningRatioCalculator = new CompositeInstantMiningRatioCalculator();
            return instantMiningRatioCalculator.getInstantMiningRatio(blockState, mainHand.getItem());
        }
    }

    class CompositeInstantMiningRatioCalculator implements InstantMiningRatioCalculator {

        private final ToolInstantMiningRatioCalculator[] calculators;

        public CompositeInstantMiningRatioCalculator() {
            this.calculators = ToolInstantMiningRatioCalculator.values();
        }

        @Override
        public float getInstantMiningRatio(BlockState blockState, Item mainHand) {
            for (ToolInstantMiningRatioCalculator calculator : calculators) {
                if (!calculator.canInstantMine(blockState, mainHand)) {
                    continue;
                }

                return calculator.getInstantMiningRatio(blockState, mainHand);
            }

            return 1.0f;
        }
    }
}
