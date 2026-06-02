# 24点游戏说明

24点使用旋转布局。核心方块放在布局左下角，玩家通过点击显示方块输入数字和运算符，组合出结果为24的表达式。

## 模块结构

```text
common/simple24Puzzle/
├── block/
│   ├── Block24PuzzleCore.java
│   ├── Block24PuzzleCoreEntity.java
│   ├── Block24PuzzleDisplay.java
│   └── Block24PuzzleDisplayEntity.java
├── data/
│   └── GameToken24Puzzle.java
├── logic/
│   ├── Game24PuzzleLogic.java
│   ├── Game24PuzzleHelper.java
│   └── Game24PuzzleReward.java
└── renderer/
    ├── Block24PuzzleCoreEntityRenderer.java
    └── Block24PuzzleDisplayEntityRenderer.java
```

## 关键类

| 类 | 作用 |
| --- | --- |
| `Block24PuzzleCore` | 玩家交互入口，判断点击区域，处理展开和验证 |
| `Block24PuzzleCoreEntity` | 保存当前谜题数字、输入表达式、连击计数 |
| `Block24PuzzleDisplayEntity` | 保存单个显示方块的Token |
| `GameToken24Puzzle` | 数字(1-13)、运算符(+-*/)和括号的枚举 |
| `Game24PuzzleLogic` | 纯24点算法，包括谜题生成、表达式验证、解法提示 |
| `Game24PuzzleHelper` | 生成布局，读写显示网格 |
| `Game24PuzzleReward` | 按连击次数和完成时间发放奖励 |

## 布局

展开后生成 8x5 区域：

```text
Frame   Frame   Frame   Frame   Frame   Frame   Frame   Frame
Frame   Op1     Op2     Op3     Op4     (       )       Frame
Frame   Empty   Empty   Empty   Empty   Empty   Empty   Frame
Frame   Num1    Num2    Num3    Num4    Empty   Empty   Frame
Core    Frame   Frame   Frame   Frame   Frame   Frame   Refresh
```

显示区是 6x3：
- 第一行：运算符 +、-、×、÷ 和括号 (、)
- 第二行：空（用于显示输入表达式）
- 第三行：题目数字（4个）

## 流程

1. 玩家点击核心方块中心区域。
2. `unfoldGame` 检查空间并生成布局。
3. `Game24PuzzleLogic.generateValidPuzzle` 生成有解的4个数字。
4. 玩家点击数字或运算符方块输入表达式。
5. 点击核心方块左上角清除输入，右上角撤销最后输入。
6. 玩家点击核心方块中心区域验证答案。
7. `Game24PuzzleLogic.verifyResult` 验证表达式是否正确。
8. `Game24PuzzleReward` 检查连击奖励。
9. 自动开始下一局。

## 规则逻辑

### 谜题生成

`Game24PuzzleLogic.generateValidPuzzle`：
- 从数字 1-13 中随机选择4个
- 使用 `hasSolution` 验证是否有解
- 确保生成的谜题一定可解

### 表达式验证

`Game24PuzzleLogic.verifyResult` 执行三步验证：
1. **语法检查**：括号配对、运算符连续性
2. **数字检查**：使用的数字必须与题目完全一致（可重复）
3. **计算检查**：表达式结果是否等于24

### 表达式计算

使用双栈算法（中缀转后缀）：
- 支持运算符优先级（×÷ 高于 +-）
- 支持括号改变运算顺序
- 左结合原则

## 奖励

奖励配置来自 `SimpleBlockGameConfig.GAME_24PUZZLE_CONFIG`：

| 配置项 | 说明 |
| --- | --- |
| `comboThreshold` | 触发时间奖励需要的连续成功次数 |
| `timeTier1` | 最快时间阈值（秒）|
| `timeTier2` | 第二快时间阈值 |
| `timeTier3` | 第三快时间阈值 |
| `timeTier4` | 第四快时间阈值 |

奖励机制：
- 连续答对 `comboThreshold` 次后获得时间奖励
- 完成时间越快，奖励越好
- 时间奖励等级：Tier1 > Tier2 > Tier3 > Tier4 > default
- 获得奖励后连击计数重置

## 核心方块点击区域

核心方块表面分为三个交互区域：

| 区域 | 位置 | 功能 |
| --- | --- | --- |
| TOP_LEFT | 左上角 | 清除当前输入 |
| TOP_RIGHT | 右上角 | 撤销最后一个输入 |
| CENTER | 中心 8x8 区域 | 验证答案并开始下一局 |

## 扩展注意

- 修改数字范围时，需要更新 `GameToken24Puzzle` 枚举和对应的贴图。
- 修改布局大小时，需要同步修改 `Game24PuzzleHelper` 中的常量。
- 添加新运算符需要修改表达式验证和计算逻辑。
- 点击区域判断在 `Block24PuzzleCore#getClickRegion`。