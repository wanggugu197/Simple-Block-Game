# 数独游戏说明

数独使用垂直布局的 9x9 网格。核心方块负责难度选择和游戏控制，玩家在显示方块上填入数字，完成标准数独规则。

## 模块结构

```text
common/simpleSudoku/
├── block/
│   ├── BlockSudokuCore.java
│   ├── BlockSudokuCoreEntity.java
│   ├── BlockSudokuDisplay.java
│   └── BlockSudokuDisplayEntity.java
├── data/
│   ├── Difficulty.java
│   └── SudokuGameState.java
├── logic/
│   ├── GameSudokuLogic.java
│   ├── GameSudokuHelper.java
│   └── GameSudokuReward.java
└── renderer/
    ├── BlockSudokuCoreEntityRenderer.java
    └── BlockSudokuDisplayEntityRenderer.java
```

## 关键类

| 类 | 作用 |
| --- | --- |
| `BlockSudokuCore` | 核心交互、难度调整、展开布局 |
| `BlockSudokuCoreEntity` | 保存棋盘数据、游戏状态、完成数 |
| `BlockSudokuDisplay` | 玩家点击的数独格子 |
| `BlockSudokuDisplayEntity` | 保存单格数字和状态 |
| `Difficulty` | 难度等级（简单、中等、困难） |
| `SudokuGameState` | 游戏状态（空闲、进行中、完成） |
| `GameSudokuLogic` | 生成数独棋盘、验证填入、判断完成 |
| `GameSudokuHelper` | 布局生成、显示同步、输入处理 |
| `GameSudokuReward` | 完成奖励 |

## 难度

| 难度 | 空格数 | 描述 |
| --- | --- | --- |
| Easy | 30-35 | 适合新手 |
| Normal | 40-45 | 中等难度 |
| Hard | 50-55 | 适合进阶玩家 |

## 流程

1. 玩家在核心方块上选择难度。
2. 点击中心区域展开 9x9 布局。
3. `GameSudokuLogic.generatePuzzle` 生成数独谜题。
4. 玩家点击显示方块填入数字。
5. `GameSudokuLogic.validateMove` 验证填入是否有效。
6. 完成全部格子后判断是否符合数独规则。
7. 完成后按配置发放奖励。

## 规则逻辑

`GameSudokuLogic` 负责：

- `generatePuzzle`：生成有效数独谜题，保证唯一解。
- `validateMove`：验证数字是否符合行、列、宫格规则。
- `isComplete`：判断是否全部填满且符合规则。
- `solvePuzzle`：求解数独（用于生成谜题验证）。

## 扩展注意

- 新难度需要同步修改 `Difficulty` 枚举和谜题生成参数。
- 格子状态需要更新显示实体和渲染器贴图。
- 数独生成算法复杂度较高，需关注性能优化。