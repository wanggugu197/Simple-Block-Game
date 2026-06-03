# 十滴水游戏说明

十滴水使用垂直 6x6 网格。玩家点击水滴提升等级，达到临界等级后水滴爆开并向四个方向传播，引发连锁反应。

## 模块结构

```text
common/simpleTenDrops/
├── block/
│   ├── BlockTenDropsCore.java
│   ├── BlockTenDropsCoreEntity.java
│   ├── BlockTenDropsDisplay.java
│   └── BlockTenDropsDisplayEntity.java
├── data/
│   ├── DropletLevel.java
│   ├── TenDropsDirection.java
│   └── TenDropsGameState.java
├── logic/
│   ├── GameTenDropsLogic.java
│   ├── GameTenDropsHelper.java
│   └── GameTenDropsReward.java
├── renderer/
│   ├── BlockTenDropsCoreEntityRenderer.java
│   └── BlockTenDropsDisplayEntityRenderer.java
└── simpleTenDropsRegistration.java
```

## 关键类

| 类 | 作用 |
| --- | --- |
| `BlockTenDropsCore` | 展开、开始、重置游戏 |
| `BlockTenDropsCoreEntity` | 保存关卡、水滴数量、状态和 tick 过程 |
| `BlockTenDropsDisplay` | 玩家点击的水滴格子 |
| `BlockTenDropsDisplayEntity` | 保存单个水滴等级和运动状态 |
| `DropletLevel` | 水滴等级 |
| `TenDropsDirection` | 水滴传播方向 |
| `TenDropsGameState` | 游戏状态 |
| `GameTenDropsLogic` | 初始棋盘生成、胜利判断、奖励计算 |
| `GameTenDropsHelper` | 布局生成、显示读写、输入处理 |
| `GameTenDropsReward` | 关卡奖励 |

## 流程

1. 展开后生成 6x6 显示网格和控制方块。
2. 开始游戏时按当前关卡生成初始水滴。
3. 玩家点击水滴，消耗一次操作次数。
4. 水滴达到临界等级后爆开。
5. 爆开的水滴向四个方向传播，命中其他水滴后可能继续连锁。
6. 清空全部水滴则胜利并进入下一关。

## 规则逻辑

`GameTenDropsLogic` 负责：

- `generateLevelGrid`：按关卡生成初始水滴。
- `hasPotentialChainReaction`：保证初始盘面有可玩连锁。
- `calculateReward`：根据消除数量和连击数计算奖励。
- `isVictory`：判断网格是否清空。

## 扩展注意

- 网格大小由 `GameTenDropsLogic.GRID_SIZE` 控制，改动时要同步布局和贴图。
- 关卡参数集中在 `GameTenDropsLogic#getLevelConfig`。
- 水滴动画和显示状态在显示实体和渲染器中处理。