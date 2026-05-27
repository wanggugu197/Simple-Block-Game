# 🎯 记忆键游戏代码文档

## 一、项目架构

### 1.1 模块结构

```
src/main/java/com/simple_block_game/common/simpleMemoryKey/
├── block/                    # 方块类
│   ├── BlockMemoryKeyCore.java          # 核心方块（游戏状态管理）
│   ├── BlockMemoryKeyCoreEntity.java    # 核心方块实体（存储游戏状态）
│   ├── BlockMemoryKeyButton.java        # 按键方块（玩家输入）
│   ├── BlockMemoryKeyButtonEntity.java  # 按键方块实体（闪烁控制）
│   └── BlockMemoryKeyRefresh.java       # 刷新控制方块
├── data/                     # 数据类
│   ├── MemoryKeyGameState.java          # 游戏状态枚举
│   ├── MemoryKeyLevel.java              # 关卡枚举
│   └── MemoryKeyPosition.java           # 按键位置枚举
├── logic/                    # 逻辑类
│   ├── GameMemoryKeyLogic.java          # 游戏核心逻辑
│   ├── GameMemoryKeyHelper.java         # Minecraft交互辅助方法
│   └── GameMemoryKeyReward.java         # 奖励系统
└── renderer/                 # 渲染器
    ├── BlockMemoryKeyCoreEntityRenderer.java      # 核心实体渲染器
    └── BlockMemoryKeyButtonEntityRenderer.java    # 按键实体渲染器
```

---

## 二、核心组件说明

### 2.1 方块类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `BlockMemoryKeyCore` | 游戏核心方块，管理游戏状态和交互 | `unfoldGame()`, `startGame()`, `resetGame()` |
| `BlockMemoryKeyCoreEntity` | 核心方块实体，存储游戏状态和tick更新 | `tick()`, `completeReset()`, `startDemonstration()` |
| `BlockMemoryKeyButton` | 按键方块，处理玩家点击输入 | `useWithoutItem()` |
| `BlockMemoryKeyButtonEntity` | 按键实体，管理闪烁状态 | `startFlashing()`, `tick()` |
| `BlockMemoryKeyRefresh` | 刷新控制方块，提供重置/最小化/关闭功能 | `useWithoutItem()` |

### 2.2 数据类

#### 游戏状态枚举 (`MemoryKeyGameState`)

| 状态 | 说明 | 触发条件 |
|------|------|---------|
| `IDLE` | 空闲状态，等待玩家开始 | 游戏初始化/重置后 |
| `DEMONSTRATING` | 系统演示阶段 | 开始游戏/错误恢复后 |
| `PLAYER_INPUT` | 玩家输入阶段 | 演示完成后 |
| `ERROR` | 输入错误状态（持续2秒） | 玩家输入错误 |
| `LEVEL_SUCCESS` | 关卡成功状态（持续1秒） | 完成当前关卡 |
| `ALL_SUCCESS` | 全部通关状态 | 完成所有6关 |
| `GAME_OVER` | 游戏结束状态 | 生命耗尽 |

#### 关卡枚举 (`MemoryKeyLevel`)

| 关卡 | 序列长度 | 闪烁时长(ms) |
|------|---------|-------------|
| `LEVEL_1` | 3 | 1000 |
| `LEVEL_2` | 6 | 900 |
| `LEVEL_3` | 9 | 800 |
| `LEVEL_4` | 12 | 700 |
| `LEVEL_5` | 15 | 600 |
| `LEVEL_6` | 18 | 500 |

#### 按键位置枚举 (`MemoryKeyPosition`)

| 位置 | 相对坐标 | ID |
|------|---------|-----|
| `NORTH_WEST` | (-1, 0, -1) | 0 |
| `NORTH` | (0, 0, -1) | 1 |
| `NORTH_EAST` | (1, 0, -1) | 2 |
| `WEST` | (-1, 0, 0) | 3 |
| `EAST` | (1, 0, 0) | 4 |
| `SOUTH_WEST` | (-1, 0, 1) | 5 |
| `SOUTH` | (0, 0, 1) | 6 |
| `SOUTH_EAST` | (1, 0, 1) | 7 |

### 2.3 逻辑类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `GameMemoryKeyLogic` | 游戏核心逻辑 | `generateFullSequence()`, `getSequenceLengthForLevel()`, `validateInput()` |
| `GameMemoryKeyHelper` | Minecraft交互辅助方法 | `generateMemoryKeyLayout()`, `minimizeMemoryKeyLayout()`, `destroyMemoryKeyLayout()`, `triggerButtonFlash()`, `handlePlayerInput()` |
| `GameMemoryKeyReward` | 奖励系统 | `handleReward()` |

### 2.4 渲染器类

| 类名 | 职责 | 关键方法 |
|------|------|---------|
| `BlockMemoryKeyCoreEntityRenderer` | 核心实体渲染器，显示关卡和生命值 | `getTextureForState()` |
| `BlockMemoryKeyButtonEntityRenderer` | 按键实体渲染器，显示按键状态和闪烁效果 | `getTexturesForState()` |

---

## 三、游戏流程

### 3.1 状态机流转

```
[IDLE] ──点击开始──→ [DEMONSTRATING] ──演示完成──→ [PLAYER_INPUT]
                                                    │        │
                                           正确输入 │        │ 错误输入
                                                   ▼        ▼
                                      [LEVEL_SUCCESS]  [ERROR]
                                       (1秒后)        (2秒后)
                                           │        │
                                    最后一关? │        │
                                       ├─是→ [ALL_SUCCESS]
                                       └─否─┘        │
                                              [DEMONSTRATING] ←───┘
                                                    │
                                              生命=0│
                                                    ▼
                                               [GAME_OVER]
```

### 3.2 布局结构

游戏展开后形成 3×3 的布局：

```
┌─────────────────────┐
│ NW  │ North │ NE    │
│ [0] │  [1]  │ [2]   │
├─────────────────────┤
│ West│ Core  │ East  │
│ [3] │       │ [4]   │
├─────────────────────┤
│ SW  │ South │ SE    │
│ [5] │  [6]  │ [7]   │
└─────────────────────┘
         ↑
      Refresh
```

核心方块位于中心，8个按键环绕四周，控制方块位于底部。

### 3.3 核心方法调用链

**展开游戏**：
```
unfoldGame() → generateMemoryKeyLayout() → initializeGame() → [IDLE]
```

**开始游戏**：
```
startGame() → generateFullSequence() → startDemonstration() → [DEMONSTRATING]
```

**玩家输入处理**：
```
handleButtonClick() → handlePlayerInput() → handleCorrectInput()/handleWrongInput()
```

**关闭游戏**：
```
closeGame() → destroyMemoryKeyLayout() → minimizeMemoryKeyLayout() + 掉落核心方块
```

---

## 四、关键实现细节

### 4.1 实体Tick驱动设计

游戏核心逻辑通过 `BlockMemoryKeyCoreEntity.tick()` 方法实现：

```java
public void tick() {
    // 错误状态处理（2秒延迟）
    if (gameState == MemoryKeyGameState.ERROR) {
        errorTickCounter++;
        if (errorTickCounter >= 40) {
            startDemonstration();
            setGameState(MemoryKeyGameState.DEMONSTRATING);
        }
        return;
    }
    
    // 关卡成功处理（1秒延迟）
    if (gameState == MemoryKeyGameState.LEVEL_SUCCESS) {
        successTickCounter++;
        if (successTickCounter >= 20) {
            if (isAllLevelsComplete()) {
                setGameState(MemoryKeyGameState.ALL_SUCCESS);
            } else {
                nextLevel();
                startDemonstration();
            }
        }
        return;
    }
    
    // 演示阶段处理
    if (gameState == MemoryKeyGameState.DEMONSTRATING) {
        // 按键闪烁逻辑
    }
}
```

### 4.2 按键闪烁机制

按键闪烁由 `BlockMemoryKeyButtonEntity.tick()` 独立处理：

```java
public void tick() {
    if (isFlashing()) {
        flashTickCounter++;
        if (flashTickCounter >= FLASH_DURATION_TICKS) {
            setFlashing(false);
        }
    }
}
```

### 4.3 方块状态同步

游戏状态和生命值通过 `updateBlockState()` 同步到方块属性：

```java
private void updateBlockState() {
    BlockState newState = state
            .setValue(BlockMemoryKeyCore.GAME_STATE, gameState)
            .setValue(BlockMemoryKeyCore.LIVES, remainingLives);
    level.setBlock(worldPosition, newState, 3);
    level.sendBlockUpdated(worldPosition, state, newState, 3);
}
```

---

## 五、设计模式

### 5.1 状态机模式

使用枚举管理游戏状态，通过状态转换驱动游戏流程。

### 5.2 实体Tick驱动

避免使用外部调度器，通过实体的 `tick()` 方法处理延迟逻辑。

### 5.3 模块化分离

- **逻辑层**：纯业务逻辑，无Minecraft依赖
- **交互层**：与Minecraft世界交互
- **数据层**：状态和配置数据
- **渲染层**：自定义渲染逻辑

---

## 六、扩展建议

### 6.1 可扩展功能

| 功能 | 说明 |
|------|------|
| 音效系统 | 添加按键音效和背景音乐 |
| 动画效果 | 添加状态切换粒子效果 |
| 难度配置 | 允许玩家选择难度等级 |
| 多人模式 | 支持多人协作或竞争玩法 |

### 6.2 代码优化

1. **配置外置**：将游戏参数（序列长度、延迟时间）外置为配置文件
2. **事件系统**：使用事件总线解耦游戏逻辑
3. **测试覆盖**：添加单元测试和集成测试

---

## 七、文件清单

| 文件 | 路径 | 说明 |
|------|------|------|
| 核心方块 | `block/BlockMemoryKeyCore.java` | 游戏核心方块 |
| 核心实体 | `block/BlockMemoryKeyCoreEntity.java` | 存储游戏状态和tick更新 |
| 按键方块 | `block/BlockMemoryKeyButton.java` | 玩家输入按键 |
| 按键实体 | `block/BlockMemoryKeyButtonEntity.java` | 按键闪烁控制 |
| 刷新方块 | `block/BlockMemoryKeyRefresh.java` | 重置/最小化/关闭控制 |
| 游戏状态 | `data/MemoryKeyGameState.java` | 游戏状态枚举 |
| 关卡枚举 | `data/MemoryKeyLevel.java` | 关卡配置 |
| 位置枚举 | `data/MemoryKeyPosition.java` | 按键位置配置 |
| 游戏逻辑 | `logic/GameMemoryKeyLogic.java` | 核心游戏逻辑 |
| 辅助方法 | `logic/GameMemoryKeyHelper.java` | Minecraft交互方法 |
| 奖励系统 | `logic/GameMemoryKeyReward.java` | 战利品奖励 |
| 核心渲染器 | `renderer/BlockMemoryKeyCoreEntityRenderer.java` | 核心实体渲染 |
| 按键渲染器 | `renderer/BlockMemoryKeyButtonEntityRenderer.java` | 按键实体渲染 |

---

## 八、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2026-05 | 初始版本，完成核心玩法 |
| v1.1 | 2026-05 | 修复状态切换问题 |
| v1.2 | 2026-05 | 添加奖励系统和自定义渲染器 |

*文档版本: 1.2*  
*最后更新: 2026-05-29*