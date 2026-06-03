# 记忆键游戏说明

记忆键使用 3x3 垂直布局。核心方块位于中心，8 个按钮围绕核心方块。玩家先观看闪烁序列，再按顺序点击按钮。

## 模块结构

```text
common/simpleMemoryKey/
├── block/
│   ├── BlockMemoryKeyCore.java
│   ├── BlockMemoryKeyCoreEntity.java
│   ├── BlockMemoryKeyButton.java
│   └── BlockMemoryKeyButtonEntity.java
├── data/
│   ├── MemoryKeyGameState.java
│   ├── MemoryKeyLevel.java
│   └── MemoryKeyPosition.java
├── logic/
│   ├── GameMemoryKeyLogic.java
│   ├── GameMemoryKeyHelper.java
│   └── GameMemoryKeyReward.java
├── renderer/
│   ├── BlockMemoryKeyCoreEntityRenderer.java
│   └── BlockMemoryKeyButtonEntityRenderer.java
└── simpleMemoryKeyRegistration.java
```

## 关键类

| 类 | 作用 |
| --- | --- |
| `BlockMemoryKeyCore` | 展开、开始、重置游戏 |
| `BlockMemoryKeyCoreEntity` | 保存状态、关卡、生命、序列和 tick 计时 |
| `BlockMemoryKeyButton` | 玩家输入按钮 |
| `BlockMemoryKeyButtonEntity` | 控制按钮闪烁状态 |
| `MemoryKeyGameState` | 空闲、演示、输入、错误、成功、结束等状态 |
| `MemoryKeyLevel` | 每关序列长度和闪烁速度 |
| `MemoryKeyPosition` | 8 个按钮的位置和编号 |
| `GameMemoryKeyLogic` | 生成序列和关卡长度 |
| `GameMemoryKeyHelper` | 布局生成、按钮查找、输入转发 |
| `GameMemoryKeyReward` | 关卡奖励 |

## 状态流程

```text
IDLE
  -> DEMONSTRATING
  -> PLAYER_INPUT
  -> LEVEL_SUCCESS -> 下一关或 ALL_SUCCESS
  -> ERROR -> 重新演示
  -> GAME_OVER
```

## 布局

```text
Button  Button  Button
Button  Core    Button
Button  Button  Button
        Refresh
```

## 流程

1. 展开后生成 8 个按钮和控制方块。
2. 开始游戏时生成完整随机序列。
3. 核心实体通过 tick 逐个触发按钮闪烁。
4. 演示结束后进入玩家输入状态。
5. 输入正确则继续，完成当前关后进入下一关。
6. 输入错误扣生命，生命耗尽则游戏结束。

## 扩展注意

- 改按钮数量时，需要同步修改 `MemoryKeyPosition` 和布局生成。
- 改关卡节奏时，优先修改 `MemoryKeyLevel`。
- 延迟逻辑集中在核心实体和按钮实体的 tick 中。