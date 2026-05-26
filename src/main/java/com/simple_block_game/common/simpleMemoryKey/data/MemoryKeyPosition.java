package com.simple_block_game.common.simpleMemoryKey.data;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/** 记忆键游戏按键位置枚举（8方向环形布局） */
public enum MemoryKeyPosition implements StringRepresentable {

    NORTH(0, "north", 0, 0, -1),
    SOUTH(1, "south", 0, 0, 1),
    EAST(2, "east", 1, 0, 0),
    WEST(3, "west", -1, 0, 0),
    NORTH_EAST(4, "north_east", 1, 0, -1),
    NORTH_WEST(5, "north_west", -1, 0, -1),
    SOUTH_EAST(6, "south_east", 1, 0, 1),
    SOUTH_WEST(7, "south_west", -1, 0, 1);

    private static final Map<Integer, MemoryKeyPosition> ID_MAP = new HashMap<>();
    private static final Map<String, MemoryKeyPosition> NAME_MAP = new HashMap<>();

    static {
        for (MemoryKeyPosition pos : values()) {
            ID_MAP.put(pos.id, pos);
            NAME_MAP.put(pos.serializedName, pos);
        }
    }

    @Getter
    private final int id;           // 位置ID(0-7)
    private final String serializedName;
    @Getter
    private final int offsetX;      // X偏移
    @Getter
    private final int offsetY;      // Y偏移(始终为0)
    @Getter
    private final int offsetZ;      // Z偏移

    MemoryKeyPosition(int id, String serializedName, int offsetX, int offsetY, int offsetZ) {
        this.id = id;
        this.serializedName = serializedName;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    /** 根据ID获取枚举，无效时返回NORTH */
    public static MemoryKeyPosition fromId(int id) {
        return ID_MAP.getOrDefault(id, NORTH);
    }

    /** 计算相对于中心的绝对坐标 */
    public BlockPos getRelativePos(BlockPos center) {
        return center.offset(offsetX, offsetY, offsetZ);
    }

    /** 获取总位置数 */
    public static int getTotalPositions() {
        return values().length;
    }

    /** 获取所有位置相对于中心的坐标列表 */
    public static Iterable<BlockPos> getButtonPositions(BlockPos center) {
        BlockPos[] positions = new BlockPos[values().length];
        int i = 0;
        for (MemoryKeyPosition pos : values()) {
            positions[i++] = pos.getRelativePos(center);
        }
        return () -> java.util.Arrays.stream(positions).iterator();
    }
}
