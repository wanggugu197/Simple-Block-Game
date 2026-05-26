package com.simple_block_game.common.base.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static com.simple_block_game.common.base.block.BaseRotatedBlock.FACING;

/** 旋转放置的刷新方块的点击区域 */
public enum RotatedRefreshArea {

    NULL,
    LEFT_TOP,
    RIGHT_TOP,
    BOTTOM;

    private static final int HALF_PIXEL = 8;

    public static RotatedRefreshArea fromHit(BlockState state, BlockHitResult hit) {
        Direction facing = state.getValue(FACING);
        if (hit.getDirection() != facing) return NULL;

        Vec3 uv = getUV(facing, hit.getLocation(), hit.getBlockPos());
        if (uv.y < HALF_PIXEL) return BOTTOM;

        boolean swapX = facing == Direction.NORTH || facing == Direction.EAST;
        return uv.x <= HALF_PIXEL ?
                (swapX ? RIGHT_TOP : LEFT_TOP) :
                (swapX ? LEFT_TOP : RIGHT_TOP);
    }

    /**
     * 将世界坐标转换为UV坐标（0-16像素范围）
     */
    private static Vec3 getUV(Direction facing, Vec3 worldPos, BlockPos blockPos) {
        double localX = worldPos.x - blockPos.getX();
        double localY = worldPos.y - blockPos.getY();
        double localZ = worldPos.z - blockPos.getZ();

        return switch (facing) {
            case NORTH, SOUTH -> new Vec3(Mth.clamp(localX * 16, 0, 16), Mth.clamp(localY * 16, 0, 16), 0);
            case EAST, WEST -> new Vec3(Mth.clamp(localZ * 16, 0, 16), Mth.clamp(localY * 16, 0, 16), 0);
            default -> new Vec3(0, 0, 0);
        };
    }
}
