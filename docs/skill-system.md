# Mob Battle 统一技能实体系统

## 目标

所有由 `SkillPayload` 驱动的实体通过 `SkillEntity` 暴露统一状态与命令入口。网络层只验证请求，不知道具体实体技能表；每个实体拥有并测试自己的兼容字符串和副作用。

这套边界解决三个历史问题：网络层巨型实体 `switch`、通过反射读取私有技能字段、客户端仅凭实体 ID 驱动任意实体。

## SkillEntity 契约

| 方法 | 默认行为 | 责任 |
| --- | --- | --- |
| `canSkill()` | 必须实现 | 实体是否具备主动施放技能的基础条件 |
| `hasSkill()` | `false` | 服务端是否已启动技能流程 |
| `setHasSkill(boolean)` | no-op | 更新技能流程状态 |
| `isSkillDeathSequenceActive()` | `false` | 自定义死亡动画是否处于只允许收尾指令的阶段 |
| `isWaitingForAxeRecovery()` | `false` | 只读调试状态，替代网络层反射 |
| `canAcceptSpawnCommand()` | `false` | 当前实体是否处于服务端已发起的出生流程并允许 `spawn` 收尾 |
| `handleSkillPayload(String)` | `false` | 处理已通过边界校验的兼容命令；未知命令不得改变状态 |

默认实现刻意保守。仅实现接口不会自动放行客户端命令。

## 服务端请求策略

`SkillRequestPolicy` 是不访问世界的纯函数，只依据接收器采集的五项事实裁决：

```java
SkillRequestPolicy.Result result = SkillRequestPolicy.decide(
        senderTracksEntity,
        entityHasActiveSkill,
        entityIsDeadOrDying,
        entityAllowsSpawnCommand,
        command
);
```

检查顺序：

1. 空命令或纯空白 → `INVALID_COMMAND`。
2. 长度超过 64 → `COMMAND_TOO_LONG`。
3. 含 `[a-z0-9_]` 以外字符 → `INVALID_COMMAND`。
4. 发送方未跟踪目标 → `ENTITY_NOT_TRACKED`。
5. `kill` / `die` 只有实体已死亡、濒死或处于自定义死亡收尾阶段才接受，否则 → `ENTITY_NOT_DYING`。
6. `spawn` 只有实体显式允许且 `hasSkill()` 为真才接受，否则分别 → `SPAWN_NOT_ALLOWED` 或 `NO_ACTIVE_SKILL`。
7. 其他命令只有 `hasSkill()` 为真才接受，否则 → `NO_ACTIVE_SKILL`。

策略只裁决“是否有资格尝试”。实体是否认识命令仍由 `handleSkillPayload` 决定，避免维护第二份技能名表。

## 网络执行链

```text
SkillPayload
  -> 当前玩家维度解析 entityId
  -> instanceof SkillEntity
  -> PlayerLookup.tracking(entity)
  -> 读取 SkillEntity 显式状态
  -> SkillRequestPolicy.decide(...)
  -> skill.handleSkillPayload(command)
```

整个链路在服务器网络回调线程直接完成，不做无必要的二次 `server.execute`。拒绝和未知命令只有在调试日志开启时记录，不产生游戏副作用。

## 实体迁移模板

```java
public final class ExampleEntity extends Mob implements SkillEntity {
    @Override
    public boolean canSkill() {
        return !hasSkill() && getTarget() != null && cooldown == 0;
    }

    @Override
    public boolean hasSkill() {
        return entityData.get(HAS_SKILL);
    }

    @Override
    public void setHasSkill(boolean hasSkill) {
        entityData.set(HAS_SKILL, hasSkill);
    }

    @Override
    public boolean handleSkillPayload(String command) {
        return switch (command) {
            case "attack" -> {
                finishAttack();
                yield true;
            }
            case "stop" -> {
                setHasSkill(false);
                setNoAi(false);
                yield true;
            }
            default -> false;
        };
    }
}
```

迁移要求：

1. 历史协议字符串逐字保留，动画关键帧、Payload、实体处理和测试必须一致。
2. 技能启动只能由服务端状态机设置 `hasSkill=true`。
3. 未识别字符串返回 `false`，且不得部分修改状态。
4. 自定义死亡动画覆写 `isSkillDeathSequenceActive()`，禁止网络层反射字段。
5. 只有确实存在“服务端发起出生动画 → 客户端关键帧回传完成”的实体才覆写 `canAcceptSpawnCommand()`；返回值必须编码一次性的当前流程状态。
6. 通用实体可复用 `GeneralEntity`、`GeneralEntityOnlyOneSkill` 或 `BaseSkillLittlePersonEntity` 的既有实现，但不得重新建立集中分发器。

## 协议兼容

- `mob_battle:skill` 与 `mob_battle:player_skill` 的 ID、方向和 Codec 字段顺序不得改变。
- 命令只允许小写字母、数字、下划线，最大 64 字符。
- `stop` 结束技能并恢复 AI；`stop_ai` / `start_ai` 控制 AI；`kill` / `die` 是死亡收尾；`spawn` 是实体专属出生收尾。
- `SkillPayload.entityId` 按发送方所在维度解析。`PlayerSkillPayload.entityId` 的历史处理语义不得擅自改变。
- 新命令必须由服务端可达状态发起，并验证策略与实体行为。
- 删除或改名旧字符串属于协议变更，不能作为普通重构处理。

## 生命周期恢复

`SkillAiRecoveryEvent` 使用实体加载/卸载事件维护身份集合，不做每 tick 世界全量扫描。状态按 `MinecraftServer` 实例隔离，并在停服时清理。

恢复上限保持现有语义：

- 普通活跃技能：200 tick。
- `DeepCreatureEntity` / `SexEntity`：280 tick。
- 有目标但空闲 `noAi`：60 tick。
- 恢复动作：`setHasSkill(false)` 与 `setNoAi(false)`。

新增特殊状态机时，应显式提供恢复条件和生命周期清理，不得用包名、反射或无限期静态 Map 推断状态。

Xbot002 的 `attack2` 在 0.5 秒和 1.125 秒使用相同的 `runAttack2_1`。网络账本仍只消费一次 `attack2_1`，第二击由实体在第一击后 13 tick 续发；结束或重启技能时清除待发连击，两击均穿透无敌帧。

## 运行验证要求

- 覆盖普通技能、死亡、出生、未跟踪、非法字符和长度边界。
- 核对 Payload ID、Codec 往返及技能和 AI 超时恢复。
- 每个新增专属命令至少覆盖“合法状态接受”和“非法/未知状态无副作用拒绝”。
- 使用实际服务端验证接线与行为；仅编译不能证明运行时正确。
