# Simple Block Game 项目架构文档

## 一、项目概述

Simple Block Game 是一个基于 **Minecraft NeoForge** 的迷你游戏集合模组。项目采用**模块化设计**，支持快速添加新的小游戏，目前包含四个游戏模块：2048、扫雷、记忆键和十滴水。

### 1.1 核心设计理念

- **模块化**：每个游戏独立封装，便于维护和扩展
- **分离原则**：纯游戏逻辑与 Minecraft 交互代码分离，支持独立测试
- **统一接口**：所有游戏遵循一致的生命周期管理模式
- **共享组件**：基础方块、刷新控制、渲染器等组件在各游戏间共享

### 1.2 项目结构

```
Simple-Block-Game/
├── src/main/java/com/simple_block_game/
│   ├── common/                    # 公共代码（核心）
│   │   ├── base/                  # 基础框架（复用组件）
│   │   │   ├── block/             # 基础方块类
│   │   │   │   ├── BaseGameBlockEntity.java     # 游戏方块实体基类
│   │   │   │   ├── BaseRotatedBlock.java        # 可旋转方块基类
│   │   │   │   ├── BaseVerticalBlock.java       # 垂直方块基类
│   │   │   │   ├── BaseRotatedRefreshBlock.java # 旋转刷新方块基类
│   │   │   │   ├── BaseVerticalRefreshBlock.java # 垂直刷新方块基类
│   │   │   │   ├── BlockRefreshEntity.java      # 刷新实体（共享）
│   │   │   │   ├── IGameCoreBlock.java          # 游戏核心接口
│   │   │   │   ├── RotatedFrame.java            # 旋转框架方块
│   │   │   │   └── VerticalFrame.java           # 垂直框架方块
│   │   │   ├── data/              # 共享数据类
│   │   │   │   ├── RotatedRefreshArea.java      # 旋转刷新区域枚举
│   │   │   │   └── VerticalRefreshArea.java     # 垂直刷新区域枚举
│   │   │   ├── renderer/          # 渲染器基类
│   │   │   │   ├── BaseGameBlockEntityRenderer.java      # 基础渲染器
│   │   │   │   ├── BaseGameBlockEntityCubeRenderer.java  # 立方体渲染器
│   │   │   │   ├── BaseGameBlockEntityRenderState.java   # 渲染状态
│   │   │   │   └── BlockRefreshEntityRenderer.java       # 刷新实体渲染器
│   │   │   └── reward/            # 奖励系统基类
│   │   │       └── BaseGameReward.java          # 奖励基类
│   │   ├── simple2048/            # 2048游戏模块
│   │   ├── simpleMinesweeper/     # 扫雷游戏模块
│   │   ├── simpleMemoryKey/       # 记忆键游戏模块
│   │   ├── simpleTenDrop/         # 十滴水游戏模块
│   │   ├── CommonInit.java        # 公共初始化
│   │   ├── SimpleBlockGameRecipe.java   # 配方注册
│   │   └── SimpleBlockGameRegistration.java # 方块/实体注册
│   ├── data/lang/                 # 语言处理
│   │   └── LangHandler.java       # 多语言处理
│   ├── registry/                  # 注册系统扩展
│   │   ├── GameRegistryCore.java  # 注册核心扩展
│   │   └── builder/               # 自定义注册构建器
│   │       ├── ModBlockBuilder.java
│   │       ├── ModEntityBuilder.java
│   │       ├── ModFluidBuilder.java
│   │       └── ModItemBuilder.java
│   ├── SimpleBlockGame.java        # 主类
│   └── SimpleBlockGameConfig.java  # 配置类
├── src/main/resources/            # 资源文件（模型、纹理、语言文件）
├── build.gradle                   # 构建配置
└── docs/                          # 文档目录
    ├── PROJECT_ARCHITECTURE.md    # 架构文档（本文件）
    ├── ADDING_GAME_DOC.md         # 添加游戏指南
    ├── MEMORY_KEY_GAME_DOC.md     # 记忆键游戏文档
    ├── MINESWEEPER_GAME_DOC.md    # 扫雷游戏文档
    ├── 2048_GAME_DOC.md           # 2048游戏文档
    └── TEN_DROP_GAME_DOC.md       # 十滴水游戏文档
```

---

## 二、核心框架

### 2.1 基础方块类层次结构

| 类名 | 父类 | 职责 | 方向属性 | 适用游戏 |
|------|------|------|---------|----------|
| `BaseGameBlockEntity` | `BlockEntity` | 游戏方块实体基类，处理方向获取和网络同步 | 无 | 所有游戏 |
| `BaseVerticalBlock` | `BaseEntityBlock` | 垂直固定方块基类 | 无 | 扫雷、记忆键、十滴水 |
| `BaseRotatedBlock` | `BaseEntityBlock` | 水平旋转方块基类 | FACING（水平四方向） | 2048 |
| `BaseVerticalRefreshBlock` | `BaseVerticalBlock` | 垂直刷新方块基类 | 无 | 扫雷、记忆键、十滴水 |
| `BaseRotatedRefreshBlock` | `BaseRotatedBlock` | 旋转刷新方块基类 | FACING | 2048 |

### 2.2 核心接口与实体

| 接口/类 | 职责 | 作用范围 |
|---------|------|----------|
| `IGameCoreBlock` | 游戏核心方块接口，定义生命周期管理方法 | 所有游戏核心方块 |
| `BlockRefreshEntity` | 刷新控制方块实体，存储关联的核心方块位置 | 所有游戏共享 |
| `BaseGameReward` | 奖励系统基类，提供战利品掉落功能 | 所有游戏奖励 |
| `BaseGameBlockEntityRenderer` | 基础渲染器基类，处理方向旋转和纹理渲染 | 所有游戏方块实体渲染 |
| `BaseGameBlockEntityCubeRenderer` | 立方体渲染器基类，支持六面纹理渲染 | 需要立方体渲染的方块 |

### 2.3 IGameCoreBlock 接口详解

所有游戏核心方块必须实现此接口，定义了游戏的完整生命周期：

```java
public interface IGameCoreBlock {
    BooleanProperty UNFOLDED = BooleanProperty.create("unfolded");
    
    boolean checkLayoutAreaIsEmpty(ServerLevel serverLevel, BlockPos pos, BlockState state);
    boolean unfoldGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);
    void startGame(ServerLevel serverLevel, BlockPos pos, BlockState state, Player player);
    void resetGame(ServerLevel serverLevel, BlockPos pos, BlockState state);
    void minimizeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);
    void closeGame(ServerLevel serverLevel, BlockPos pos, BlockState state);
    boolean isGameUnfolded(BlockState state);
    BlockEntity getGameCoreEntity(ServerLevel serverLevel, BlockPos pos);
}
```

### 2.4 刷新控制方块设计

刷新方块提供统一的游戏控制功能，三个交互区域：

| 控制区域 | 功能 | 数据保留 |
|---------|------|----------|
| 左上区域 | 最小化游戏（隐藏布局） | 保留 |
| 右上区域 | 关闭游戏（移除布局） | 清除 |
| 下方区域 | 重置游戏（重新开始） | 清除 |

---

## 三、游戏模块结构

每个游戏模块遵循统一的目录结构，便于快速定位和维护：

```
simpleXXX/                          # 游戏模块根目录
├── block/                          # 方块层（Minecraft交互）
│   ├── BlockXXXCore.java           # 核心方块（玩家交互入口）
│   ├── BlockXXXCoreEntity.java     # 核心方块实体（状态存储、tick更新）
│   ├── BlockXXXDisplay.java        # 显示方块（可选）
│   ├── BlockXXXDisplayEntity.java  # 显示方块实体（可选）
│   ├── BlockXXXButton.java         # 按键方块（记忆键专用，可选）
│   ├── BlockXXXButtonEntity.java   # 按键方块实体（可选）
│   └── BlockXXXRefresh.java        # 刷新控制方块
├── data/                           # 数据层（状态定义）
│   ├── XXXGameState.java           # 游戏状态枚举（状态机）
│   └── XXXConfig/Difficulty.java   # 配置/难度枚举
├── logic/                          # 逻辑层（核心逻辑）
│   ├── GameXXXLogic.java           # 纯游戏逻辑（无Minecraft依赖）
│   ├── GameXXXHelper.java          # Minecraft交互辅助方法
│   └── GameXXXReward.java          # 奖励系统
└── renderer/                       # 渲染层（可选）
    ├── BlockXXXCoreEntityRenderer.java      # 核心实体渲染器
    ├── BlockXXXDisplayEntityRenderer.java   # 显示实体渲染器
    └── BlockXXXButtonEntityRenderer.java    # 按键实体渲染器
```

### 3.1 各层职责说明

| 层级 | 职责 | 依赖限制 |
|------|------|----------|
| **block/** | 处理方块放置、交互、渲染、网络同步 | 依赖 Minecraft |
| **data/** | 定义游戏状态、配置常量 | 无依赖（纯Java） |
| **logic/** | 游戏核心逻辑（规则、判定、计算） | 无依赖（纯Java） |
| **renderer/** | 自定义渲染逻辑 | 依赖 Minecraft 渲染API |

---

## 四、现有游戏模块

### 4.1 模块概览

| 游戏 | 核心方块 | 交互方式 | 布局类型 | 难度等级 |
|------|----------|----------|----------|----------|
| **2048** | `Block2048Core` | 点击象限区域控制滑动方向 | 6×6 旋转布局 | 固定 |
| **扫雷** | `BlockMinesweeperCore` | 左键翻开、右键标记旗帜 | 可变尺寸垂直布局 | 3种 |
| **记忆键** | `BlockMemoryKeyCore` | 观看演示序列后点击输入 | 3×3 垂直布局 | 6种 |
| **十滴水** | `BlockTenDropCore` | 点击水滴分裂 | 可变尺寸垂直布局 | 固定 |

### 4.2 各游戏核心特性

#### 2048 游戏
- **布局**：6×6（4×4 显示区域，外围控制区域）
- **交互**：点击四个象限控制上下左右滑动
- **核心逻辑**：数字合并、随机生成、游戏结束判定
- **旋转特性**：支持四个方向放置

#### 扫雷游戏
- **布局**：可变尺寸（9×9 ~ 30×64）
- **交互**：左键翻开方块、右键标记/取消旗帜
- **核心特性**：首次点击保护、BFS连锁翻开、旗帜计数
- **难度配置**：简单(9×9,10雷)、中等(16×16,40雷)、困难(30×16,99雷)

#### 记忆键游戏
- **布局**：3×3（中心核心方块，8方向按键）
- **交互**：观看序列演示后按顺序点击按键
- **核心特性**：6个难度关卡、3条生命容错、序列随机生成
- **关卡递增**：序列长度从3递增到18

#### 十滴水游戏
- **布局**：可变尺寸水滴网格
- **交互**：点击水滴使其分裂
- **核心特性**：水滴合并、连锁反应、边界溢出
- **难度**：固定布局随机生成

---

## 五、设计模式与架构原则

### 5.1 设计模式

| 模式 | 应用场景 | 说明 |
|------|----------|------|
| **状态机模式** | 游戏状态管理 | 使用枚举管理游戏状态流转（IDLE→PLAYING→SUCCESS→GAME_OVER） |
| **实体Tick驱动** | 延迟逻辑处理 | 通过 `BlockEntity.tick()` 处理定时任务（如序列演示、错误延迟） |
| **策略模式** | 方向旋转（2048） | 根据玩家朝向动态调整旋转次数 |
| **模板方法模式** | 刷新方块基类 | 定义控制流程框架，子类实现具体游戏逻辑 |
| **单例模式** | 注册管理 | 全局注册入口确保唯一性 |

### 5.2 架构原则

1. **纯逻辑与交互分离**：`GameXXXLogic` 不依赖 Minecraft API，可独立编写单元测试
2. **模块化设计**：每个游戏模块独立，降低耦合度
3. **共享实体**：四个游戏共享 `BlockRefreshEntity` 和渲染器基类，减少代码冗余
4. **统一接口**：`IGameCoreBlock` 确保所有游戏行为一致，便于扩展
5. **关注点分离**：方块层处理渲染和交互，逻辑层处理游戏规则

---

## 六、关键技术点

### 6.1 方块更新标志（updateFlags）

项目中统一使用 `updateFlags = 3`（`NOTIFY_NEIGHBORS | NOTIFY_LISTENERS`）确保正确的客户端同步：

```java
level.setBlock(pos, state, 3);  // 同时通知邻居和监听器
```

### 6.2 实体序列化

所有 `BlockEntity` 实现完整的 NBT 序列化，支持存档保存/加载：

| 方法 | 用途 | 调用时机 |
|------|------|----------|
| `saveAdditional()` | 保存私有数据到存档 | 存档时 |
| `loadAdditional()` | 从存档加载私有数据 | 加载存档时 |
| `getUpdateTag()` | 获取客户端同步数据 | 初始加载时 |
| `getUpdatePacket()` | 创建客户端同步数据包 | 状态变更时 |

### 6.3 多语言支持

通过 `LangHandler` 统一管理多语言翻译，支持中英文切换：

```java
LangHandler.addLang("msg.minesweeper.game_over", "游戏结束", "Game Over");
```

### 6.4 方块注册流程

使用自定义注册构建器简化注册流程：

```java
public static final BlockEntry<BlockMinesweeperCore> BLOCK_MINESWEEPER_CORE = REGISTRYLIB
    .block(REGISTRYLIB, "minesweeper_core", BlockMinesweeperCore::new)
    .langCn("扫雷核心方块")
    .lang("Minesweeper Core")
    .noBlockstate()
    .simpleItem()
    .register();
```

---

## 七、文件清单

### 7.1 基础框架

| 文件 | 路径 | 说明 |
|------|------|------|
| 游戏方块实体基类 | `common/base/block/BaseGameBlockEntity.java` | 方向获取、网络同步 |
| 旋转方块基类 | `common/base/block/BaseRotatedBlock.java` | 支持水平四方向朝向 |
| 垂直方块基类 | `common/base/block/BaseVerticalBlock.java` | 无方向属性 |
| 旋转刷新基类 | `common/base/block/BaseRotatedRefreshBlock.java` | 旋转布局游戏刷新控制 |
| 垂直刷新基类 | `common/base/block/BaseVerticalRefreshBlock.java` | 垂直布局游戏刷新控制 |
| 刷新实体 | `common/base/block/BlockRefreshEntity.java` | 共享刷新控制实体 |
| 游戏核心接口 | `common/base/block/IGameCoreBlock.java` | 游戏生命周期管理 |
| 旋转框架 | `common/base/block/RotatedFrame.java` | 旋转布局框架方块 |
| 垂直框架 | `common/base/block/VerticalFrame.java` | 垂直布局框架方块 |
| 基础渲染器 | `common/base/renderer/BaseGameBlockEntityRenderer.java` | 面向渲染器基类 |
| 立方体渲染器 | `common/base/renderer/BaseGameBlockEntityCubeRenderer.java` | 立方体渲染器基类 |
| 渲染状态 | `common/base/renderer/BaseGameBlockEntityRenderState.java` | 渲染状态数据 |
| 刷新渲染器 | `common/base/renderer/BlockRefreshEntityRenderer.java` | 刷新实体渲染器 |
| 奖励基类 | `common/base/reward/BaseGameReward.java` | 战利品奖励系统 |
| 旋转刷新区域 | `common/base/data/RotatedRefreshArea.java` | 旋转刷新区域枚举 |
| 垂直刷新区域 | `common/base/data/VerticalRefreshArea.java` | 垂直刷新区域枚举 |

### 7.2 工具类

| 文件 | 路径 | 说明 |
|------|------|------|
| 注册类 | `common/SimpleBlockGameRegistration.java` | 方块/实体注册入口 |
| 配方类 | `common/SimpleBlockGameRecipe.java` | 合成配方注册 |
| 语言处理 | `data/lang/LangHandler.java` | 多语言支持 |
| 配置类 | `SimpleBlockGameConfig.java` | 模组配置管理 |
| 注册核心 | `registry/GameRegistryCore.java` | 注册系统扩展 |
| 方块构建器 | `registry/builder/ModBlockBuilder.java` | 方块注册构建器 |
| 实体构建器 | `registry/builder/ModEntityBuilder.java` | 实体注册构建器 |
| 流体构建器 | `registry/builder/ModFluidBuilder.java` | 流体注册构建器 |
| 物品构建器 | `registry/builder/ModItemBuilder.java` | 物品注册构建器 |

### 7.3 游戏模块文件

| 模块 | 文件类型 | 文件 |
|------|----------|------|
| **2048** | 核心方块 | `Block2048Core.java`, `Block2048CoreEntity.java` |
| | 显示方块 | `Block2048Display.java`, `Block2048DisplayEntity.java` |
| | 刷新方块 | `Block2048Refresh.java` |
| | 数据 | `Quadrant.java`, `Value2048.java` |
| | 逻辑 | `Game2048Logic.java`, `Game2048Helper.java`, `Game2048Reward.java` |
| | 渲染器 | `Block2048CoreEntityRenderer.java`, `Block2048DisplayEntityRenderer.java` |
| **扫雷** | 核心方块 | `BlockMinesweeperCore.java`, `BlockMinesweeperCoreEntity.java` |
| | 显示方块 | `BlockMinesweeperDisplay.java`, `BlockMinesweeperDisplayEntity.java` |
| | 刷新方块 | `BlockMinesweeperRefresh.java` |
| | 数据 | `MinesweeperState.java`, `PresetDifficulty.java` |
| | 逻辑 | `GameMinesweeperLogic.java`, `GameMinesweeperHelper.java`, `GameMinesweeperReward.java` |
| | 渲染器 | `BlockMinesweeperCoreEntityRenderer.java`, `BlockMinesweeperDisplayEntityRenderer.java` |
| **记忆键** | 核心方块 | `BlockMemoryKeyCore.java`, `BlockMemoryKeyCoreEntity.java` |
| | 按键方块 | `BlockMemoryKeyButton.java`, `BlockMemoryKeyButtonEntity.java` |
| | 刷新方块 | `BlockMemoryKeyRefresh.java` |
| | 数据 | `MemoryKeyGameState.java`, `MemoryKeyLevel.java`, `MemoryKeyPosition.java` |
| | 逻辑 | `GameMemoryKeyLogic.java`, `GameMemoryKeyHelper.java`, `GameMemoryKeyReward.java` |
| | 渲染器 | `BlockMemoryKeyCoreEntityRenderer.java`, `BlockMemoryKeyButtonEntityRenderer.java` |
| **十滴水** | 核心方块 | `BlockTenDropCore.java`, `BlockTenDropCoreEntity.java` |
| | 显示方块 | `BlockTenDropDisplay.java`, `BlockTenDropDisplayEntity.java` |
| | 刷新方块 | `BlockTenDropRefresh.java` |
| | 数据 | `TenDropGameState.java`, `DropletLevel.java`, `Direction.java` |
| | 逻辑 | `GameTenDropLogic.java`, `GameTenDropHelper.java`, `GameTenDropReward.java` |

---

## 八、扩展指南

如需添加新游戏，请参考 `docs/ADDING_GAME_DOC.md` 详细指南。核心步骤：

1. 创建模块目录结构（block、data、logic、renderer）
2. 定义游戏状态枚举（XXXGameState）
3. 实现纯游戏逻辑（`GameXXXLogic`）
4. 实现核心方块实体（`BlockXXXCoreEntity`）
5. 实现核心方块（`BlockXXXCore`）
6. 实现刷新控制方块（`BlockXXXRefresh`）
7. 实现渲染器（如需要）
8. 注册方块和实体
9. 添加资源文件（模型、纹理、语言）

---

*文档版本: 2.1*  
*最后更新: 2026-05-29*  
*扩展指南: `docs/ADDING_GAME_DOC.md`*