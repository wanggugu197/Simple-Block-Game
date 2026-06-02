# Simple Block Game 项目说明

Simple Block Game 是一个 NeoForge 模组，把多个小游戏做成 Minecraft 方块玩法。当前包含 2048、扫雷、记忆键、十滴水和数独。

## 项目结构

```text
src/main/java/com/simple_block_game/
├── SimpleBlockGame.java              # Mod 入口
├── SimpleBlockGameConfig.java        # 配置项
├── common/
│   ├── base/                         # 通用方块、实体、渲染、奖励基类
│   ├── simple2048/                   # 2048
│   ├── simpleMinesweeper/            # 扫雷
│   ├── simpleMemoryKey/              # 记忆键
│   ├── simpleTenDrops/               # 十滴水
│   ├── simpleSudoku/                 # 数独
│   ├── CommonInit.java               # 公共初始化
│   ├── SimpleBlockGameRegistration.java
│   └── SimpleBlockGameRecipe.java
├── data/lang/                        # 语言生成
└── registry/                         # 注册系统封装
```

资源文件位于 `src/main/resources/assets/simple_block_game/`，包含模型、贴图和物品模型。

## 核心分层

每个游戏模块按相同结构组织：

```text
simpleXXX/
├── block/       # Minecraft 方块、方块实体、玩家交互
├── data/        # 状态、难度、方向、位置等枚举
├── logic/       # 游戏规则和世界布局辅助
└── renderer/    # 方块实体渲染
```

职责划分：

| 层 | 作用 |
| --- | --- |
| `block` | 玩家点击、方块状态、NBT 保存、网络同步 |
| `data` | 状态值、方向值、难度值 |
| `logic/GameXXXLogic` | 纯游戏规则，尽量不依赖 Minecraft |
| `logic/GameXXXHelper` | 布局生成、显示方块读写、世界清理 |
| `logic/GameXXXReward` | 根据分数、关卡或结果掉落奖励 |
| `renderer` | 按实体状态选择贴图 |

## 通用基础类

| 类 | 作用 |
| --- | --- |
| `IGameCoreBlock` | 定义展开、开始、重置、最小化、关闭等生命周期 |
| `BaseGameBlockEntity` | 提供方块实体同步和方向读取 |
| `BaseRotatedBlock` | 支持水平朝向，适合 2048 |
| `BaseVerticalBlock` | 垂直布局基类，适合扫雷、记忆键、十滴水、数独 |
| `BaseRotatedRefreshBlock` | 旋转布局控制方块 |
| `BaseVerticalRefreshBlock` | 垂直布局控制方块 |
| `BlockRefreshEntity` | 保存刷新方块关联的核心方块位置 |
| `BaseGameReward` | 封装战利品表掉落 |

## 游戏生命周期

核心方块实现 `IGameCoreBlock`：

1. `checkLayoutAreaIsEmpty`：检查布局区域是否可展开。
2. `unfoldGame`：生成显示方块、边框和控制方块。
3. `startGame`：初始化或开始当前局。
4. `resetGame`：重置游戏数据和显示。
5. `minimizeGame`：隐藏布局，保留核心方块。
6. `closeGame`：移除布局和核心方块，通常掉落核心方块物品。

刷新控制方块通常有三个区域：

| 区域 | 操作 |
| --- | --- |
| 左上 | 最小化 |
| 右上 | 关闭 |
| 下方 | 重置 |

## 注册入口

主要注册集中在 `SimpleBlockGameRegistration.java`：

- 物品和创造模式标签页
- 核心方块、显示方块、按键方块
- 方块实体
- 渲染器
- 通用框架方块和刷新方块

配方集中在 `SimpleBlockGameRecipe.java`。

## 配置

`SimpleBlockGameConfig.java` 提供：

- 各游戏启用开关
- 2048 分数和最大数字奖励
- 扫雷奖励阈值
- 记忆键关卡奖励
- 十滴水关卡奖励
- 数独难度和奖励

## 文档索引

- `2048_GAME_DOC.md`：2048 模块说明
- `MINESWEEPER_GAME_DOC.md`：扫雷模块说明
- `MEMORY_KEY_GAME_DOC.md`：记忆键模块说明
- `TEN_DROPS_GAME_DOC.md`：十滴水模块说明
- `SUDOKU_GAME_DOC.md`：数独模块说明
- `TWENTY_FOUR_PUZZLE_GAME_DOC.md`：24点模块说明
- `ADDING_GAME_DOC.md`：添加新游戏指南