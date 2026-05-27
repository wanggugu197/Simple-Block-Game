# 添加新游戏指南

## 一、概述

本指南详细介绍如何向 Simple Block Game 项目中添加新的迷你游戏。项目采用模块化设计，遵循统一的架构模式，使添加新游戏变得简单高效。

### 1.1 前置条件

- 熟悉 Java 编程语言
- 了解 Minecraft NeoForge 开发基础
- 掌握项目架构（参考 `docs/PROJECT_ARCHITECTURE.md`）

### 1.2 开发流程概览

```
┌─────────────────────────────────────────────────────────────┐
│                    添加新游戏开发流程                        │
├─────────────────────────────────────────────────────────────┤
│  1. 创建目录结构                                            │
│       ↓                                                     │
│  2. 定义游戏状态枚举 (XXXGameState)                          │
│       ↓                                                     │
│  3. 实现纯游戏逻辑 (GameXXXLogic)                           │
│       ↓                                                     │
│  4. 实现核心方块实体 (BlockXXXCoreEntity)                   │
│       ↓                                                     │
│  5. 实现核心方块 (BlockXXXCore)                            │
│       ↓                                                     │
│  6. 实现刷新控制方块 (BlockXXXRefresh)                      │
│       ↓                                                     │
│  7. 注册方块和实体                                          │
│       ↓                                                     │
│  8. 添加资源文件（模型、纹理、语言）                         │
│       ↓                                                     │
│  9. 测试与调试                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、创建目录结构

在 `src/main/java/com/simple_block_game/common/` 下创建新游戏目录：

```
simpleXXX/                          # XXX为游戏名称，如 simpleTetris
├── block/                          # 方块层
│   ├── BlockXXXCore.java           # 核心方块
│   ├── BlockXXXCoreEntity.java     # 核心方块实体
│   ├── BlockXXXDisplay.java        # 显示方块（可选）
│   ├── BlockXXXDisplayEntity.java  # 显示方块实体（可选）
│   └── BlockXXXRefresh.java        # 刷新控制方块
├── data/                           # 数据层
│   ├── XXXGameState.java           # 游戏状态枚举
│   └── XXXDifficulty.java          # 难度配置枚举（可选）
├── logic/                          # 逻辑层
│   ├── GameXXXLogic.java           # 纯游戏逻辑
│   ├── GameXXXHelper.java          # Minecraft交互辅助
│   └── GameXXXReward.java          # 奖励系统（可选）
└── renderer/                       # 渲染器层（可选）
    ├── BlockXXXCoreEntityRenderer.java       # 核心实体渲染器
    └── BlockXXXDisplayEntityRenderer.java   # 显示实体渲染器（可选）
```

---

## 三、定义游戏状态枚举

创建 `data/XXXGameState.java`，定义游戏的状态机：

```java
package com.simple_block_game.common.simpleXXX.data;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

/** XXX游戏状态枚举 */
public enum XXXGameState implements StringRepresentable {
    
    IDLE("idle"),               // 空闲状态（未开始）
    PLAYING("playing"),         // 游戏进行中
    PAUSED("paused"),           // 暂停状态（可选）
    LEVEL_SUCCESS("level_success"), // 关卡成功
    ALL_SUCCESS("all_success"), // 全部通关
    GAME_OVER("game_over");     // 游戏结束

    private final String serializedName;

    XXXGameState(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }
}
```

**关键说明：**
- 实现 `StringRepresentable` 接口用于序列化
- `serializedName` 用于 NBT 存储和网络同步
- 状态设计根据游戏需求调整

---

## 四、实现纯游戏逻辑

创建 `logic/GameXXXLogic.java`，实现与 Minecraft 无关的纯游戏逻辑：

```java
package com.simple_block_game.common.simpleXXX.logic;

import java.util.List;

/** XXX游戏核心逻辑（无Minecraft依赖） */
public final class GameXXXLogic {

    /**
     * 处理玩家输入
     * @param gameData 游戏数据
     * @param input 玩家输入
     * @return 游戏结果
     */
    public static GameResult processInput(GameData gameData, PlayerInput input) {
        // 纯逻辑处理，不涉及任何Minecraft类
        boolean isSuccess = evaluateInput(gameData, input);
        
        if (isSuccess) {
            updateGameData(gameData);
            if (isGameComplete(gameData)) {
                return GameResult.success(true);
            }
            return GameResult.success(false);
        }
        
        return GameResult.fail();
    }

    /**
     * 判断游戏是否完成
     */
    public static boolean isGameComplete(GameData gameData) {
        // 实现游戏完成判定逻辑
        return false;
    }

    /**
     * 初始化游戏数据
     */
    public static GameData initializeGame(int difficulty) {
        // 创建并返回初始游戏数据
        return new GameData();
    }

    /**
     * 重置游戏数据
     */
    public static void resetGame(GameData gameData) {
        // 重置游戏状态
    }

    // 内部类：游戏数据（纯POJO）
    public static class GameData {
        // 游戏状态数据字段
    }

    // 内部类：玩家输入
    public static class PlayerInput {
        // 输入数据字段
    }

    // 内部类：游戏结果
    public static class GameResult {
        private final boolean success;
        private final boolean gameComplete;
        
        private GameResult(boolean success, boolean gameComplete) {
            this.success = success;
            this.gameComplete = gameComplete;
        }
        
        public static GameResult success(boolean gameComplete) {
            return new GameResult(true, gameComplete);
        }
        
        public static GameResult fail() {
            return new GameResult(false, false);
        }
        
        public boolean isSuccess() { return success; }
        public boolean isGameComplete() { return gameComplete; }
    }
}
```

**设计原则：**
- 不导入任何 `net.minecraft` 包
- 使用纯 Java 数据结构
- 便于单元测试和逻辑复用

---

## 五、实现核心方块实体

创建 `block/BlockXXXCoreEntity.java`，继承 `BlockEntity`：

```java
package com.simple_block_game.common.simpleXXX.block;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.simpleXXX.data.XXXGameState;
import com.simple_block_game.common.simpleXXX.logic.GameXXXLogic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** XXX游戏核心方块实体 */
public class BlockXXXCoreEntity extends BlockEntity {

    // === 常量定义 ===
    private static final String KEY_GAME_STATE = "GameState";
    private static final String KEY_SCORE = "Score";
    // ... 其他数据键

    // === 游戏状态字段 ===
    private XXXGameState gameState = XXXGameState.IDLE;
    private int score = 0;
    private GameXXXLogic.GameData gameData;

    // === 构造函数 ===
    public BlockXXXCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_XXX_CORE_ENTITY.get(), pos, state);
        this.gameData = GameXXXLogic.initializeGame(1);
    }

    // === 状态管理 ===
    public XXXGameState getGameState() {
        return gameState;
    }

    public void setGameState(XXXGameState gameState) {
        this.gameState = gameState;
        setChanged();
        updateBlockState();
    }

    // === 游戏逻辑 ===
    /**
     * 游戏主循环（由 tick 调用）
     */
    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // 根据游戏状态执行不同逻辑
        switch (gameState) {
            case PLAYING -> handlePlayingState(serverLevel);
            case LEVEL_SUCCESS -> handleLevelSuccess(serverLevel);
            case GAME_OVER -> handleGameOver(serverLevel);
            // ... 其他状态
        }
    }

    /**
     * 处理玩家输入
     */
    public void handlePlayerInput(int input) {
        if (gameState != XXXGameState.PLAYING) {
            return;
        }

        GameXXXLogic.PlayerInput playerInput = new GameXXXLogic.PlayerInput();
        // 设置输入数据
        
        GameXXXLogic.GameResult result = GameXXXLogic.processInput(gameData, playerInput);
        
        if (result.isSuccess()) {
            if (result.isGameComplete()) {
                setGameState(XXXGameState.ALL_SUCCESS);
            } else {
                // 更新分数等
                score += 100;
            }
        } else {
            setGameState(XXXGameState.GAME_OVER);
        }
        
        setChanged();
    }

    /**
     * 初始化游戏（首次展开）
     */
    public void initialize() {
        this.gameState = XXXGameState.IDLE;
        this.score = 0;
        this.gameData = GameXXXLogic.initializeGame(1);
        setChanged();
        updateBlockState();
    }

    /**
     * 完全重置游戏
     */
    public void completeReset() {
        GameXXXLogic.resetGame(gameData);
        this.gameState = XXXGameState.IDLE;
        this.score = 0;
        setChanged();
        updateBlockState();
    }

    /**
     * 更新方块状态（同步到方块属性）
     */
    private void updateBlockState() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState state = serverLevel.getBlockState(worldPosition);
        // 更新方块状态属性
        serverLevel.setBlock(worldPosition, state, 3);
        serverLevel.sendBlockUpdated(worldPosition, state, state, 3);
    }

    // === 序列化 ===
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        writeToTag(tag);
        output.store("XXXGameData", CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read("XXXGameData", CompoundTag.CODEC).orElse(new CompoundTag());
        readFromTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        writeToTag(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        HolderLookup.Provider registries = level != null ? level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
        return ClientboundBlockEntityDataPacket.create(this, (be, _) -> be.getUpdateTag(registries));
    }

    private void writeToTag(CompoundTag tag) {
        tag.putString(KEY_GAME_STATE, gameState.getSerializedName());
        tag.putInt(KEY_SCORE, score);
        // ... 保存其他数据
    }

    private void readFromTag(CompoundTag tag) {
        String gameStateStr = tag.getString(KEY_GAME_STATE).orElse(XXXGameState.IDLE.name());
        try {
            gameState = XXXGameState.valueOf(gameStateStr);
        } catch (IllegalArgumentException e) {
            gameState = XXXGameState.IDLE;
        }
        score = tag.getIntOr(KEY_SCORE, 0);
        // ... 读取其他数据
    }

    // === 状态处理方法 ===
    private void handlePlayingState(ServerLevel level) {
        // 游戏进行中的逻辑
    }

    private void handleLevelSuccess(ServerLevel level) {
        // 关卡成功处理
    }

    private void handleGameOver(ServerLevel level) {
        // 游戏结束处理
    }
}
```

---

## 六、实现核心方块

创建 `block/BlockXXXCore.java`，继承基类并实现 `IGameCoreBlock` 接口：

```java
package com.simple_block_game.common.simpleXXX.block;

import com.simple_block_game.common.base.block.BaseVerticalBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.simpleXXX.data.XXXGameState;
import com.simple_block_game.common.simpleXXX.logic.GameXXXHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** XXX游戏核心方块 */
public class BlockXXXCore extends BaseVerticalBlock implements IGameCoreBlock {

    // === 方块状态属性 ===
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty UNFOLDED = 
        net.minecraft.world.level.block.state.properties.BooleanProperty.create("unfolded");

    // === 构造函数 ===
    public BlockXXXCore(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(UNFOLDED, false));
    }

    // === 状态定义 ===
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNFOLDED);
        super.createBlockStateDefinition(builder);
    }

    // === IGameCoreBlock 实现 ===
    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameXXXHelper.checkLayoutAreaIsEmpty(level, pos);
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        // 生成游戏布局
        GameXXXHelper.generateLayout(level, pos);
        
        // 更新方块状态为已展开
        level.setBlock(pos, state.setValue(UNFOLDED, true), 3);
        
        // 初始化游戏数据
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockXXXCoreEntity entity) {
            entity.initialize();
        }
        
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockXXXCoreEntity entity) {
            entity.setGameState(XXXGameState.PLAYING);
            // 发送开始消息
            player.sendSystemMessage(
                net.minecraft.network.chat.Component.translatable("msg.xxx.start")
            );
        }
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockXXXCoreEntity entity) {
            entity.completeReset();
        }
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        // 移除布局方块
        GameXXXHelper.minimizeLayout(level, pos);
        
        // 更新方块状态为未展开
        level.setBlock(pos, state.setValue(UNFOLDED, false), 3);
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        // 销毁布局
        GameXXXHelper.destroyLayout(level, pos);
        
        // 掉落核心方块物品
        if (state.getBlock() instanceof BlockXXXCore) {
            dropAsItem(level, pos, state);
        }
    }

    @Override
    public boolean isGameUnfolded(BlockState state) {
        return state.getValue(UNFOLDED);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    // === 方块交互 ===
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // 清理布局
            if (isGameUnfolded(state)) {
                GameXXXHelper.destroyLayout((ServerLevel) level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // === 辅助方法 ===
    private void dropAsItem(ServerLevel level, BlockPos pos, BlockState state) {
        // 实现掉落逻辑
        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
            level,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            new net.minecraft.world.item.ItemStack(this)
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}
```

---

## 七、实现刷新控制方块

创建 `block/BlockXXXRefresh.java`，继承对应的刷新基类：

```java
package com.simple_block_game.common.simpleXXX.block;

import com.simple_block_game.common.base.block.BaseVerticalRefreshBlock;
import com.simple_block_game.common.base.block.IGameCoreBlock;
import com.simple_block_game.common.base.block.VerticalRefreshArea;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** XXX游戏刷新控制方块 */
public class BlockXXXRefresh extends BaseVerticalRefreshBlock {

    public BlockXXXRefresh(Properties properties) {
        super(properties);
    }

    @Override
    protected void handleRefreshAction(ServerLevel level, BlockPos refreshPos, BlockPos corePos, 
                                       VerticalRefreshArea area, BlockState refreshState) {
        if (!(level.getBlockState(corePos).getBlock() instanceof IGameCoreBlock coreBlock)) {
            return;
        }

        switch (area) {
            case TOP_LEFT -> coreBlock.minimizeGame(level, corePos, level.getBlockState(corePos));
            case TOP_RIGHT -> coreBlock.closeGame(level, corePos, level.getBlockState(corePos));
            case BOTTOM -> coreBlock.resetGame(level, corePos, level.getBlockState(corePos));
        }
    }
}
```

---

## 八、实现辅助方法

创建 `logic/GameXXXHelper.java`，封装 Minecraft 交互逻辑：

```java
package com.simple_block_game.common.simpleXXX.logic;

import com.simple_block_game.common.SimpleBlockGameRegistration;
import com.simple_block_game.common.base.block.BlockRefreshEntity;
import com.simple_block_game.common.simpleXXX.block.BlockXXXDisplayEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** XXX游戏辅助工具类 */
public final class GameXXXHelper {

    // === 方块引用 ===
    private static final Block CORE = SimpleBlockGameRegistration.BLOCK_XXX_CORE.get();
    private static final Block DISPLAY = SimpleBlockGameRegistration.BLOCK_XXX_DISPLAY.get();
    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block REFRESH = SimpleBlockGameRegistration.BLOCK_XXX_REFRESH.get();

    private GameXXXHelper() {}

    // === 布局管理 ===
    /**
     * 检查布局区域是否为空
     */
    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        // 检查所有需要放置方块的位置
        for (BlockPos pos : getLayoutPositions(corePos)) {
            if (!level.isEmptyBlock(pos)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成游戏布局
     */
    public static void generateLayout(ServerLevel level, BlockPos corePos) {
        BlockState displayState = DISPLAY.defaultBlockState();
        BlockState frameState = FRAME.defaultBlockState();
        BlockState refreshState = REFRESH.defaultBlockState();

        // 生成显示方块
        for (BlockPos pos : getDisplayPositions(corePos)) {
            if (level.isEmptyBlock(pos)) {
                level.setBlock(pos, displayState, 3);
                // 初始化显示方块实体
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BlockXXXDisplayEntity entity) {
                    entity.setCorePos(corePos);
                    // 设置初始状态
                }
            }
        }

        // 生成边框
        for (BlockPos pos : getFramePositions(corePos)) {
            if (level.isEmptyBlock(pos)) {
                level.setBlock(pos, frameState, 3);
            }
        }

        // 生成刷新按钮
        BlockPos refreshPos = getRefreshPos(corePos);
        if (level.isEmptyBlock(refreshPos)) {
            level.setBlock(refreshPos, refreshState, 3);
            if (level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
                refreshEntity.setCorePos(corePos);
            }
        }
    }

    /**
     * 最小化布局（保留核心方块）
     */
    public static void minimizeLayout(ServerLevel level, BlockPos corePos) {
        // 移除显示方块
        for (BlockPos pos : getDisplayPositions(corePos)) {
            if (level.getBlockState(pos).getBlock() == DISPLAY) {
                level.removeBlock(pos, false);
            }
        }

        // 移除边框
        for (BlockPos pos : getFramePositions(corePos)) {
            if (level.getBlockState(pos).getBlock() == FRAME) {
                level.removeBlock(pos, false);
            }
        }

        // 移除刷新按钮
        BlockPos refreshPos = getRefreshPos(corePos);
        if (level.getBlockState(refreshPos).getBlock() == REFRESH) {
            level.removeBlock(refreshPos, false);
        }
    }

    /**
     * 销毁布局（完全移除）
     */
    public static void destroyLayout(ServerLevel level, BlockPos corePos) {
        minimizeLayout(level, corePos);
        
        // 如果需要，也可以移除核心方块（通常由 closeGame 处理）
    }

    // === 位置计算 ===
    private static Iterable<BlockPos> getLayoutPositions(BlockPos corePos) {
        // 返回所有需要检查的位置
        return () -> java.util.stream.Stream.concat(
            getDisplayPositions(corePos).stream(),
            java.util.stream.Stream.concat(
                getFramePositions(corePos).stream(),
                java.util.stream.Stream.of(getRefreshPos(corePos))
            )
        ).iterator();
    }

    private static Iterable<BlockPos> getDisplayPositions(BlockPos corePos) {
        // 返回显示方块位置列表
        BlockPos[] positions = {
            // 根据游戏布局定义位置
            corePos.offset(0, 0, 1),
            corePos.offset(0, 0, -1),
            // ...
        };
        return () -> java.util.Arrays.stream(positions).iterator();
    }

    private static Iterable<BlockPos> getFramePositions(BlockPos corePos) {
        // 返回边框位置列表
        BlockPos[] positions = {
            // 边框位置
        };
        return () -> java.util.Arrays.stream(positions).iterator();
    }

    private static BlockPos getRefreshPos(BlockPos corePos) {
        // 返回刷新按钮位置
        return corePos.offset(2, 0, 2);
    }

    // === 游戏交互辅助 ===
    /**
     * 更新显示方块状态
     */
    public static void updateDisplay(ServerLevel level, BlockPos corePos, int displayIndex, int value) {
        BlockPos displayPos = getDisplayPosition(corePos, displayIndex);
        BlockEntity be = level.getBlockEntity(displayPos);
        if (be instanceof BlockXXXDisplayEntity entity) {
            entity.setValue(value);
        }
    }

    private static BlockPos getDisplayPosition(BlockPos corePos, int index) {
        // 根据索引获取显示方块位置
        return corePos;
    }
}
```

---

## 九、注册方块和实体

在 `SimpleBlockGameRegistration.java` 中添加注册代码：

```java
// === XXX游戏注册 ===

// 核心方块
public static final BlockEntry<BlockXXXCore> BLOCK_XXX_CORE = REGISTRYLIB
    .block(REGISTRYLIB, "xxx_core", BlockXXXCore::new)
    .langCn("XXX核心方块")
    .lang("XXX Core")
    .noBlockstate()
    .simpleItem()
    .register();

// 核心方块实体
public static final BlockEntityTypeEntry<BlockXXXCoreEntity> BLOCK_XXX_CORE_ENTITY = REGISTRYLIB
    .blockEntity(REGISTRYLIB, "xxx_core_entity", (_, p, s) -> new BlockXXXCoreEntity(p, s))
    .validBlock(BLOCK_XXX_CORE)
    .register();

// 显示方块（如果需要）
public static final BlockEntry<BlockXXXDisplay> BLOCK_XXX_DISPLAY = REGISTRYLIB
    .block(REGISTRYLIB, "xxx_display", BlockXXXDisplay::new)
    .langCn("XXX显示方块")
    .lang("XXX Display")
    .noBlockstate()
    .register();

// 显示方块实体（如果需要）
public static final BlockEntityTypeEntry<BlockXXXDisplayEntity> BLOCK_XXX_DISPLAY_ENTITY = REGISTRYLIB
    .blockEntity(REGISTRYLIB, "xxx_display_entity", (_, p, s) -> new BlockXXXDisplayEntity(p, s))
    .validBlock(BLOCK_XXX_DISPLAY)
    .register();

// 刷新控制方块
public static final BlockEntry<BlockXXXRefresh> BLOCK_XXX_REFRESH = REGISTRYLIB
    .block(REGISTRYLIB, "xxx_refresh", BlockXXXRefresh::new)
    .langCn("XXX刷新方块")
    .lang("XXX Refresh")
    .noBlockstate()
    .register();
```

---

## 十、添加资源文件

### 10.1 语言文件

在 `src/main/resources/assets/simple_block_game/lang/` 下创建/更新语言文件：

**中文 (`zh_cn.json`)：**
```json
{
  "block.simple_block_game.xxx_core": "XXX核心方块",
  "block.simple_block_game.xxx_display": "XXX显示方块",
  "block.simple_block_game.xxx_refresh": "XXX刷新方块",
  "msg.xxx.start": "游戏开始！",
  "msg.xxx.game_over": "游戏结束！",
  "msg.xxx.success": "恭喜通关！"
}
```

**英文 (`en_us.json`)：**
```json
{
  "block.simple_block_game.xxx_core": "XXX Core",
  "block.simple_block_game.xxx_display": "XXX Display",
  "block.simple_block_game.xxx_refresh": "XXX Refresh",
  "msg.xxx.start": "Game Start!",
  "msg.xxx.game_over": "Game Over!",
  "msg.xxx.success": "Congratulations!"
}
```

### 10.2 方块模型和纹理

创建模型文件和纹理文件：

```
src/main/resources/assets/simple_block_game/
├── blockstates/
│   └── xxx_core.json
├── models/
│   ├── block/
│   │   ├── xxx_core.json
│   │   ├── xxx_display.json
│   │   └── xxx_refresh.json
│   └── item/
│       └── xxx_core.json
└── textures/
    └── block/
        ├── xxx_core.png
        ├── xxx_display.png
        └── xxx_refresh.png
```

### 10.3 配方注册（可选）

在 `SimpleBlockGameRecipe.java` 中添加合成配方：

```java
// XXX核心方块配方
RECIPES.add(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BLOCK_XXX_CORE.get())
    .pattern("ABA")
    .pattern("BCB")
    .pattern("ABA")
    .define('A', Blocks.IRON_BLOCK)
    .define('B', Blocks.GLASS)
    .define('C', Items.REDSTONE)
    .unlockedBy("has_redstone", has(Items.REDSTONE))
    .build());
```

---

## 十一、实现渲染器（可选）

创建 `renderer/BlockXXXCoreEntityRenderer.java`，继承渲染器基类：

```java
package com.simple_block_game.common.simpleXXX.renderer;

import com.simple_block_game.common.base.renderer.BaseGameBlockEntityRenderer;
import com.simple_block_game.common.simpleXXX.block.BlockXXXCoreEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/** XXX游戏核心实体渲染器 */
public class BlockXXXCoreEntityRenderer extends BaseGameBlockEntityRenderer<BlockXXXCoreEntity> {

    public BlockXXXCoreEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderGameContent(BlockXXXCoreEntity entity, float partialTick, 
                                     PoseStack poseStack, MultiBufferSource bufferSource, 
                                     int packedLight, int packedOverlay) {
        // 渲染游戏特定内容
        // 例如：渲染分数、状态指示器等
        
        // 应用旋转
        applyRotation(poseStack, entity);
        
        // 渲染逻辑
        // ...
    }
}
```

**渲染器基类功能：**
- 提供通用的旋转处理方法 `applyRotation()`
- 处理方块朝向的渲染适配
- 统一渲染上下文管理

**注册渲染器：**

在 `ClientInit.java` 中注册：
```java
// XXX游戏渲染器注册
RenderingRegistry.registerBlockEntityRenderer(
    SimpleBlockGameRegistration.BLOCK_XXX_CORE_ENTITY.get(),
    context -> new BlockXXXCoreEntityRenderer(context)
);
```

---

## 十二、添加奖励系统（可选）

创建 `logic/GameXXXReward.java`：

```java
package com.simple_block_game.common.simpleXXX.logic;

import com.simple_block_game.common.base.reward.BaseGameReward;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/** XXX游戏奖励系统 */
public final class GameXXXReward extends BaseGameReward {

    /**
     * 处理游戏奖励
     * @param level 服务端世界
     * @param player 玩家
     * @param success 是否成功通关
     */
    public static void handleReward(ServerLevel level, Player player, boolean success) {
        if (success) {
            // 通关奖励：更好的战利品
            dropLoot(level, player.position(), getSuccessLootTable());
        } else {
            // 失败奖励：基础战利品
            dropLoot(level, player.position(), getFailureLootTable());
        }
    }

    private static net.minecraft.resources.ResourceLocation getSuccessLootTable() {
        return new net.minecraft.resources.ResourceLocation("simple_block_game", "game/xxx_success");
    }

    private static net.minecraft.resources.ResourceLocation getFailureLootTable() {
        return new net.minecraft.resources.ResourceLocation("simple_block_game", "game/xxx_failure");
    }
}
```

---

## 十二、测试与调试

### 12.1 测试清单

| 测试项 | 说明 |
|--------|------|
| 方块放置 | 核心方块能否正常放置 |
| 布局展开 | 点击核心方块能否展开游戏布局 |
| 游戏开始 | 游戏能否正常开始 |
| 玩家交互 | 玩家输入能否正确响应 |
| 状态流转 | 状态机是否正常工作 |
| 刷新控制 | 三个控制区域功能是否正常 |
| 存档加载 | 游戏数据能否正确保存和加载 |
| 奖励掉落 | 通关/失败时奖励是否正常掉落 |

### 12.2 调试技巧

1. **日志输出**：使用 `LOGGER.info()` 输出关键状态
2. **调试模式**：在游戏中使用 `/debug` 命令
3. **断点调试**：使用 IDE 连接 Minecraft 调试器
4. **测试世界**：创建专门的测试世界进行测试

---

## 十三、参考示例

参考现有游戏模块的实现：

| 游戏 | 路径 | 说明 |
|------|------|------|
| 2048 | `common/simple2048/` | 旋转布局，滑动交互 |
| 扫雷 | `common/simpleMinesweeper/` | 垂直布局，点击翻开 |
| 记忆键 | `common/simpleMemoryKey/` | 垂直布局，序列输入 |

---

## 十四、常见问题

### Q1: 如何选择继承基类？

- **垂直布局游戏**（扫雷、记忆键）：继承 `BaseVerticalBlock`
- **旋转布局游戏**（2048）：继承 `BaseRotatedBlock`

### Q2: 为什么要分离纯逻辑和交互代码？

- 纯逻辑代码可独立测试
- 便于移植到其他平台
- 代码结构更清晰

### Q3: 如何处理多语言？

使用 `LangHandler.addLang()` 在 `CommonInit.java` 中注册语言键。

### Q4: 方块更新标志 `updateFlags` 应该用多少？

项目中统一使用 `3`（`NOTIFY_NEIGHBORS | NOTIFY_LISTENERS`）。

---

*文档版本: 1.2*  
*最后更新: 2026-05-29*  
*参考文档: `docs/PROJECT_ARCHITECTURE.md`*