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
├── renderer/
│   ├── BlockXXXCoreEntityRenderer.java      # 核心渲染器（必须）
│   └── BlockXXXDisplayEntityRenderer.java   # 显示渲染器（可选）
└── simpleXXXRegistration.java      # 模块内注册入口（必须）
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

流程复杂的游戏建议定义状态枚举，需要实现 `StringRepresentable` 接口并提供 `fromSerializedName` 方法用于 NBT 反序列化：

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

    /** 从序列化名称获取枚举值（用于 NBT 读取） */
    public static XXXGameState fromSerializedName(String name) {
        for (XXXGameState state : values()) {
            if (state.serializedName.equals(name)) {
                return state;
            }
        }
        return IDLE;  // 默认返回 IDLE
    }

    /** 判断游戏是否已结束 */
    public boolean isGameEnded() {
        return this == GAME_OVER || this == SUCCESS;
    }

    /** 判断游戏是否可交互 */
    public boolean isInteractive() {
        return this == PLAYING;
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

`BlockXXXCoreEntity` 负责保存游戏数据和状态同步。使用 `lombok` 的 `@Getter` 和 `@Setter` 简化代码。

**模板：**
```java
public class BlockXXXCoreEntity extends BaseGameBlockEntity {
    // NBT 键常量
    private static final String KEY_DATA = "XXXGameData";
    private static final String KEY_GAME_STATE = "GameState";
    private static final String KEY_SCORE = "Score";
    private static final String KEY_LEVEL = "Level";
    
    @Getter
    private XXXGameState gameState = XXXGameState.IDLE;
    @Getter
    private int score = 0;
    @Getter
    private int level = 1;
    @Getter
    @Setter
    private Player currentPlayer;

    public BlockXXXCoreEntity(BlockPos pos, BlockState state) {
        // 使用模块内注册类引用实体类型
        super(simpleXXXRegistration.BLOCK_XXX_CORE_ENTITY.get(), pos, state);
    }

    /** 设置游戏状态 */
    public void setGameState(XXXGameState gameState) {
        this.gameState = gameState;
        syncToClient();
    }

    /** 添加分数 */
    public void addScore(int points) {
        score += points;
        syncToClient();
    }

    /** 开始游戏 */
    public void startGame() {
        gameState = XXXGameState.PLAYING;
        score = 0;
        syncToClient();
    }

    /** 重置游戏 */
    public void completeReset() {
        gameState = XXXGameState.IDLE;
        score = 0;
        level = 1;
        // 重置游戏数据...
        syncToClient();
    }

    /** 状态同步（关键） */
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    /** NBT 保存 */
    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        
        tag.putString(KEY_GAME_STATE, gameState.getSerializedName());
        tag.putInt(KEY_SCORE, score);
        tag.putInt(KEY_LEVEL, level);
        // 保存其他游戏数据...
        
        output.store(KEY_DATA, CompoundTag.CODEC, tag);
    }

    /** NBT 读取 */
    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(KEY_DATA, CompoundTag.CODEC).orElse(new CompoundTag());
        
        gameState = XXXGameState.fromSerializedName(tag.getStringOr(KEY_GAME_STATE, "idle"));
        score = tag.getIntOr(KEY_SCORE, 0);
        level = tag.getIntOr(KEY_LEVEL, 1);
        // 读取其他游戏数据...
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
| 垂直布局 | `BaseVerticalBlock` | 固定朝上，如扫雷、记忆键、十滴水、数独 |

**核心方块必须实现 `IGameCoreBlock`**：

```java
public class BlockXXXCore extends BaseVerticalBlock implements IGameCoreBlock {
    
    /** CODEC 定义（用于数据生成） */
    private static final MapCodec<BlockXXXCore> CODEC = simpleCodec(BlockXXXCore::new);

    public BlockXXXCore(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(UNFOLDED, false));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseVerticalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNFOLDED);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new BlockXXXCoreEntity(pos, state);
    }

    /** 提供服务端 ticker（用于游戏逻辑更新） */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NonNull BlockState state, 
                                                                  @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (_, _, _, entity) -> {
            if (entity instanceof BlockXXXCoreEntity coreEntity) {
                coreEntity.tick();
            }
        };
    }

    /** 玩家交互入口 */
    @Override
    public @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                     @NonNull BlockPos pos, @NonNull Player player,
                                                     @NonNull BlockHitResult hit) {
        // 检查游戏启用开关
        if (!SimpleBlockGameConfig.enableXXXGame.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.FAIL;

        if (!state.getValue(UNFOLDED)) {
            if (!checkLayoutAreaIsEmpty(serverLevel, pos, state)) {
                MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.common.obstructed"), true);
                return InteractionResult.PASS;
            }
            unfoldGame(serverLevel, pos, state, player);
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.xxx.game_started"), true);
        }

        return InteractionResult.SUCCESS;
    }

    // ===== 生命周期方法 =====
    @Override
    public boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos pos, BlockState state) {
        return GameXXXHelper.checkLayoutAreaIsEmpty(level, pos);
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        GameXXXHelper.generateLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), 3);
        
        getCoreEntity(level, pos).ifPresent(BlockXXXCoreEntity::initialize);
        startGame(level, pos, state, player);
        return true;
    }

    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        getCoreEntity(level, pos).ifPresent(entity -> {
            entity.setGameState(XXXGameState.PLAYING);
            MultiVersionHelper.sendPlayerMessage(player, Component.translatable("msg.xxx.start"), true);
        });
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        getCoreEntity(level, pos).ifPresent(BlockXXXCoreEntity::completeReset);
    }

    @Override
    public void minimizeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameXXXHelper.minimizeLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, false), 3);
    }

    @Override
    public void closeGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameXXXHelper.destroyLayout(level, pos);
    }

    @Override
    public BlockEntity getGameCoreEntity(ServerLevel level, BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    /** 获取核心实体（使用 Optional 避免空指针） */
    private Optional<BlockXXXCoreEntity> getCoreEntity(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BlockXXXCoreEntity entity ? Optional.of(entity) : Optional.empty();
    }
}
```

## 7. 实现 Helper

`GameXXXHelper` 负责所有世界操作，是方块层和逻辑层的桥梁。Helper 类应为 `final`，构造器为 `private`，所有方法为 `static`。

**建议方法：**

| 方法 | 作用 |
| --- | --- |
| `checkLayoutAreaIsEmpty` | 展开前检查空间是否足够 |
| `generateLayout` | 放置显示方块、边框、刷新方块 |
| `startGame` | 初始化游戏数据并显示 |
| `resetGame` | 重置游戏数据和显示 |
| `minimizeLayout` | 移除布局，保留核心方块 |
| `closeLayout` | 移除布局和核心方块 |
| `updateDisplay` | 根据游戏数据更新显示方块 |

**示例 - GameXXXHelper：**
```java
public final class GameXXXHelper {
    // 使用模块内注册类引用方块
    private static final Block FRAME = SimpleBlockGameRegistration.BLOCK_VERTICAL_FRAME.get();
    private static final Block DISPLAY = simpleXXXRegistration.BLOCK_XXX_DISPLAY.get();
    private static final Block REFRESH = simpleXXXRegistration.BLOCK_XXX_REFRESH.get();
    private static final Block CORE = simpleXXXRegistration.BLOCK_XXX_CORE.get();
    
    private static final int GRID_SIZE = 6;  // 网格大小常量
    
    private GameXXXHelper() {}  // 私有构造器
    
    /** 检查布局区域是否为空 */
    public static boolean checkLayoutAreaIsEmpty(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return false;
        
        for (int x = 0; x <= GRID_SIZE + 1; x++) {
            for (int z = 0; z <= GRID_SIZE + 1; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (!pos.equals(corePos) && !level.isEmptyBlock(pos)) return false;
            }
        }
        return level.isEmptyBlock(corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1));
    }
    
    /** 生成游戏布局 */
    public static void generateLayout(ServerLevel level, BlockPos corePos) {
        BlockState displayState = DISPLAY.defaultBlockState();
        BlockState frameState = FRAME.defaultBlockState();
        
        for (int x = 0; x <= GRID_SIZE + 1; x++) {
            for (int z = 0; z <= GRID_SIZE + 1; z++) {
                BlockPos pos = corePos.offset(x, 0, z);
                if (pos.equals(corePos)) continue;
                
                boolean isDisplay = x > 0 && x <= GRID_SIZE && z > 0 && z <= GRID_SIZE;
                level.setBlock(pos, isDisplay ? displayState : frameState, Block.UPDATE_ALL);
                
                if (isDisplay) initDisplayEntity(level, pos, corePos);
            }
        }
        
        // 放置刷新方块
        BlockPos refreshPos = corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1);
        level.setBlock(refreshPos, REFRESH.defaultBlockState(), Block.UPDATE_ALL);
        initRefreshEntity(level, refreshPos, corePos);
    }
    
    /** 最小化布局（移除显示和边框） */
    public static void minimizeLayout(ServerLevel level, BlockPos corePos) {
        if (corePos == null || !level.isLoaded(corePos)) return;
        
        for (int x = 0; x <= GRID_SIZE + 1; x++) {
            for (int z = 0; z <= GRID_SIZE + 1; z++) {
                Block block = level.getBlockState(corePos.offset(x, 0, z)).getBlock();
                if (block == FRAME || block == DISPLAY) {
                    level.removeBlock(corePos.offset(x, 0, z), false);
                }
            }
        }
        
        if (level.getBlockState(corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1)).getBlock() == REFRESH) {
            level.removeBlock(corePos.offset(GRID_SIZE + 1, 0, GRID_SIZE + 1), false);
        }
    }
    
    /** 销毁布局（包括核心方块） */
    public static void destroyLayout(ServerLevel level, BlockPos corePos) {
        minimizeLayout(level, corePos);
        if (corePos == null || !level.isLoaded(corePos)) return;
        
        if (level.getBlockState(corePos).getBlock() instanceof BlockXXXCore) {
            Vec3 center = Vec3.atCenterOf(corePos);
            ItemEntity item = new ItemEntity(level, center.x, center.y, center.z, new ItemStack(CORE));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
            level.removeBlock(corePos, false);
        }
    }
    
    /** 更新显示方块 */
    public static void updateDisplay(ServerLevel level, BlockPos corePos, int[][] grid) {
        for (int z = 0; z < GRID_SIZE; z++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPos displayPos = corePos.offset(x + 1, 0, z + 1);
                if (level.getBlockEntity(displayPos) instanceof BlockXXXDisplayEntity entity) {
                    entity.setValue(grid[z][x]);
                }
            }
        }
    }
    
    /** 初始化显示实体 */
    private static void initDisplayEntity(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (!level.isLoaded(pos)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockXXXDisplayEntity entity) {
            entity.setCorePos(corePos);
        }
    }
    
    /** 初始化刷新实体 */
    private static void initRefreshEntity(ServerLevel level, BlockPos pos, BlockPos corePos) {
        if (!level.isLoaded(pos)) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockRefreshEntity entity) {
            entity.setCorePos(corePos);
        }
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

## 11. 创建模块内注册文件

创建 `simpleXXXRegistration.java` 作为模块内注册入口：

```java
public class simpleXXXRegistration {
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

    // 刷新方块
    public static final BlockEntry<BaseVerticalRefreshBlock> BLOCK_XXX_REFRESH = REGISTRYLIB
            .block(REGISTRYLIB, "xxx_refresh", p -> BaseVerticalRefreshBlock.create(p, "xxx"))
            .register();
}
```

### 11.1 在主注册文件中集成

在 `SimpleBlockGameRegistration.java` 中需要：
1. 确保刷新方块实体包含新游戏的刷新方块
2. 确保配方和其他全局注册正确引用模块内注册项

```java
// 在 BLOCK_REFRESH_ENTITY 中追加新的刷新方块
.validBlock(simpleXXXRegistration.BLOCK_XXX_REFRESH)
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
5. 创建 simpleXXXRegistration（模块内注册）
       ↓
6. 在主注册文件集成（追加刷新方块）
       ↓
7. 添加显示方块和渲染器
       ↓
8. 添加奖励、配方、资源
       ↓
9. 运行数据生成和测试
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