package stitched_carpet;

import carpet.settings.Rule;
import static carpet.settings.RuleCategory.*;
import static stitched_carpet.StitchedCarpetRuleCategory.*;

class StitchedCarpetRuleCategory {
    public static final String BACKPORT = "backport";
    public static final String REINTRODUCE = "reintroduce";
}

public class StitchedCarpetSettings {
    @Rule(
            desc = "1.17 Shulker cloning",
            extra = {
                    "A shulker hitting a shulker with a shulker bullet can make a new shulker",
            },
            category = {"shulkerclone", "mobs", SURVIVAL, BACKPORT}
    )
    public static boolean shulkerCloning = false;

    @Rule(
            desc = "Shulker behavior fixes",
            extra = {
                    "Fixes MC-139265, so shulkers use portals correctly, MC-183884, so shulkers can sit next",
                    "to each other, and MC-159773, so shulkers properly teleport away from the faces of blocks",
                    "that aren't flat and allows them to attach to bottom slabs/stairs."
            },
            category = {"shulkerclone", "mobs", BACKPORT, BUGFIX}
    )
    public static boolean shulkerBehaviorFix = false;

    @Rule(desc = "Mending only repairs equipped damaged items, from 1.16+.", category = {"backport", SURVIVAL})
    public static boolean mendingOnlyDamaged = false;

    @Rule(desc = "When to generate obsidian platform in the end.",
          extra = {
                  "all - Generate end platform when all entities are transferred to the_end dimension.",
                  "none - End platform will not be generated.",
                  "player - End platform is generated only when the player entity teleports to the_end dimension.",
          },
          category = {BACKPORT, BUGFIX, FEATURE}
    )
    public static EndPlatformOptions endPlatform = EndPlatformOptions.PLAYER;

    public enum EndPlatformOptions {
        ALL,
        NONE,
        PLAYER
    }

    @Rule(desc = "Makes hoes a mining tool like 1.16+.",
          extra = {"Hoe harvests nether wart block, hay, sponge, dried kelp, and leaves, can be enchanted with Efficiency, Fortune and Silk Touch, and takes damage from breaking blocks like in 1.16."},
          category = {"backport", SURVIVAL})
    public static boolean hoeMiningTool = false;

    @Rule(desc = "Shulker boxes drop contents when item entity is destroyed, from 1.17.",
            category = {"backport", SURVIVAL})
    public static boolean shulkerBoxItemsDropContents = false;


    @Rule(desc = "Reintroduces infinity and mending stacking on bows from 1.9 - 1.11.",
            category = {SURVIVAL, REINTRODUCE})
    public static boolean infinityMendingStacking = false;

    @Rule(desc = "Reintroduces protection stacking on armor from 1.14 - 1.14.2. Works in enchanting table.",
            category = {SURVIVAL, REINTRODUCE})
    public static boolean protectionStacking = false;


    @Rule(desc = "Instant Mining Gold Blocks with iron and diamond pickaxes.",
          category = {SURVIVAL, "instamine"})
    public static boolean instantMiningGold = false;

    @Rule(desc = "Instant Mining Wood with Diamond Axe with Efficiency V and Haste II",
          category = {SURVIVAL, "instamine"})
    public static boolean instantMiningWood = false;

    @Rule(desc = "Instant Mining Concrete with Diamond Pickaxe with Efficiency V and Haste II",
            category = {SURVIVAL, "instamine"})
    public static boolean instantMiningConcrete = false;


    @Rule(desc = "Change the end platform spawn location. Use separated 'x,y,z', anything else uses default position.",
          category = {CREATIVE, FEATURE},
          strict = false)
    public static String endPlatformSpawnPoint = "";
}
