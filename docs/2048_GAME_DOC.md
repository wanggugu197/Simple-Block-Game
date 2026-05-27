# 🎮 2048游戏代码文档

## 一、项目架构

### 1.1 模块结构

```
src/main/java/com/simple_block_game/common/simple2048/
├── block/                    # 方块类
│   ├── Block2048Core.java          # 核心方块（玩家交互入口）
│   ├── Block2048CoreEntity.java    # 核心方块实体（存储分数/最大值）
│   ├── Block2048Display.java       # 显示方块（显示数字）
│   ├── Block2048DisplayEntity.java # 显示方块实体
│   └── Block2048Refresh.java       # 刷新控制方块
├── data/                     # 数据类
│   ├── Quadrant.java               # 四象限方向枚举（上下左右）
│   └── Value2048.java              # 数字值枚举
├── logic/                    # 逻辑类
│   ├── Game2048Logic.java          # 游戏核心逻辑
│   ├── Game2048Helper.java         # Minecraft交互辅助方法
│   └── Game2048Reward.java         # 奖励系统
└── renderer/                 # 渲染器
    ├── Block2048CoreEntityRenderer.java      # 核心实体渲染器
    └── Block2048DisplayEntityRenderer.java   # 显示实体渲染器
```

---

## 二、核心组件说明

### 2.1 方块类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `Block2048Core` | 游戏核心方块，处理玩家点击输入 | `useWithoutItem()`, `unfoldGame()`, `startGame()` |
| `Block2048CoreEntity` | 核心方块实体，存储分数和最大值 | `getScore()`, `addScore()`, `getMaxNumber()` |
| `Block2048Display` | 显示方块，显示2048数字 | `getDisplayValue()`, `setDisplayValue()` |
| `Block2048DisplayEntity` | 显示方块实体，存储当前值 | `getValue()`, `setValue()` |
| `Block2048Refresh` | 刷新控制方块，提供重置/最小化/关闭功能 | `useWithoutItem()` |

### 2.2 数据类

#### 四象限方向枚举 (`Quadrant`)

| 方向 | 说明 | 旋转次数 |
|------|------|---------|
| `NULL` | 无效方向 | 0 |
| `UP` | 向上滑动 | 1（左转1次） |
| `DOWN` | 向下滑动 | 3（左转3次，等价于右转1次） |
| `LEFT` | 向左滑动 | 0（无需旋转） |
| `RIGHT` | 向右滑动 | 2（左转2次，等价于180度旋转） |

**关键方法**：
- `getRotationCount()`: 返回将该方向转换为向左滑动所需的90度左旋转次数

### 2.3 逻辑类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `Game2048Logic` | 纯游戏逻辑（无Minecraft依赖） | `processMove()`, `initGrid()`, `isGameOver()` |
| `Game2048Helper` | Minecraft世界交互方法 | `generate2048Layout()`, `readDisplayGrid()`, `writeDisplayGrid()` |
| `Game2048Reward` | 分数和数字奖励系统 | `handleScoreReward()`, `handleMaxNumberReward()` |

### 2.4 渲染器类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `Block2048CoreEntityRenderer` | 核心实体渲染器，显示分数和最大值 | `getTextureForState()` |
| `Block2048DisplayEntityRenderer` | 显示实体渲染器，显示2048数字纹理 | `getTexturesForState()` |

---

## 三、游戏流程

### 3.1 布局结构

游戏展开后形成 6×6 的布局：

```
┌──────────────────────┐
│  Frame  Frame  Frame │
│  Frame Display Frame │
│  Frame Display Frame │
│  Frame Display Frame │
│  Frame Display Frame │
│  Frame  Frame Refresh│
└──────────────────────┘
    ↑
  Core (位置0,0)
```

核心方块位于左下角(0,0)，显示区域为 4×4，控制方块位于右上角(5,5)。

### 3.2 玩家交互

玩家通过点击核心方块的四个象限区域来控制滑动方向：

```
        ┌───────────────────┐
        │      UP           │
        ├───────────────────┤
        │ LEFT  │  CENTER   │  RIGHT  │
        │ (3×3) │  (8×8)    │  (3×3)  │
        │       │  展开/重置 │         │
        ├───────────────────┤
        │     DOWN          │
        └───────────────────┘
```

- **中心8×8区域**：未展开时展开游戏，已展开时无操作
- **左上3×3区域**：向左滑动
- **右上3×3区域**：向右滑动
- **左下3×3区域**：向下滑动
- **右下3×3区域**：向上滑动

### 3.3 核心方法调用链

**展开游戏**：
```
unfoldGame() → checkLayoutAreaIsEmpty() → generate2048Layout() → initGrid() → writeDisplayGrid()
```

**玩家移动**：
```
useWithoutItem() → getQuadrant() → handleGameMove() → processMove() → writeDisplayGrid() → handleScoreReward()
```

**重置游戏**：
```
resetGame() → reset2048Layout() → initGrid() → writeDisplayGrid() → reset()
```

---

## 四、关键实现细节

### 4.1 方向判断算法

通过UV坐标判断点击象限：

```java
private Quadrant getQuadrant(BlockState state, BlockHitResult hit) {
    Vec3 uv = getUV(state.getValue(FACING), hit.getLocation(), hit.getBlockPos());
    boolean subDiag = uv.y < (16 - uv.x);  // 副对角线判断
    boolean mainDiag = uv.y < uv.x;        // 主对角线判断
    // 根据对角线划分四个象限
    return subDiag ? (mainDiag ? DOWN : LEFT) : (mainDiag ? RIGHT : UP);
}
```

### 4.2 游戏核心逻辑

`Game2048Logic.processMove()` 处理移动的核心流程：

1. 根据方向旋转/转置网格
2. 向左滑动数字
3. 合并相邻相同数字
4. 再次向左滑动填充空位
5. 添加随机数字（2或4）
6. 检查游戏是否结束

### 4.3 网格读写机制

通过显示方块实体存储每个格子的值：

```java
// 读取网格
int[][] grid = new int[4][4];
for (int row = 0; row < 4; row++) {
    for (int col = 0; col < 4; col++) {
        grid[row][col] = Block2048Display.getDisplayValue(level, pos);
    }
}

// 写入网格
Block2048Display.setDisplayValue(level, pos, grid[row][col]);
```

---

## 五、设计模式

### 5.1 纯逻辑与交互分离

游戏核心逻辑(`Game2048Logic`)不依赖Minecraft，可以独立测试。

### 5.2 实体存储状态

游戏状态通过`BlockEntity`持久化，支持保存/加载。

### 5.3 方向适配

支持四个朝向(NORTH/EAST/SOUTH/WEST)，通过坐标变换适配不同方向。

---

## 六、文件清单

| 文件 | 路径 | 说明 |
|------|------|------|
| 核心方块 | `block/Block2048Core.java` | 玩家交互入口 |
| 核心实体 | `block/Block2048CoreEntity.java` | 分数/最大值存储 |
| 显示方块 | `block/Block2048Display.java` | 数字显示 |
| 显示实体 | `block/Block2048DisplayEntity.java` | 显示值存储 |
| 控制方块 | `block/Block2048Refresh.java` | 重置/最小化/关闭 |
| 方向枚举 | `data/Quadrant.java` | 滑动方向 |
| 数字枚举 | `data/Value2048.java` | 显示值映射 |
| 游戏逻辑 | `logic/Game2048Logic.java` | 纯2048算法 |
| 辅助方法 | `logic/Game2048Helper.java` | Minecraft交互 |
| 奖励系统 | `logic/Game2048Reward.java` | 分数奖励 |
| 核心渲染器 | `renderer/Block2048CoreEntityRenderer.java` | 核心实体渲染 |
| 显示渲染器 | `renderer/Block2048DisplayEntityRenderer.java` | 显示实体渲染 |

---

## 七、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2026-05 | 初始版本，完成核心玩法 |
| v1.1 | 2026-05 | 添加奖励系统 |
| v1.2 | 2026-05 | 添加自定义渲染器 |

*文档版本: 1.2*  
*最后更新: 2026-05-29*