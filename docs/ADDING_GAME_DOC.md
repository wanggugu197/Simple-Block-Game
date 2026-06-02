# 添加新游戏指南

本指南说明如何在 Simple Block Game 中添加一个新小游戏。目标是保持现有结构：游戏规则独立、Minecraft 交互集中、注册入口统一。

## 1. 命名规范

先确定 4 个名字：

| 项 | 示例 | 说明 |
| --- | --- | --- |
| 游戏名 | `Tetris` | 用于显示和文档 |
| 包名 | `simpleTetris` | 小写驼峰，在 `common/` 下 |
| 注册名 | `tetris_core`, `tetris_display` | 小写下划线 |
| 类名前缀 | `BlockTetrisCore`, `GameTetrisLogic` | 大驼峰 |

## 2. 创建目录结构

在 `src/main/java/com/simple_block_game/common/` 下创建：

```text
simpleXXX/
├── block/
│   ├── BlockXXXCore.java           # 核心方块（必须）
│   ├── BlockXXXCoreEntity.java     # 核心实体（必须）
│   ├── BlockXXXDisplay.java        # 显示方块（可选）
│   ├── BlockXXXDisplayEntity.java  # 显示实体（可选）
│   ├── BlockXXXButton.java         # 按钮方块（可选，如记忆键）
│   └── BlockXXXButtonEntity.java   # 按钮实体（可选）
├── data/
│   ├── XXXGameState.java           # 游戏状态枚举（推荐）
│   ├── XXXDifficulty.java          # 难度配置（可选）
│   └── XXXToken.java               # 游戏元素枚举（如24点的Token）
├── logic/
│   ├── GameXXXLogic.java           # 纯游戏逻辑（必须）
│   ├── GameXXXHelper.java          # 世界操作辅助（必须）
│   └── GameXXXReward.java          # 奖励系统（可选）
└── renderer/
    ├── BlockXXXCoreEntityRenderer.java      # 核心渲染器（必须）
    └── BlockXXXDisplayEntityRenderer.java   # 显示渲染器（可选）
```

## 3. 先写纯逻辑（推荐）

先实现 `GameXXXLogic`，**不要依赖 Minecraft API**。这样可以独立测试游戏规则。

**模板：**
```java
public final class GameXXXLogic {
    private GameXXXLogic() {}  // 私有构造器，防止实例化

    /** 初始化游戏数据 */
    public static GameData initGame() {
        return new GameData(...);
    }

    /** 处理玩家输入 */
    public static MoveResult processInput(GameData data, PlayerInput input) {
        // 只处理规则，不读写世界
        // 返回结果说明：是否成功、是否结束、是否需要更新显示
    }

    /** 判断游戏是否结束 */
    public static boolean isGameOver(GameData data) {
        return ...;
    }

    // 内部数据结构（使用 record 简化）
    public record GameData(int score, int level, int[] board) {}
    public record PlayerInput(int x, int y) {}
    public record MoveResult(boolean success, boolean gameOver, boolean needRefresh) {}
}
```

**要求：**
- 不导入 `net.minecraft.*`
- 输入输出使用普通 Java 类型（int、String、enum、record）
- 结果对象清晰说明操作结果
- 能单独写单元测试或手动调用验证

## 4. 定义状态和数据

### 4.1 游戏状态枚举

流程复杂的游戏建议定义状态枚举：

```java
public enum XXXGameState implements StringRepresentable {
    IDLE("idle"),
    PLAYING("playing"),
    SUCCESS("success"),
    GAME_OVER("game_over");

    private final String serializedName;

    XXXGameState(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
```

### 4.2 游戏元素枚举

如24点的 `GameToken24Puzzle`，包含数字、运算符等：

```java
public enum GameToken24Puzzle implements StringRepresentable {
    ONE("1", 1, true),
    ADD("+", -1, false),
    L_BRACKET("(", -5, false);

    private final String name;
    private final int value;
    private final boolean isNumber;

    // constructor, getters...
}
```

## 5. 实现核心方块实体

`BlockXXXCoreEntity` 负责保存游戏数据和状态同步。

**模板：**
```java
public class BlockXXXCoreEntity extends BaseGameBlockEntity {
    private static final String DATA_KEY = "XXXData";
    
    // 游戏状态
    private XXXGameState gameState = XXXGameState.IDLE;
    private int score;
    private int level;

    public BlockXXXCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_XXX_CORE_ENTITY.get(), pos, state);
    }

    // ===== 状态修改方法 =====
    public void startGame() {
        gameState = XXXGameState.PLAYING;
        score = 0;
        syncToClient();
    }

    public void addScore(int points) {
        score += points;
        syncToClient();
    }

    public void setGameState(XXXGameState state) {
        gameState = state;
        syncToClient();
    }

    public void reset() {
        gameState = XXXGameState.IDLE;
        score = 0;
        level = 1;
        syncToClient();
    }

    // ===== 状态同步（关键）=====
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    // ===== NBT 保存/读取 =====
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putString("State", gameState.getSerializedName());
        tag.putInt("Score", score);
        tag.putInt("Level", level);
        output.store(DATA_KEY, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(DATA_KEY, CompoundTag.CODEC).orElse(new CompoundTag());
        gameState = XXXGameState.valueOf(tag.getString("State"));
        score = tag.getIntOr("Score", 0);
        level = tag.getIntOr("Level", 1);
    }
}
```

### 5.1 状态同步规范

| 层级 | 允许操作 | 禁止操作 |
| --- | --- | --- |
| 实体层 (Entity) | 调用 `syncToClient()` | 直接调用 `sendBlockUpdated()` |
| 方块层 (Block) | 调用实体的方法 | 调用 `setChanged()` / `sendBlockUpdated()` |
| 逻辑层 (Logic) | 修改数据对象 | 任何同步操作 |

**核心原则**：状态同步逻辑必须统一在实体层实现。

## 6. 选择方块基类

| 布局类型 | 基类 | 适用场景 |
| --- | --- | --- |
| 旋转布局 | `BaseRotatedBlock` | 面向玩家朝向，如2048、24点 |
| 垂直布局 | `BaseVerticalBlock` | 固定朝上，如扫雷、数独 |

**核心方块必须实现 `IGameCoreBlock`**：

```java
public class BlockXXXCore extends BaseVerticalBlock implements IGameCoreBlock {
    
    public BlockXXXCore(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(UNFOLDED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockXXXCoreEntity(pos, state);
    }

    // ===== 生命周期方法 =====
    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(level, pos, state)) {
            player.sendOverlayMessage(Component.translatable("msg.common.obstructed"));
            return false;
        }
        GameXXXHelper.generateLayout(level, pos, state.getValue(FACING));
        level.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        startGame(level, pos, state, player);
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        GameXXXHelper.startGame(level, pos, state.getValue(FACING));
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameXXXHelper.resetGame(level, pos, state.getValue(FACING));
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameXXXHelper.minimizeLayout(level, pos, state.getValue(FACING));
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameXXXHelper.closeLayout(level, pos, state.getValue(FACING));
    }

    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameXXXHelper.checkLayoutAreaIsEmpty(level, pos, state.getValue(FACING));
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }
}
```

## 7. 实现 Helper

`GameXXXHelper` 负责所有世界操作，是方块层和逻辑层的桥梁。

**建议方法：**

| 方法 | 作用 |
| --- | --- |
| `checkLayoutAreaIsEmpty` | 展开前检查空间是否足够 |
| `generateLayout` | 放置显示方块、边框、刷新方块 |
| `startGame` | 初始化游戏数据并显示 |
| `resetGame` | 重置游戏数据和显示 |
| `minimizeLayout` | 移除布局，保留核心方块 |
| `closeLayout` | 移除布局和核心方块 |

**示例 - generateLayout：**
```java
public static void generateLayout(ServerLevel level, BlockPos corePos, Direction facing) {
    // 获取方块状态
    BlockState displayState = SimpleBlockGameRegistration.BLOCK_XXX_DISPLAY.get()
            .defaultBlockState().setValue(BaseVerticalBlock.FACING, facing);
    
    // 生成布局
    for (int i = 0; i < WIDTH; i++) {
        for (int j = 0; j < HEIGHT; j++) {
            BlockPos pos = calculatePosition(corePos, facing, i, j);
            if (shouldPlaceDisplay(i, j)) {
                level.setBlock(pos, displayState, Block.UPDATE_ALL);
                // 设置显示实体数据
            }
        }
    }
    
    // 设置刷新方块关联
    BlockPos refreshPos = getRefreshPosition(corePos, facing);
    if (level.getBlockEntity(refreshPos) instanceof BlockRefreshEntity refreshEntity) {
        refreshEntity.setCorePos(corePos);
    }
}
```

## 8. 刷新控制方块

刷新方块提供重置、最小化、关闭功能，通常使用基类即可：

```java
// 垂直布局
public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_XXX_REFRESH = REGISTRYLIB
        .block(REGISTRYLIB, "xxx_refresh", p -> BaseVerticalRefreshBlock.create(p, "xxx"))
        .register();

// 旋转布局
public static final BlockEntry<BaseRotatedRefreshBlock> BLOCK_XXX_REFRESH = REGISTRYLIB
        .block(REGISTRYLIB, "xxx_refresh", p -> BaseRotatedRefreshBlock.create(p, "xxx"))
        .register();
```

刷新方块的三个交互区域：

| 区域 | 操作 |
| --- | --- |
| 左上 | 最小化布局 |
| 右上 | 关闭游戏 |
| 下方 | 重置当前局 |

## 9. 实现显示方块（可选）

如果游戏需要棋盘显示，添加显示方块：

```java
public class BlockXXXDisplayEntity extends BaseGameBlockEntity {
    private int value;  // 显示的值
    
    public void setValue(int value) {
        if (this.value == value) return;  // 避免不必要的同步
        this.value = value;
        syncToClient();
    }
    
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
    
    // NBT 保存/读取...
}
```

## 10. 添加奖励系统（可选）

创建 `GameXXXReward` 继承 `BaseGameReward`：

```java
public final class GameXXXReward extends BaseGameReward {
    private GameXXXReward() {}

    public static boolean handleReward(ServerLevel level, Player player, BlockXXXCoreEntity entity) {
        if (!canReward(entity)) return false;
        
        Identifier rewardTable = determineRewardTable(entity);
        dropLoot(level, player, rewardTable);
        return true;
    }

    private static boolean canReward(BlockXXXCoreEntity entity) {
        return entity.getScore() >= SimpleBlockGameConfig.XXX_CONFIG.scoreThreshold.get();
    }

    private static Identifier determineRewardTable(BlockXXXCoreEntity entity) {
        return id(SimpleBlockGameConfig.XXX_CONFIG.defaultReward.get());
    }
}
```

## 11. 注册方块和实体

在 `SimpleBlockGameRegistration.java` 添加：

```java
// 核心方块
public static final BlockEntry<BlockXXXCore> BLOCK_XXX_CORE = REGISTRYLIB
        .block(REGISTRYLIB, "xxx_core", BlockXXXCore::new)
        .langCn("XXX核心方块")
        .lang("XXX Core")
        .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
        .item(builder -> builder.addTab(TAB_GANM.getKey())
                .model(() -> (item, prov) -> prov.createWithExistingModel(item, prov.modLoc("item/simple_xxx/xxx_core"))))
        .register();

// 显示方块（可选）
public static final BlockEntry<BlockXXXDisplay> BLOCK_XXX_DISPLAY = REGISTRYLIB
        .block(REGISTRYLIB, "xxx_display", BlockXXXDisplay::new)
        .langCn("XXX显示方块")
        .lang("XXX Display")
        .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_center"))
        .item(builder -> builder.addTab(TAB_GANM.getKey()))
        .register();

// 核心实体
public static final BlockEntityTypeEntry<BlockXXXCoreEntity> BLOCK_XXX_CORE_ENTITY = REGISTRYLIB
        .blockEntity(REGISTRYLIB, "xxx_core_entity", (_, p, s) -> new BlockXXXCoreEntity(p, s))
        .validBlock(BLOCK_XXX_CORE)
        .renderer(() -> () -> BlockXXXCoreEntityRenderer::new)
        .register();

// 刷新方块（追加到共享刷新实体）
public static final BlockEntityTypeEntry<BlockRefreshEntity> BLOCK_REFRESH_ENTITY = REGISTRYLIB
        .blockEntity(REGISTRYLIB, "refresh_entity", BlockRefreshEntity::new)
        .validBlock(BLOCK_2048_REFRESH)
        .validBlock(BLOCK_XXX_REFRESH)  // 追加新的刷新方块
        .register();
```

## 12. 添加配置

在 `SimpleBlockGameConfig.java` 添加：

```java
public static final XXXRewardConfig XXX_CONFIG = new XXXRewardConfig();
public static ModConfigSpec.BooleanValue enableXXXGame;

private static void initConfig() {
    BUILDER.push("Simple Block Game Settings");
    
    enableXXXGame = BUILDER.comment("Enable XXX game")
            .define("enable_xxx_game", true);
    
    BUILDER.pop();
    
    // ... 其他配置
    
    XXX_CONFIG.init(BUILDER);
}

public static class XXXRewardConfig {
    public ModConfigSpec.IntValue scoreThreshold;
    public ModConfigSpec.ConfigValue<String> reward;
    
    public void init(ModConfigSpec.Builder builder) {
        builder.push("XXX Game");
        
        scoreThreshold = builder.comment("Score threshold for reward")
                .defineInRange("xxx_score_threshold", 1000, 0, Integer.MAX_VALUE);
        reward = builder.comment("Loot table for reward")
                .define("xxx_reward", "minecraft:chests/simple_dungeon");
        
        builder.pop();
    }
}
```

核心方块入口检查开关：

```java
@Override
public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (!SimpleBlockGameConfig.enableXXXGame.get()) return InteractionResult.PASS;
    // ... 游戏逻辑
}
```

## 13. 添加配方

在 `SimpleBlockGameRecipe.java` 添加：

```java
// 核心方块配方
prov.shaped(RecipeCategory.COMBAT, BLOCK_XXX_CORE)
        .pattern("QQQ")
        .pattern("QIQ")
        .pattern("QQQ")
        .define('Q', Items.QUARTZ_PILLAR)
        .define('I', Items.REDSTONE_BLOCK)
        .unlockedBy("has_redstone", has(Items.REDSTONE_BLOCK))
        .save(prov, "make_xxx_core");
```

## 14. 添加渲染器

渲染器继承 `BaseBlockEntityRenderer`：

```java
public class BlockXXXCoreEntityRenderer extends BaseBlockEntityRenderer<BlockXXXCoreEntity, BlockXXXCoreEntityRenderState> {
    
    private static final Identifier TEXTURE_IDLE = SimpleBlockGame.getId("textures/block/simple_xxx/xxx_idle.png");
    private static final Identifier TEXTURE_PLAYING = SimpleBlockGame.getId("textures/block/simple_xxx/xxx_playing.png");

    public static class BlockXXXCoreEntityRenderState extends GameBlockEntityRenderState {
        public XXXGameState gameState;
        public int score;
    }

    @Override
    public BlockXXXCoreEntityRenderState createRenderState() {
        return new BlockXXXCoreEntityRenderState();
    }

    @Override
    public void extractRenderState(BlockXXXCoreEntity blockEntity, BlockXXXCoreEntityRenderState state, 
                                   float partialTicks, Vec3 cameraPosition, CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.gameState = blockEntity.getGameState();
        state.score = blockEntity.getScore();
    }

    @Override
    protected Identifier getTextureForState(BlockXXXCoreEntityRenderState state) {
        return switch (state.gameState) {
            case PLAYING -> TEXTURE_PLAYING;
            default -> TEXTURE_IDLE;
        };
    }
}
```

## 15. 添加资源文件

常用资源位置：

```text
src/main/resources/assets/simple_block_game/
├── models/
│   └── item/
│       └── simple_xxx/
│           └── xxx_core.json
└── textures/
    ├── block/
    │   └── simple_xxx/
    │       ├── xxx_idle.png
    │       └── xxx_playing.png
    └── item/
        └── xxx_core.png
```

## 16. 测试清单

实现后至少检查：

| 检查项 | 说明 |
| --- | --- |
| 方块放置 | 核心方块能正常放置和显示 |
| 布局展开 | 点击能展开，有障碍物时阻止 |
| 刷新操作 | 重置、最小化、关闭功能正常 |
| 状态保存 | 退出世界再进入数据正常 |
| 奖励系统 | 奖励只在预期时机发放 |
| 客户端渲染 | 贴图显示正常，状态同步及时 |
| 配方生成 | 数据生成正常，配方可使用 |

### 16.1 状态同步检查清单

- [ ] 所有状态修改都调用了 `syncToClient()`
- [ ] 方块层没有直接调用同步方法
- [ ] 客户端能实时看到服务端状态变化

## 17. 推荐开发顺序

```
1. 写 GameXXXLogic（纯规则，独立测试）
       ↓
2. 写 BlockXXXCoreEntity（数据保存和同步）
       ↓
3. 写 GameXXXHelper（布局生成和清理）
       ↓
4. 写 BlockXXXCore（生命周期方法）
       ↓
5. 注册核心方块和实体
       ↓
6. 添加显示方块和渲染器
       ↓
7. 添加奖励、配方、资源
       ↓
8. 运行数据生成和测试
```

## 18. 常见问题

### Q: 为什么纯逻辑类不能依赖 Minecraft？
A: 便于独立测试、代码复用、以及未来可能的移植。

### Q: 状态同步为什么要在实体层统一处理？
A: 避免重复代码，保证同步逻辑一致，便于调试。

### Q: 什么时候需要自定义刷新方块？
A: 只有当默认刷新逻辑（重置/最小化/关闭）无法满足需求时才需要。

### Q: 如何处理玩家交互？
A: 在 `BlockXXXCore#useWithoutItem` 中处理点击，通过 `GameXXXHelper` 调用 `GameXXXLogic`。

## 19. 参考示例

- **旋转布局**：参考 `simple2048` 或 `simple24Puzzle`
- **垂直布局**：参考 `simpleMinesweeper` 或 `simpleSudoku`
- **按钮交互**：参考 `simpleMemoryKey`
- **复杂逻辑**：参考 `simpleSudoku` 的数独生成算法