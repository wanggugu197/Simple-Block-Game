# 💣 扫雷游戏代码文档

## 一、项目架构

### 1.1 模块结构

```
src/main/java/com/simple_block_game/common/simpleMinesweeper/
├── block/                    # 方块类
│   ├── BlockMinesweeperCore.java          # 核心方块（玩家交互入口）
│   ├── BlockMinesweeperCoreEntity.java    # 核心方块实体（存储游戏数据）
│   ├── BlockMinesweeperDisplay.java       # 显示方块（显示格子状态）
│   ├── BlockMinesweeperDisplayEntity.java # 显示方块实体
│   └── BlockMinesweeperRefresh.java       # 刷新控制方块
├── data/                     # 数据类
│   ├── MinesweeperState.java              # 格子状态枚举
│   └── PresetDifficulty.java              # 预设难度枚举
└── logic/                    # 逻辑类
    ├── GameMinesweeperLogic.java          # 游戏核心逻辑
    ├── GameMinesweeperHelper.java         # Minecraft交互辅助方法
    └── GameMinesweeperReward.java         # 奖励系统
```

---

## 二、核心组件说明

### 2.1 方块类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `BlockMinesweeperCore` | 游戏核心方块，处理玩家点击输入和难度配置 | `useWithoutItem()`, `unfoldGame()`, `startGame()` |
| `BlockMinesweeperCoreEntity` | 核心方块实体，存储游戏状态（地雷网格、显示网格、旗帜计数） | `getMineGrid()`, `getDisplayGrid()`, `initGameData()` |
| `BlockMinesweeperDisplay` | 显示方块，显示格子状态（未打开/旗帜/数字/炸弹） | `getDisplayState()`, `setDisplayState()` |
| `BlockMinesweeperDisplayEntity` | 显示方块实体，存储当前状态 | `getState()`, `setState()` |
| `BlockMinesweeperRefresh` | 刷新控制方块，提供重置/最小化/关闭功能 | `useWithoutItem()` |

### 2.2 数据类

#### 格子状态枚举 (`MinesweeperState`)

| 状态 | 值 | 说明 |
|------|-----|------|
| `UNOPENED` | 0 | 未打开 |
| `FLAGGED` | -1 | 旗帜标记 |
| `OPEN_EMPTY` | -5 | 已打开（空白） |
| `BOMB` | -9 | 炸弹 |
| `DEATH_BOMB` | -10 | 炸死玩家的炸弹 |
| `NUMBER_1~8` | 1~8 | 周围地雷数量 |
| `WRONG_FLAG_1~8` | -11~-18 | 错误旗帜标记 |

#### 预设难度枚举 (`PresetDifficulty`)

| 难度 | 宽度 | 高度 | 地雷数 |
|------|------|------|--------|
| `EASY` | 9 | 9 | 10 |
| `NORMAL` | 16 | 16 | 40 |
| `HARD` | 16 | 30 | 99 |
| `EXPERT` | 30 | 64 | 450 |
| `CUSTOM` | 自定义 | 自定义 | 自定义 |

### 2.3 逻辑类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `GameMinesweeperLogic` | 纯游戏逻辑（无Minecraft依赖） | `generateMineGrid()`, `processFlip()`, `processFlag()` |
| `GameMinesweeperHelper` | Minecraft世界交互方法 | `placeLayoutBlocks()`, `readDisplayGrid()`, `writeDisplayGrid()` |
| `GameMinesweeperReward` | 通关奖励系统 | `handleWinReward()` |

---

## 三、游戏流程

### 3.1 布局结构

游戏展开后形成 (width+2) × (height+2) 的布局：

```
┌──────────────────────────────┐
│  Frame  Frame  ...  Frame    │
│  Frame Display ... Display   │
│  Frame Display ... Display   │
│  ...                        │
│  Frame Display ... Display   │
│  Frame  Frame  ... Refresh   │
└──────────────────────────────┘
    ↑
  Core (位置0,0)
```

核心方块位于左下角(0,0)，显示区域为 width×height，控制方块位于右上角。

### 3.2 玩家交互

#### 顶部交互区域

```
        ┌─────────────────────────┐
        │ NW Corner │ Center 8x8  │ NE Corner │
        │ (难度-)   │ (展开/开始)  │ (地雷+)   │
        ├─────────────────────────┤
        │ SW Corner │    ...      │ SE Corner │
        │ (难度+)   │             │ (地雷-)   │
        └─────────────────────────┘
```

- **中心8×8区域**：未展开时展开游戏，已展开时重新开始
- **NW/SW角落**：调整难度等级（循环切换）
- **NE/SE角落**（仅自定义模式）：调整地雷数量

#### 侧面交互区域（仅自定义模式）

- **北侧/南侧**：调整宽度
- **东侧/西侧**：调整高度

#### 显示区域交互

- **左键点击**：翻开格子
- **右键点击**：标记/取消旗帜

### 3.3 核心方法调用链

**展开游戏**：
```
unfoldGame() → isAreaEmpty() → placeLayoutBlocks() → setGameStarted(true)
```

**开始游戏**：
```
startGame() → initLayoutEntities() → initGameData() → resetLayout()
```

**玩家翻开**：
```
useWithoutItem() → processFlip() → chainFlip() → writeDisplayGrid() → handleWinReward()
```

---

## 四、关键实现细节

### 4.1 首次点击保护

首次点击时，以点击位置为中心的3×3区域会被排除在雷区之外：

```java
private static boolean isSafe(int x, int z, int startX, int startZ) {
    return Math.abs(x - startX) <= 1 && Math.abs(z - startZ) <= 1;
}
```

### 4.2 连锁翻开算法

当翻开空白格子时，自动展开周围区域：

```java
public static void chainFlip(boolean[][] mineGrid, MinesweeperState[][] displayGrid, int startX, int startZ) {
    Queue<int[]> queue = new LinkedList<>();
    queue.add(new int[] { startX, startZ });
    while (!queue.isEmpty()) {
        int[] pos = queue.poll();
        // 检查周围8个方向
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                // 递归展开空白区域
            }
        }
    }
}
```

### 4.3 胜利判定

所有非地雷格子被翻开且所有地雷被标记为旗帜：

```java
public static boolean isWin(boolean[][] mineGrid, MinesweeperState[][] displayGrid) {
    for (int z = 0; z < h; z++) {
        for (int x = 0; x < w; x++) {
            if (!mineGrid[z][x] && displayGrid[z][x] == UNOPENED) return false;
            if (mineGrid[z][x] && displayGrid[z][x] != FLAGGED) return false;
        }
    }
    return true;
}
```

---

## 五、添加新游戏的模板

参考项目中已有的三个游戏实现模式：

### 5.1 核心方块实体模板

```java
public class BlockNewGameCoreEntity extends BlockEntity {
    private GameState gameState = GameState.IDLE;
    
    public void tick() { /* 游戏逻辑更新 */ }
    public void initGameData() { /* 初始化游戏数据 */ }
    public void completeReset() { /* 完全重置 */ }
}
```

### 5.2 核心方块模板

```java
public class BlockNewGameCore extends BaseVerticalBlock implements IGameCoreBlock {
    @Override
    public boolean unfoldGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        // 生成布局
        GameNewGameHelper.placeLayoutBlocks(level, pos, width, height);
        level.setBlock(pos, state.setValue(GAME_STARTED, true), 3);
        return true;
    }
    
    @Override
    public void startGame(ServerLevel level, BlockPos pos, BlockState state, Player player) {
        // 初始化游戏数据
    }
    
    // ... 其他IGameCoreBlock方法
}
```

### 5.3 纯逻辑类模板

```java
public final class GameNewGameLogic {
    public static GameResult processInput(GameData data, Input input) {
        // 纯逻辑处理，无Minecraft依赖
    }
}
```

---

## 六、设计模式

### 6.1 纯逻辑与交互分离

游戏核心逻辑(`GameMinesweeperLogic`)不依赖Minecraft，可以独立测试。

### 6.2 实体存储状态

游戏状态通过`BlockEntity`持久化，支持保存/加载。

### 6.3 难度可配置

支持多种预设难度和自定义尺寸/地雷数。

---

## 七、文件清单

| 文件 | 路径 | 说明 |
|------|------|------|
| 核心方块 | `block/BlockMinesweeperCore.java` | 玩家交互入口 |
| 核心实体 | `block/BlockMinesweeperCoreEntity.java` | 游戏状态存储 |
| 显示方块 | `block/BlockMinesweeperDisplay.java` | 格子状态显示 |
| 显示实体 | `block/BlockMinesweeperDisplayEntity.java` | 状态存储 |
| 控制方块 | `block/BlockMinesweeperRefresh.java` | 重置/最小化/关闭 |
| 状态枚举 | `data/MinesweeperState.java` | 格子状态 |
| 难度枚举 | `data/PresetDifficulty.java` | 难度配置 |
| 游戏逻辑 | `logic/GameMinesweeperLogic.java` | 纯扫雷算法 |
| 辅助方法 | `logic/GameMinesweeperHelper.java` | Minecraft交互 |
| 奖励系统 | `logic/GameMinesweeperReward.java` | 通关奖励 |

---

## 八、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2026-05 | 初始版本，完成核心玩法 |
| v1.1 | 2026-05 | 添加自定义难度和奖励系统 |

*文档版本: 1.1*  
*最后更新: 2026-05-27*