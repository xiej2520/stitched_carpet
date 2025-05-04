package stitched_carpet.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class WorldExt {
    // 1.16+ World methods backported

    private static boolean isOutOfBuildLimitVertically(BlockPos pos) {
        return isOutOfBuildLimitVertically(pos.getY());
    }

    private static boolean isOutOfBuildLimitVertically(int y) {
        return y < 0 || y >= 256;
    }

    public static boolean isDirectionSolid(World world, BlockPos pos, Entity entity, Direction direction) {
        if (isOutOfBuildLimitVertically(pos)) {
            return false;
        } else {
            Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
            if (chunk == null) {
                return false;
            }

            BlockState state = chunk.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(world, pos, EntityContext.of(entity));
            return Block.isFaceFullSquare(shape, direction);
        }
    }

    public static boolean hasSolidTopSurface(BlockState block, BlockView world, BlockPos pos, Entity entity, Direction direction) {
        return Block.isFaceFullSquare(block.getCollisionShape(world, pos, EntityContext.of(entity)), direction);
    }

    public static boolean isSpaceEmpty(World world, Entity entity, Box box) {
        return isSpaceEmpty(world, entity, box, Collections.emptySet());
    }

    public static boolean isSpaceEmpty(World world, @Nullable Entity entity, Box box, Set<Entity> excluded) {
        return world.getCollisions(entity, box, excluded).allMatch(VoxelShape::isEmpty);
    }
}
