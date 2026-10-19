# Mob Battle

Mob Battle 是一个面向 Minecraft 26.1.2 的 Fabric 战斗内容模组，包含大量自定义实体、装备、技能、网络同步、数据生成与客户端表现逻辑。本仓库当前以“保持现有功能和兼容标识”为前提，逐步把早期集中式实现迁移为按领域分层的模块。

## 开发环境

| 项目 | 版本 |
| --- | --- |
| Minecraft | 26.1.2 |
| Fabric Loader | 0.19.2 |
| Fabric API | 0.150.0+26.1.2 |
| GeckoLib | 5.5.1 |
| Java | 25 |

准确依赖以 [`gradle.properties`](gradle.properties) 和 [`build.gradle`](build.gradle) 为准。`ParticleStorm` 使用本机 Maven 仓库的 Fabric 26.1.2、`1.4.3.1` 工件；首次构建前需从对应本地工程执行 `publishToMavenLocal`。

## 代码结构

```text
src/main/java/com/kltyton/mob_battle/
├── bootstrap/                 启动阶段与稳定初始化顺序
├── skill/api/                 统一技能实体契约
├── skill/server/              服务端技能请求策略
├── network/packet/            Payload 与 Codec
├── network/receiver/client/   客户端接收器
├── network/receiver/server/   服务端接收器与信任边界
├── entity/registry/           按实体领域拆分的注册构造
├── items/registry/            按物品领域拆分的注册逻辑
├── items/gravity/             区域重力场状态与查询
├── datagen/client|server/     客户端资源与服务端数据提供器
├── event/scheduler/           基于服务器 tick 的延迟任务队列
├── client/animation/          原生动画接入与生命周期边界
└── mixin/                     按目标领域分层的最小注入
```

`ModEntities`、`ModItems` 等类仍作为兼容门面保留公开字段；新增内容应进入职责明确的领域注册器，避免再次把所有实现堆回单个入口类。

动画资源缓存、控制器初始化、过渡时间轴和骨骼复位由已安装的 GeckoLib 管理；本模组接入技能收尾、实体死亡能力和小人外观切换。小人默认使用完整原素材；精确命名为 `fufu` 时成套使用保留的彩蛋模型、纹理和动画，96 种相关物品也通过原生命名组件切换彩蛋图。钢铁侠导弹和凋零骷髅王锁定头继承原版 `ShulkerBullet`，仅保留自定义目标策略和伤害，详见 Mixin 文档。

## 统一技能实体系统

由实体动画关键帧触发的技能统一实现 `SkillEntity`：

- `canSkill()` 判断服务端是否可以发起技能；
- `hasSkill()` / `setHasSkill()` 表示服务端技能流程状态；
- `handleSkillPayload(String)` 在实体领域内处理已有协议字符串；
- `isSkillDeathSequenceActive()` 显式声明自定义死亡收尾阶段。
- `canAcceptSpawnCommand()` 只向具有服务端出生流程的实体开放 `spawn` 收尾指令。

`SkillPayloadReceiver` 是实体技能请求的统一服务端入口。它会验证目标类型、玩家跟踪关系、服务端技能状态和死亡阶段，再把指令交回实体。详细规则见 [`docs/skill-system.md`](docs/skill-system.md)。

## 构建与验证

在 Windows Git Bash 或 PowerShell 中执行：

```bash
./gradlew.bat runDatagen --no-daemon
./gradlew.bat build --no-daemon
./gradlew.bat runClient --no-daemon
```

提交前至少保证构建通过，服务端行为在实际服务端验证。涉及客户端、资源或启动路径时，必须实际启动到主菜单并检查同一次运行的 `run/logs/latest.log`；涉及数据生成时，必须审查生成文件差异。

## 文档索引

- [有套装与压缩采掘装备](docs/equipment/presence-armor.md)：套装按键、活方块生命预留与工具数值。

- [`docs/architecture.md`](docs/architecture.md)：模块边界、启动流程与扩展点。
- [`docs/skill-system.md`](docs/skill-system.md)：统一技能实体系统与迁移模板。
- [`docs/networking.md`](docs/networking.md)：网络注册、线程模型和安全边界。
- [`docs/datagen.md`](docs/datagen.md)：生成资源所有权、迁移步骤与幂等性规则。
- [`docs/mixins.md`](docs/mixins.md)：Mixin 分层、清单同步与覆写约束。
- [`docs/performance.md`](docs/performance.md)：性能热点、线程边界和并发策略。

## 贡献边界

- `任务/` 与 `参考资料/` 永远只读。
- 不改变已经发布的注册 ID、Payload Codec、技能字符串和资源键。
- 客户端输入一律视为不可信；服务端必须依据自己的实体、库存与装备状态裁决。
- Minecraft 世界、实体和注册表只在对应游戏线程访问；异步线程只处理不可变快照和纯计算。
- 新增公共入口、状态机或协议边界时使用完整中文注释，并同步对应文档和测试。
- 未经明确要求，不自动提交、推送、创建分支或发布。
