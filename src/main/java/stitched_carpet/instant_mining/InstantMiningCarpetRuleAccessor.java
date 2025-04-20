package stitched_carpet.instant_mining;

import stitched_carpet.StitchedCarpetSettings;

import java.util.HashMap;
import java.util.function.Supplier;

// Code modified from Gilly7CE/Carpet-Addons-Not-Found

public class InstantMiningCarpetRuleAccessor {
    public static final HashMap<String, Supplier<Boolean>> carpetRules;

    static {
        carpetRules = new HashMap<>();
        carpetRules.put(InstantMiningCarpetRuleKeys.instantMiningWood,
                () -> StitchedCarpetSettings.instantMiningWood);
        carpetRules.put(InstantMiningCarpetRuleKeys.instantMiningConcrete,
                () -> StitchedCarpetSettings.instantMiningConcrete);
        //carpetRules.put(InstantMiningCarpetRuleKeys.netheriteAxeWood,
        //        () -> CarpetAddonsNotFoundSettings.netheriteAxeInstantMineWood);
        //carpetRules.put(InstantMiningCarpetRuleKeys.netheritePickaxeBlueIce,
        //        () -> CarpetAddonsNotFoundSettings.netheritePickaxeInstantMineBlueIce);
        //carpetRules.put(InstantMiningCarpetRuleKeys.netheritePickaxeCobblestone,
        //        () -> CarpetAddonsNotFoundSettings.netheritePickaxeInstantMineCobblestone);
        //carpetRules.put(InstantMiningCarpetRuleKeys.netheritePickaxeDeepslate,
        //        () -> CarpetAddonsNotFoundSettings.netheritePickaxeInstantMineDeepslate);
        //carpetRules.put(InstantMiningCarpetRuleKeys.netheritePickaxeEndStone,
        //        () -> CarpetAddonsNotFoundSettings.netheritePickaxeInstantMineEndStone);
        //carpetRules.put(InstantMiningCarpetRuleKeys.netheritePickaxeNetherBricks,
        //        () -> CarpetAddonsNotFoundSettings.netheritePickaxeInstantMineNetherBricks);
    }
}

class InstantMiningCarpetRuleKeys {
    public static final String instantMiningWood = "diamondAxeWood";
    public static final String instantMiningConcrete = "diamondPickaxeConcrete";
    //public static final String netheriteAxeWood = "netheriteAxeWood";
    //public static final String netheritePickaxeBlueIce = "netheritePickaxeBlueIce";
    //public static final String netheritePickaxeCobblestone = "netheritePickaxeCobblestone";
    //public static final String netheritePickaxeDeepslate = "netheritePickaxeDeepslate";
    //public static final String netheritePickaxeEndStone = "netheritePickaxeEndStone";
    //public static final String netheritePickaxeNetherBricks = "netheritePickaxeNetherBricks";
}
