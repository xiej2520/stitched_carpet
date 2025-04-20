package stitched_carpet;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;
import net.minecraft.util.math.BlockPos;

public class StitchedCarpetSettings {
    @Rule(
            desc = "1.17 Shulker cloning",
            extra = {
                    "A shulker hitting a shulker with a shulker bullet can make a new shulker",
            },
            category = {"shulkerclone", "mobs", RuleCategory.SURVIVAL, "backport"}
    )
    public static boolean shulkerCloning = false;

    @Rule(
            desc = "Shulker behavior fixes",
            extra = {
                    "Fixes MC-139265, so shulkers use portals correctly, MC-183884, so shulkers can sit next",
                    "to each other, and MC-159773, so shulkers properly teleport away from the faces of blocks",
                    "that aren't flat and allows them to attach to bottom slabs/stairs."
            },
            category = {"shulkerclone", "mobs", "backport", RuleCategory.BUGFIX}
    )
    public static boolean shulkerBehaviorFix = false;

    @Rule(desc = "Mending only repairs equipped damaged items, from 1.16+.", category = {"backport", RuleCategory.SURVIVAL})
    public static boolean mendingOnlyDamaged = false;


    @Rule(desc = "Reintroduces infinity and mending stacking on bows from 1.9 - 1.11.",
            category = {RuleCategory.SURVIVAL, "reintroduce"})
    public static boolean infinityMendingStacking = false;

    @Rule(desc = "Reintroduces protection stacking on armor from 1.14 - 1.14.2. Works in enchanting table.",
            category = {RuleCategory.SURVIVAL, "reintroduce"})
    public static boolean protectionStacking = false;


    @Rule(desc = "Instant Mining Gold Blocks with iron and diamond pickaxes.",
          category = {RuleCategory.SURVIVAL, "instamine"})
    public static boolean instantMiningGold = false;

    @Rule(desc = "Instant Mining Wood with Diamond Axe with Efficiency V and Haste II",
          category = {RuleCategory.SURVIVAL, "instamine"})
    public static boolean instantMiningWood = false;


    @Rule(desc = "Change the end platform spawn location. Use comma-separated 'x,y,z' or 'default'.",
          category = {RuleCategory.CREATIVE, RuleCategory.FEATURE})
    public static String endPlatformSpawnPoint = "default";
}
