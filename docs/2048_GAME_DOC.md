# 2048 游戏说明

2048 使用旋转布局。核心方块放在布局左下角，玩家点击核心方块表面的四个方向区域来移动棋盘。

## 模块结构

```text
common/simple2048/
├── block/
│   ├── Block2048Core.java
│   ├── Block2048CoreEntity.java
│   ├── Block2048Display.java
│   └── Block2048DisplayEntity.java
├── data/
│   ├── Quadrant.java
│   └── Value2048.java
├── logic/
│   ├── Game2048Logic.java
│   ├── Game2048Helper.java
│   └── Game2048Reward.java
├── renderer/
│   ├── Block2048CoreEntityRenderer.java
│   └── Block2048DisplayEntityRenderer.java
└── simple2048Registration.java
```

## 关键类

| 类 | 作用 |
| --- | --- |
| `Block2048Core` | 玩家交互入口，判断点击方向，处理展开和移动 |
| `Block2048CoreEntity` | 保存总分和最大数字 |
| `Block2048DisplayEntity` | 保存单个格子的数字 |
| `Quadrant` | 表示上下左右移动方向 |
| `Value2048` | 数字到贴图状态的映射 |
| `Game2048Logic` | 纯 2048 合并算法 |
| `Game2048Helper` | 生成布局，读写 4x4 显示网格 |
| `Game2048Reward` | 按分数和最大数字发放奖励 |

## 布局

展开后生成 6x6 区域：

```text
Frame   Frame   Frame   Frame   Frame   Frame
Frame   Display Display Display Display Frame
Frame   Display Display Display Display Frame
Frame   Display Display Display Display Frame
Frame   Display Display Display Display Frame
Core    Frame   Frame   Frame   Frame   Refresh
```

显示区是 4x4。核心方块和刷新方块占据外圈。

## 流程

1. 玩家点击核心方块中心区域。
2. `unfoldGame` 检查空间并生成布局。
3. `Game2048Logic.initGrid` 创建初始棋盘。
4. 玩家点击核心方块方向区域。
5. `Game2048Logic.processMove` 合并数字并生成新数字。
6. `Game2048Helper.writeDisplayGrid` 更新显示方块。
7. `Game2048Reward` 检查分数和最大数字奖励。

## 规则逻辑

`Game2048Logic` 不依赖 Minecraft。它负责：

- 初始化 4x4 棋盘
- 过滤空格
- 合并相同数字
- 随机生成 2 或 4
- 判断是否还能移动

## 奖励

奖励配置来自 `SimpleBlockGameConfig.GAME_2048_CONFIG`：

- 分数跨过阈值时发放分数奖励
- 最大数字跨过 1024、2048、4096 等阈值时发放数字奖励

## 扩展注意

- 修改棋盘大小时，需要同步修改显示布局、贴图和 `GRID_SIZE`。
- 新增数字贴图时，需要扩展 `Value2048` 和资源文件。
- 方向点击逻辑在 `Block2048Core#getQuadrant`。