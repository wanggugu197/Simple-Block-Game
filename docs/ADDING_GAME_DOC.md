# 添加新游戏指南

本指南说明如何在 Simple Block Game 中添加一个新小游戏。目标是保持现有结构：游戏规则独立、Minecraft 交互集中、注册入口统一。

## 1. 命名

先确定 4 个名字：

| 项 | 示例 |
| --- | --- |
| 游戏名 | `Tetris` |
| 包名 | `simpleTetris` |
| 注册名 | `tetris_core`, `tetris_display`, `tetris_refresh` |
| 类名前缀 | `BlockTetrisCore`, `GameTetrisLogic` |

建议使用小写下划线作为注册名，使用驼峰作为 Java 类名。

## 2. 创建目录

在 `src/main/java/com/simple_block_game/common/` 下创建：

```text
simpleXXX/
├── block/
│   ├── BlockXXXCore.java
│   ├── BlockXXXCoreEntity.java
│   ├── BlockXXXDisplay.java              # 可选
│   ├── BlockXXXDisplayEntity.java        # 可选
│   ├── BlockXXXButton.java               # 可选
│   └── BlockXXXButtonEntity.java         # 可选
├── data/
│   ├── XXXGameState.java                 # 可选但推荐
│   └── XXXDifficulty.java                # 可选
├── logic/
│   ├── GameXXXLogic.java
│   ├── GameXXXHelper.java
│   └── GameXXXReward.java                # 可选
└── renderer/
    ├── BlockXXXCoreEntityRenderer.java
    └── BlockXXXDisplayEntityRenderer.java
```

不是所有游戏都需要显示方块或按钮方块。只保留实际需要的类。

## 3. 先写纯逻辑

先实现 `GameXXXLogic`，不要依赖 Minecraft API。

推荐包含：

```java
public final class GameXXXLogic {
    private GameXXXLogic() {}

    public static GameData initGame(...) {
        return new GameData(...);
    }

    public static MoveResult processInput(GameData data, PlayerInput input) {
        // 只处理规则，不读写世界
    }

    public record GameData(...) {}
    public record PlayerInput(...) {}
    public record MoveResult(...) {}
}
```

要求：

- 不导入 `net.minecraft.*`
- 输入和输出使用普通 Java 类型
- 结果对象说明是否成功、是否结束、是否需要更新显示
- 能单独写测试或手动调用验证

## 4. 定义状态和数据

简单游戏可以只用字段保存状态。流程复杂的游戏建议加 `XXXGameState`：

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

状态值要能保存到 NBT，并能从存档恢复。

## 5. 实现核心方块实体

`BlockXXXCoreEntity` 负责保存游戏数据。

通常需要：

- 当前状态
- 分数、关卡、生命、步数等数据
- 游戏规则数据，如棋盘、雷区、序列
- `saveAdditional`
- `loadAdditional`
- 必要时实现 tick
- **`syncToClient()` 方法用于状态同步**

### 5.1 状态同步要求

所有状态修改必须通过 `syncToClient()` 方法同步到客户端，禁止直接在方块层调用 `setChanged()` 或 `sendBlockUpdated()`。

示例结构：

```java
public class BlockXXXCoreEntity extends BaseGameBlockEntity {
    private static final String DATA_KEY = "XXXData";

    private XXXGameState gameState = XXXGameState.IDLE;
    private int score;

    public BlockXXXCoreEntity(BlockPos pos, BlockState state) {
        super(SimpleBlockGameRegistration.BLOCK_XXX_CORE_ENTITY.get(), pos, state);
    }

    public void reset() {
        gameState = XXXGameState.IDLE;
        score = 0;
        syncToClient();  // 调用同步方法
    }

    public void setScore(int score) {
        this.score = score;
        syncToClient();  // 调用同步方法
    }

    /**
     * 同步数据到客户端
     * 所有状态修改都必须调用此方法
     */
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        tag.putString("State", gameState.getSerializedName());
        tag.putInt("Score", score);
        output.store(DATA_KEY, CompoundTag.CODEC, tag);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        CompoundTag tag = input.read(DATA_KEY, CompoundTag.CODEC).orElse(new CompoundTag());
        score = tag.getIntOr("Score", 0);
    }
}
```

### 5.2 状态同步规范

| 层级 | 允许调用 | 禁止调用 |
| --- | --- | --- |
| 实体层 (Entity) | `syncToClient()` | 直接调用 `sendBlockUpdated()` |
| 方块层 (Block) | 调用实体的 `syncToClient()` | `setChanged()`、`sendBlockUpdated()` |
| 逻辑层 (Helper/Logic) | 通过实体修改状态 | 任何同步方法 |

**核心原则**：状态同步逻辑必须统一在实体层实现，方块层只负责交互分发，不处理数据同步。

## 6. 选择方块基类

| 布局类型 | 使用 |
| --- | --- |
| 面向玩家朝向旋转 | `BaseRotatedBlock` |
| 固定垂直平面 | `BaseVerticalBlock` |

2048 使用 `BaseRotatedBlock`。扫雷、记忆键、十滴水使用垂直布局。

核心方块需要实现 `IGameCoreBlock`：

```java
public class BlockXXXCore extends BaseVerticalBlock implements IGameCoreBlock {
    public BlockXXXCore(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(UNFOLDED, false));
    }

    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        if (!checkLayoutAreaIsEmpty(level, pos, state)) return false;
        GameXXXHelper.generateLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), Block.UPDATE_ALL);
        return true;
    }

    @Override
    public void resetGame(ServerLevel level, BlockPos pos, BlockState state) {
        GameXXXHelper.resetLayout(level, pos);
    }
}
```

至少实现：

- `checkLayoutAreaIsEmpty`
- `unfoldGame`
- `startGame`
- `resetGame`
- `minimizeGame`
- `closeGame`
- `getGameCoreEntity`

## 7. 实现 Helper

`GameXXXHelper` 负责所有世界操作。

建议提供：

| 方法 | 作用 |
| --- | --- |
| `checkLayoutAreaIsEmpty` | 展开前检查空间 |
| `generateLayout` | 放置显示方块、边框、刷新方块 |
| `resetLayout` | 重置显示和核心实体 |
| `minimizeLayout` | 移除布局，保留核心方块 |
| `closeLayout` | 移除布局和核心方块 |
| `readDisplayGrid` | 从显示方块读取状态，可选 |
| `writeDisplayGrid` | 写入显示方块状态，可选 |

生成刷新方块后，要设置 `BlockRefreshEntity#setCorePos(corePos)`，否则刷新方块找不到核心方块。

## 8. 刷新控制方块

当前项目的刷新方块通常直接通过注册时创建：

```java
REGISTRYLIB
    .block(REGISTRYLIB, "xxx_refresh", p -> BaseVerticalRefreshBlock.create(p, "xxx"))
    .register();
```

旋转布局使用：

```java
BaseRotatedRefreshBlock.create(p, "xxx")
```

如果默认刷新逻辑无法满足需求，再新增独立 `BlockXXXRefresh`。

## 9. 实现显示方块

如果游戏需要棋盘或格子显示，添加：

- `BlockXXXDisplay`
- `BlockXXXDisplayEntity`
- `BlockXXXDisplayEntityRenderer`

显示实体保存单格状态，渲染器按状态选择贴图。

**显示实体也必须遵循状态同步规范**，使用 `syncToClient()` 方法：

示例：

```java
public class BlockXXXDisplayEntity extends BaseGameBlockEntity {
    private int value;

    public void setValue(int value) {
        if (this.value == value) return;
        this.value = value;
        syncToClient();
    }

    /**
     * 同步数据到客户端
     */
    private void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}
```

## 10. 添加奖励

如果游戏有奖励，创建 `GameXXXReward` 并继承 `BaseGameReward`。

推荐方式：

- 奖励表配置放到 `SimpleBlockGameConfig`
- 游戏成功、跨分数、通关等事件触发奖励
- 奖励逻辑不要写在纯逻辑类里

## 11. 注册方块和实体

在 `SimpleBlockGameRegistration.java` 添加：

```java
public static final BlockEntry<BlockXXXCore> BLOCK_XXX_CORE = REGISTRYLIB
        .block(REGISTRYLIB, "xxx_core", BlockXXXCore::new)
        .langCn("XXX核心方块")
        .lang("XXX Core")
        .blockstate(() -> (block, prov) -> createVerticalBlock(block, prov, "block/base/vertical_side"))
        .item(builder -> builder.addTab(TAB_GANM.getKey()))
        .register();

public static final BlockEntityTypeEntry<BlockXXXCoreEntity> BLOCK_XXX_CORE_ENTITY = REGISTRYLIB
        .blockEntity(REGISTRYLIB, "xxx_core_entity", (_, p, s) -> new BlockXXXCoreEntity(p, s))
        .validBlock(BLOCK_XXX_CORE)
        .renderer(() -> () -> BlockXXXCoreEntityRenderer::new)
        .register();
```

如果有显示方块，也注册显示方块和显示实体。

如果有刷新方块，别忘了把它加入共享刷新实体：

```java
public static final BlockEntityTypeEntry<BlockRefreshEntity> BLOCK_REFRESH_ENTITY = REGISTRYLIB
        .blockEntity(REGISTRYLIB, "refresh_entity", BlockRefreshEntity::new)
        .validBlock(BLOCK_XXX_REFRESH)
        .register();
```

实际代码中已有多个 `.validBlock(...)`，在链上追加新的刷新方块即可。

## 12. 添加配置

在 `SimpleBlockGameConfig.java` 添加：

- 游戏启用开关，如 `enableXXXGame`
- 奖励配置，如 `level1Reward`
- 尺寸、难度、生命等可调参数

核心方块交互入口应检查启用开关：

```java
if (!SimpleBlockGameConfig.enableXXXGame.get()) return InteractionResult.PASS;
```

## 13. 添加配方

在 `SimpleBlockGameRecipe.java` 添加核心方块配方：

```java
prov.shaped(RecipeCategory.COMBAT, BLOCK_XXX_CORE)
        .pattern("QQQ")
        .pattern("QIQ")
        .pattern("QQQ")
        .define('Q', Items.QUARTZ_PILLAR)
        .define('I', Items.REDSTONE_BLOCK)
        .unlockedBy("unlocked", UNCONDITIONAL_CRITERION)
        .save(prov, "make_xxx_core");
```

## 14. 添加资源

常用资源位置：

```text
src/main/resources/assets/simple_block_game/
├── models/item/simple_xxx/
├── textures/block/simple_xxx/
└── textures/item/
```

如果使用数据生成，确认注册时设置了：

- `langCn`
- `lang`
- `blockstate`
- `item`
- `model`

## 15. 添加渲染器

渲染器继承项目已有基类，核心是按实体状态返回贴图：

```java
protected Identifier getTextureForState(BlockXXXCoreEntityRenderState state) {
    return SimpleBlockGame.getId("textures/block/simple_xxx/xxx_core.png");
}
```

实体状态读取放在 `extractRenderState`。

## 16. 测试清单

实现后至少检查：

- 核心方块能放置和显示物品模型。
- 点击核心方块能展开布局。
- 展开前会阻止覆盖已有方块。
- 刷新方块能重置、最小化、关闭。
- 显示实体能保存和读取状态。
- 退出世界再进入后数据正常。
- 奖励只在预期时机发放。
- 客户端渲染贴图正常。
- 配方数据生成正常。

### 16.1 状态同步检查

- 所有状态修改都调用了 `syncToClient()` 方法。
- 方块层没有直接调用 `setChanged()` 或 `sendBlockUpdated()`。
- 客户端能实时看到服务端的状态变化（如分数更新、按钮闪烁）。

## 17. 推荐开发顺序

1. 写 `GameXXXLogic`。
2. 写 `BlockXXXCoreEntity`。
3. 写 `GameXXXHelper` 的布局生成和清理。
4. 写 `BlockXXXCore` 的生命周期。
5. 注册核心方块和实体。
6. 添加显示方块和渲染器。
7. 添加奖励、配方、资源。
8. 运行数据生成和客户端测试。