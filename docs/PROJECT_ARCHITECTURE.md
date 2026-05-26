# Simple Block Game 项目架构文档

## 一、项目概述

Simple Block Game 是一个基于 **Minecraft NeoForge** 的迷你游戏集合模组。项目采用**模块化设计**，支持快速添加新的小游戏，目前包含三个游戏模块：2048、扫雷和记忆键。

### 1.1 项目结构

```
Simple-Block-Game/
├── src/main/java/com/simple_block_game/
│   ├── common/                    # 公共代码
│   │   ├── base/                  # 基础框架
│   │   │   ├── block/             # 基础方块类
│   │   │   │   ├── BaseRotatedBlock.java       # 可旋转方块基类
│   │   │   │   ├── BaseVerticalBlock.java      # 垂直方块基类
│   │   │   │   ├── BaseRotatedRefreshBlock.java # 旋转刷新方块基类
│   │   │   │   ├── BaseVerticalRefreshBlock.java # 垂直刷新方块基类
│   │   │   │   ├── BlockRefreshEntity.java     # 刷新实体（共享）
│   │   │   │   ├── IGameCoreBlock.java         # 游戏核心接口
│   │   │   │   ├── RotatedFrame.java           # 旋转框架方块
│   │   │   │   └── VerticalFrame.java          # 垂直框架方块
│   │   │   ├── data/              # 共享数据类
│   │   │   │   ├── RotatedRefreshArea.java     # 旋转刷新区域枚举
│   │   │   │   └── VerticalRefreshArea.java    # 垂直刷新区域枚举
│   │   │   └── reward/            # 奖励系统基类
│   │   │       └── BaseGameReward.java         # 奖励基类
│   │   ├── simple2048/            # 2048游戏模块
│   │   ├── simpleMinesweeper/     # 扫雷游戏模块
│   │   ├── simpleMemoryKey/       # 记忆键游戏模块
│   │   ├── CommonInit.java        # 公共初始化
│   │   ├── SimpleBlockGameRecipe.java   # 配方注册
│   │   └── SimpleBlockGameRegistration.java # 方块/实体注册
│   ├── data/lang/                 # 语言处理
│   │   └── LangHandler.java       # 多语言处理
│   ├── registry/                  # 注册系统扩展
│   │   └── builder/               # 自定义注册构建器
│   │       ├── ModBlockBuilder.java
│   │       ├── ModEntityBuilder.java
│   │       ├── ModFluidBuilder.java
│   │       └── ModItemBuilder.java
│   ├── GameRegistryCore.java       # 注册核心扩展
│   ├── SimpleBlockGame.java        # 主类
│   └── SimpleBlockGameConfig.java  # 配置类
├── src/main/resources/            # 资源文件
├── build.gradle                   # 构建配置
└── docs/                          # 文档
```

---

## 二、核心框架

### 2.1 基础方块类层次结构

| 类名 | 父类 | 职责 | 方向属性 |
|------|------|------|---------|
| `BaseVerticalBlock` | `BaseEntityBlock` | 垂直固定方块基类 | 无 |
| `BaseRotatedBlock` | `BaseEntityBlock` | 水平旋转方块基类 | FACING（水平四方向） |
| `BaseVerticalRefreshBlock` | `BaseVerticalBlock` | 垂直刷新方块基类 | 无 |
| `BaseRotatedRefreshBlock` | `BaseRotatedBlock` | 旋转刷新方块基类 | FACING |

### 2.2 核心接口与实体

| 接口/类 | 职责 |
|---------|------|
| `IGameCoreBlock` | 游戏核心方块接口，定义生命周期管理方法 |
| `BlockRefreshEntity` | 刷新控制方块实体，存储关联的核心方块位置（共享） |
| `BaseGameReward` | 奖励系统基类，提供战利品掉落功能 |

### 2.3 IGameCoreBlock 接口

所有游戏核心方块必须实现此接口：

```java
public interface IGameCoreBlock {
    BooleanProperty UNFOLDED = BooleanProperty.create("unfolded");
    
    boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state);
    boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);
    void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);
    void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state);
    void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);
    void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);
    boolean isGameUnfolded(BlockState state);
    BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos);
}
```

### 2.4 刷新控制方块设计

刷新方块提供统一的游戏控制功能：

| 控制区域 | 功能 |
|---------|------|
| 左上区域 | 最小化游戏（保留数据，隐藏布局） |
| 右上区域 | 关闭游戏（移除布局，掉落核心方块） |
| 下方区域 | 重置游戏（保持布局，重置数据） |

---

## 三、游戏模块结构

每个游戏模块遵循统一的目录结构：

```
simpleXXX/
├── block/
│   ├── BlockXXXCore.java           # 核心方块（玩家交互入口）
│   ├── BlockXXXCoreEntity.java     # 核心方块实体（状态存储）
│   ├── BlockXXXDisplay.java        # 显示方块（可选）
│   ├── BlockXXXDisplayEntity.java  # 显示方块实体（可选）
│   ├── BlockXXXButton.java         # 按键方块（可选，记忆键专用）
│   ├── BlockXXXButtonEntity.java   # 按键方块实体（可选）
│   └── BlockXXXRefresh.java        # 刷新控制方块
├── data/
│   ├── XXXGameState.java           # 游戏状态枚举
│   └── XXXConfig/Difficulty.java   # 配置/难度枚举
└── logic/
    ├── GameXXXLogic.java           # 纯游戏逻辑（无Minecraft依赖）
    ├── GameXXXHelper.java          # Minecraft交互辅助方法
    └── GameXXXReward.java          # 奖励系统
```

---

## 四、现有游戏模块

### 4.1 模块概览

| 游戏 | 核心方块 | 交互方式 | 布局类型 |
|------|----------|----------|----------|
| **2048** | `Block2048Core` | 点击象限区域控制滑动方向 | 6×6 旋转布局 |
| **扫雷** | `BlockMinesweeperCore` | 左键翻开、右键标记旗帜 | 可变尺寸垂直布局 |
| **记忆键** | `BlockMemoryKeyCore` | 点击按键输入序列 | 3×3 垂直布局 |

### 4.2 各游戏核心特性

#### 2048 游戏
- **布局**：6×6（4×4 显示区域）
- **交互**：点击四个象限控制上下左右滑动
- **核心逻辑**：数字合并、随机生成、游戏结束判定

#### 扫雷游戏
- **布局**：可变尺寸（9×9 ~ 30×64）
- **交互**：左键翻开、右键标记
- **核心特性**：首次点击保护、BFS连锁翻开、多种难度配置

#### 记忆键游戏
- **布局**：3×3（中心核心，8方向按键）
- **交互**：观看演示序列后点击输入
- **核心特性**：6个难度关卡、容错机制、序列演示

---

## 五、添加新游戏的步骤

### 5.1 创建目录结构

```
src/main/java/com/simple_block_game/common/simpleNewGame/
├── block/
├── data/
└── logic/
```

### 5.2 实现核心方块实体

```java
public class BlockNewGameCoreEntity extends BlockEntity {
    private NewGameState gameState = NewGameState.IDLE;
    
    public void tick() { /* 游戏逻辑更新 */ }
    public void initGameData() { /* 初始化游戏数据 */ }
    public void completeReset() { /* 完全重置游戏状态 */ }
}
```

### 5.3 实现核心方块

```java
public class BlockNewGameCore extends BaseVerticalBlock implements IGameCoreBlock {
    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        GameNewGameHelper.generateLayout(level, pos);
        level.setBlock(pos, state.setValue(UNFOLDED, true), 3);
        return true;
    }
    
    // 实现其他 IGameCoreBlock 方法...
}
```

### 5.4 实现纯游戏逻辑

```java
public final class GameNewGameLogic {
    public static GameResult processInput(/* 参数 */) {
        // 纯逻辑处理，无Minecraft依赖
        return new GameResult();
    }
}
```

### 5.5 注册方块和实体

在 `SimpleBlockGameRegistration.java` 中添加：

```java
public static final BlockEntry<BlockNewGameCore> BLOCK_NEW_GAME_CORE = REGISTRYLIB
        .block(REGISTRYLIB, "new_game_core", BlockNewGameCore::new)
        .langCn("新游戏核心方块")
        .lang("New Game Core")
        .noBlockstate()
        .simpleItem()
        .register();

public static final BlockEntityTypeEntry<BlockNewGameCoreEntity> BLOCK_NEW_GAME_CORE_ENTITY = REGISTRYLIB
        .blockEntity(REGISTRYLIB, "new_game_core_entity", (_, p, s) -> new BlockNewGameCoreEntity(p, s))
        .validBlock(BLOCK_NEW_GAME_CORE)
        .register();
```

---

## 六、设计模式与架构原则

### 6.1 设计模式

| 模式 | 应用 | 说明 |
|------|------|------|
| **状态机模式** | 游戏状态管理 | 使用枚举管理游戏状态流转 |
| **实体Tick驱动** | 延迟逻辑处理 | 通过 `BlockEntity.tick()` 处理定时任务 |
| **策略模式** | 方向旋转（2048） | 根据方向动态调整旋转次数 |
| **模板方法模式** | 刷新方块基类 | 定义控制流程，子类实现具体逻辑 |

### 6.2 架构原则

1. **纯逻辑与交互分离**：游戏核心逻辑（`GameXXXLogic`）不依赖 Minecraft，可独立测试
2. **模块化设计**：每个游戏模块独立，便于维护和扩展
3. **共享实体**：三个游戏共享 `BlockRefreshEntity`，减少冗余
4. **统一接口**：`IGameCoreBlock` 确保所有游戏行为一致

---

## 七、关键技术点

### 7.1 方块更新标志（updateFlags）

项目中统一使用 `updateFlags = 3`（`NOTIFY_NEIGHBORS | NOTIFY_LISTENERS`）确保正确的客户端同步：

```java
level.setBlock(pos, state, 3);
```

### 7.2 实体序列化

所有 `BlockEntity` 实现 NBT 序列化，支持存档保存/加载：
- `saveAdditional()` / `loadAdditional()`：保存/加载私有数据
- `getUpdateTag()` / `getUpdatePacket()`：同步到客户端

### 7.3 多语言支持

通过 `LangHandler` 统一管理多语言翻译：

```java
LangHandler.addLang("key", "中文", "English");
```

---

## 八、文件清单

### 8.1 基础框架

| 文件 | 路径 | 说明 |
|------|------|------|
| 旋转方块基类 | `common/base/block/BaseRotatedBlock.java` | 支持水平方向朝向 |
| 垂直方块基类 | `common/base/block/BaseVerticalBlock.java` | 无方向属性 |
| 旋转刷新基类 | `common/base/block/BaseRotatedRefreshBlock.java` | 旋转刷新控制 |
| 垂直刷新基类 | `common/base/block/BaseVerticalRefreshBlock.java` | 垂直刷新控制 |
| 刷新实体 | `common/base/block/BlockRefreshEntity.java` | 共享刷新实体 |
| 游戏核心接口 | `common/base/block/IGameCoreBlock.java` | 生命周期管理 |
| 奖励基类 | `common/base/reward/BaseGameReward.java` | 战利品奖励 |

### 8.2 工具类

| 文件 | 路径 | 说明 |
|------|------|------|
| 注册类 | `common/SimpleBlockGameRegistration.java` | 方块/实体注册 |
| 配方类 | `common/SimpleBlockGameRecipe.java` | 合成配方注册 |
| 语言处理 | `data/lang/LangHandler.java` | 多语言支持 |
| 配置类 | `SimpleBlockGameConfig.java` | 模组配置 |

---

*文档版本: 1.1*  
*最后更新: 2026-05-27*