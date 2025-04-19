package stitched_carpet.util;

// from Kira-NT/farmable-shulkers
public interface TeleportableEntity {
    /**
     * Refreshes the position of the entity after teleportation.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    void refreshPositionAfterTeleport(double x, double y, double z);
}
