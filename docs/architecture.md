# Mob Battle 架构基线

## 目标与边界

本项目以保持现有玩法、存档、注册 ID、资源键和网络协议为前提，按领域与职责拆分早期集中式实现。目录名必须直接表达业务边界，禁止新增 `manager`、`util`、`misc` 等无法说明所有权的包。

绝对只读范围只有 `任务/` 与 `参考资料/`。其余源码、资源、配置、测试和文档可以在任务范围内调整，但不得覆盖共享工作树中的他人改动，也不得未经授权提交或推送。

## 分层模型

| 层级 | 目录 | 责任 |
| --- | --- | --- |
| 装配层 | `bootstrap/` | 只表达初始化阶段和既有调用顺序，不承载业务逻辑 |
| 领域层 | `entity/`、`items/`、`block/`、`effect/`、`enchantment/` | 持有实体、物品、方块及其行为；领域内部继续按功能分包 |
| 契约层 | `skill/api/`、`network/packet/`、`accessor/` | 定义跨模块稳定接口、Payload 数据形状和注入桥接接口 |
| 边界层 | `skill/server/`、`network/receiver/client|server/` | 校验不可信输入并把请求交给所属领域 |
| 注册层 | `entity/registry/`、`items/registry/` | 按内容领域构造并注册对象，保持历史注册顺序 |
| 生命周期层 | `event/`、`event/scheduler/` | 绑定加载、卸载、tick 和停服事件；释放按服务器或世界保存的状态 |
| 客户端层 | `client/` | 渲染、屏幕、输入、动画和仅客户端状态 |
| 数据层 | `datagen/client|server/` | 生成语言、模型、标签、战利品表和配方；规则见 [datagen.md](datagen.md) |
| 注入层 | `mixin/` | 按被修改的领域分包，只承担无法由公开事件或 API 完成的最小注入 |

真正跨领域且已有多个调用方的无状态逻辑，应使用描述行为的类名，例如 `AllianceRules`、`EntityQueries`、`CombatEffectApplier`。不得以“通用工具”为理由恢复含义模糊的工具集合。

## 启动顺序

通用入口为 `Mob_battle.onInitialize()`，唯一委托到 `MobBattleBootstrap.initialize()`。五个阶段及阶段内顺序均是兼容契约：

1. 配置：`MobBattleConfig`。
2. 核心类型：`ModEntityDataSerializers` → `ModSensorTypes` → `ModEntityAttributes` → `ModComponents`。
3. 内容：`ModEffects` → `ModItems` → `ModRecipeTypes` → `ModEnchantments` → `ModCommands` → `ModEvents` → `ModEntities` → `ModBlocks` → `ModBlockEntities` → `ModAttributer`。
4. 网络：`ModPackets` → `ServerPlayNetwork`。
5. 运行时：`ModSounds` → `ServerBgmManager` → `DroneManager` → `ModItemGroups` → `SkillAiRecoveryEvent` → `CompressArmorPlayerStateLifecycle` → `ModScreenHandlers`。

关键约束：

- 效果先于物品，物品先于实体，方块先于方块实体。
- Payload 类型先注册，接收器后绑定。
- 运行时索引必须在实体卸载、切换世界、玩家断线或服务器停止时释放。
- 不得以并行初始化优化上述注册顺序；Minecraft 注册表写入不是可并行工作负载。

客户端入口为 `Mob_battleClient.onInitializeClient()`。客户端接收器、渲染器、屏幕、按键与动画控制器只允许引用客户端可用类型，公共初始化路径不得加载专用客户端类。

## 兼容门面

`ModEntities` 与 `ModItems` 继续暴露历史公开字段，但不再拥有大段构造逻辑：

- 所有实体类型在 `entity/registry/` 的领域注册器中构造，`ModEntities` 只提供同名别名与稳定的最终注册顺序。
- 物品按武器、战斗、装备、设备、货币、刷怪蛋等领域注册；`ModItems` 只保留兼容字段、固定装配顺序和少量委托入口。
- `RegistrySupport` 统一处理物品注册和兼容 Map 填充，新增代码不得绕过它复制注册流程。
- `ModSkillEntityType` 是旧入口的兼容接口；新实现直接依赖 `SkillEntity`。

兼容门面中的公开字段、类型、注册 ID 和初始化先后不得随意删除或改名。若确需协议或存档迁移，必须另行提供迁移文档和回归测试。

## 跨模块调用规则

```text
Payload 数据 -> 对应端 Receiver -> 边界校验 -> 领域接口/实体方法
生命周期事件 -> 领域索引或调度器 -> 同线程状态变更
领域注册器 -> 兼容门面别名 -> Bootstrap 固定装配
Datagen Provider -> src/main/generated -> 构建资源
```

- 网络接收器不维护实体类型巨型 `switch`；技能指令由实体自己的 `handleSkillPayload` 拥有。
- 客户端发送的数据不是真实状态。实体、库存、装备、权限、距离和模式都必须在服务端重新解析。
- 世界、实体、注册表和网络上下文只在对应游戏线程访问。异步计算只接收不可变快照，结果必须回到游戏线程应用。
- 延迟游戏逻辑统一走 `ServerTickScheduler`，不得新建休眠线程、`Timer` 或无生命周期的全局任务列表。

## 扩展流程

### 新增实体

1. 把实现放入明确的实体领域包。
2. 在 `entity/registry/` 新增或扩展领域注册器。
3. 由 `ModEntities` 暴露兼容字段，并在原有顺序位置完成注册。
4. 技能实体实现 `SkillEntity`；刷怪蛋通过 `items/registry/SpawnEggItemRegistrar` 接入。
5. 同步模型、语言、标签、战利品，并验证实体运行行为。

### 新增物品

1. 按行为进入 `items/tool`、`items/armor`、`items/consumable`、`items/control` 等领域。
2. 在最接近的 `items/registry/*Registrar` 注册，统一使用 `RegistrySupport`。
3. 模型、语言、配方和标签由 Datagen 提供器生成。

### 新增网络能力

1. Payload 只定义数据与 Codec。
2. 在 `ModPackets` 保持明确且稳定的注册顺序。
3. 接收器按方向进入 `receiver/client` 或 `receiver/server`。
4. 在接收器完成线程、身份、状态和数据范围校验，再委托领域代码。
5. 为 ID、Codec 字段顺序和拒绝规则增加测试。

## 结构质量门

- 不存在空目录、失效示例、未注册 Mixin 或重复资源。
- `src/main/resources` 与 `src/main/generated` 不拥有同一路径。
- `ModEntities`、`ModItems` 不再直接堆放新领域构造逻辑。
- 网络端不出现无必要的二次 `execute`；异步入口只允许一次切回对应游戏线程。
- 结构变化同步更新 [skill-system.md](skill-system.md)、[networking.md](networking.md)、[datagen.md](datagen.md)、[mixins.md](mixins.md)、[performance.md](performance.md) 和 [ai-onboarding.md](ai-onboarding.md)。
