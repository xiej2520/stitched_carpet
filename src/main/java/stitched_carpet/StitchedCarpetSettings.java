package stitched_carpet;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;

public class StitchedCarpetSettings {
    // 1.17 shulker cloning
    @Rule(
            desc = "1.17 Shulker cloning",
            extra = {
                    "A shulker hitting a shulker with a shulker bullet can make a new shulker",
            },
            category = {"shulkerclone", "mobs", RuleCategory.SURVIVAL, "backport"}
    )
    public static boolean shulkerCloning = false;

    // Shulker behavior fixes
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
}
