# 扫雷游戏说明

扫雷使用垂直布局。核心方块负责难度选择、展开和开始游戏，显示方块表示每个格子的状态。

## 模块结构

```text
common/simpleMinesweeper/
├── block/
│   ├── BlockMinesweeperCore.java
│   ├── BlockMinesweeperCoreEntity.java
│   ├── BlockMinesweeperDisplay.java
│   └── BlockMinesweeperDisplayEntity.java
├── data/
│   ├── MinesweeperState.java
│   └── PresetDifficulty.java
├── logic/
│   ├── GameMinesweeperLogic.java
│   ├── GameMinesweeperHelper.java
│   └── GameMinesweeperReward.java
├── renderer/
│   ├── BlockMinesweeperCoreEntityRenderer.java
│   └── BlockMinesweeperDisplayEntityRenderer.java
└── simpleMinesweeperRegistration.java
```

## 关键类

| 类 | 作用 |
| --- | --- |
| `BlockMinesweeperCore` | 核心交互、难度调整、展开布局 |
| `BlockMinesweeperCoreEntity` | 保存雷区、显示状态、旗帜数和游戏状态 |
| `BlockMinesweeperDisplay` | 玩家点击的扫雷格子 |
| `BlockMinesweeperDisplayEntity` | 保存单格显示状态 |
| `MinesweeperState` | 未打开、旗帜、数字、炸弹等格子状态 |
| `PresetDifficulty` | 预设宽高和雷数 |
| `GameMinesweeperLogic` | 生成雷区、翻开、插旗、胜利判断 |
| `GameMinesweeperHelper` | 生成布局并同步显示格子 |
| `GameMinesweeperReward` | 胜利奖励 |

## 难度

| 难度 | 尺寸 | 雷数 |
| --- | --- | --- |
| Easy | 9x9 | 10 |
| Normal | 16x16 | 40 |
| Hard | 16x30 | 99 |
| Expert | 30x64 | 450 |
| Custom | 自定义 | 自定义 |

## 流程

1. 玩家在核心方块上选择难度。
2. 点击中心区域展开布局。
3. 第一次翻开格子时生成雷区，保护点击点附近区域。
4. 左键翻开，右键标记旗帜。
5. 空白格子通过队列连锁翻开。
6. 胜利后按配置发放奖励。

## 规则逻辑

`GameMinesweeperLogic` 负责：

- `generateMineGrid`：生成雷区并避开首次点击安全区。
- `processFlip`：处理翻开格子。
- `processFlag`：处理旗帜切换。
- `isWin`：判断非雷格是否全部打开、雷是否全部标记。

## 扩展注意

- 新状态需要同时更新 `MinesweeperState`、显示实体和渲染器贴图。
- 调整最大尺寸时，要关注布局生成数量和客户端渲染压力。
- 自定义难度应保证雷数小于可放置格子数。