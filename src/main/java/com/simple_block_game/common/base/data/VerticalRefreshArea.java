package com.simple_block_game.common.base.data;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public enum VerticalRefreshArea {

    NORTH_EAST,
    SOUTH_EAST,
    WEST,
    NULL;

    private static final int HALF_PIXEL = 8;

    public static VerticalRefreshArea fromHit(BlockHitResult hit) {
        Vec3 worldPos = hit.getLocation();
        Vec3 blockCenter = hit.getBlockPos().getCenter();

        double u = Mth.clamp((worldPos.x - (blockCenter.x - 0.5)) * 16, 0, 16);
        double v = Mth.clamp((worldPos.z - (blockCenter.z - 0.5)) * 16, 0, 16);

        if (u >= HALF_PIXEL) {
            return v < HALF_PIXEL ? NORTH_EAST : SOUTH_EAST;
        }
        return WEST;
    }
}
