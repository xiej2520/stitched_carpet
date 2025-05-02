package stitched_carpet.util;

import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class BlockPosParser {
    public static Optional<BlockPos> parseBlockPos(String s) {
        try {
            String[] split = s.split("[,\\s]+"); // comma or whitespace
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            return Optional.of(new BlockPos(x, y, z));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
}
