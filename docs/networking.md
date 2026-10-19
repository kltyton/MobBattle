# Mob Battle 网络边界

## 设计原则

网络层只负责四件事：定义稳定数据格式、按固定顺序注册、验证不可信输入、把已验证请求交给所属领域。不得在 `ModPackets`、`ServerPlayNetwork` 或接收器中重新堆放实体业务逻辑。

Payload ID、方向、Codec 字段顺序和技能字符串是兼容协议。改动这些内容必须同时更新两端、迁移说明，并验证协议往返与拒绝边界。

## 目录与装配

| 目录或入口 | 责任 |
| --- | --- |
| `network/packet/` | 只定义 record、`ID`、`CODEC` 和 `type()` |
| `ModPackets.init()` | 按历史顺序向 `PayloadTypeRegistry` 注册类型 |
| `network/receiver/server/` | 服务端信任边界和领域委托 |
| `network/receiver/client/` | 客户端状态、表现与领域委托 |
| `ServerPlayNetwork.init()` | 只装配服务端接收器 |
| `ClientPlayNetwork.init()` | 只装配客户端接收器 |

注册顺序必须是“Payload 类型在前、Receiver 在后”。公共侧不得静态加载专用客户端类。

## 线程模型

Fabric 26.1 当前 play networking 回调已在对应游戏线程执行：

- `ServerPlayNetworking.registerGlobalReceiver` 回调位于服务器线程，可以直接读取和修改服务端世界。
- `ClientPlayNetworking.registerGlobalReceiver` 回调位于客户端线程，可以直接更新客户端状态或表现。
- 接收器中再次调用 `server.execute` 或 `client.execute` 只会增加一次队列跳转和延迟，因此本项目接收器直接处理。
- 只有真正来自外部工作线程的入口才允许切回游戏线程。例如 `ServerTickScheduler.schedule` 在非服务器线程调用时只执行一次 `server.execute`，并仅把任务入队。
- 不得在 `CompletableFuture`、并行流、自建线程、`Timer` 或虚拟线程中访问 `Level`、`Entity`、注册表、玩家库存或网络上下文。

官方线程与网络示例见 [Fabric Networking 文档](https://docs.fabricmc.net/develop/networking)。

## 服务端信任边界

客户端 Payload 一律视为请求，不视为事实。每个服务端接收器至少按适用范围验证：

1. 身份与权限：使用 `context.player()`，需要管理员能力时检查权限。
2. 实体：按玩家所在维度解析 ID，强转前使用 `instanceof`；需要远程控制时验证跟踪、距离或所有权。
3. 物品：以玩家服务端背包、主手、副手或装备槽中的实际 `ItemStack` 为准，禁止修改 Payload 携带的副本。
4. 数值：模式、等级、索引、长度和坐标必须限定范围。
5. 状态：冷却、技能流程、死亡阶段、装备和队伍关系由服务端重新读取。
6. 字符串：技能命令必须满足 `[a-z0-9_]` 且长度不超过 64；未知命令无副作用拒绝。
7. 线程：所有世界写入发生在服务器线程。

`SkillPayloadReceiver` 的固定链路是：

```text
entityId -> 当前维度实体 -> instanceof SkillEntity
         -> PlayerLookup.tracking -> SkillRequestPolicy
         -> entity.handleSkillPayload(command)
```

网络层不维护实体类型 `switch`，不反射实体私有技能状态。详细规则见 [skill-system.md](skill-system.md)。

## 客户端边界

客户端接收器只能修改客户端可见状态，例如动画、声音、Boss 栏、视角和物品组显示。要求：

- 实体 ID 或 UUID 解析失败时安全忽略。
- 不从客户端接收器加载专用服务端类。
- 世界尚未加载、实体已卸载或玩家为空时无副作用返回。
- 只为当前连接或当前世界保存状态，并在断开或切换世界时清理。
- 不把表现层状态反向当作服务端裁决依据。

## 新增 Payload 模板

```java
public record ExampleActionPayload(int targetId, String action)
        implements CustomPacketPayload {
    public static final Type<ExampleActionPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(Mob_battle.MOD_ID, "example_action"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExampleActionPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ExampleActionPayload::targetId,
                    ByteBufCodecs.STRING_UTF8, ExampleActionPayload::action,
                    ExampleActionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
```

服务端接收器直接在回调中校验并委托：

```java
ServerPlayNetworking.registerGlobalReceiver(ExampleActionPayload.ID, (payload, context) -> {
    Entity target = context.player().level().getEntity(payload.targetId());
    if (!(target instanceof SomeDomainEntity domainEntity)) {
        return;
    }
    if (!PlayerLookup.tracking(target).contains(context.player())) {
        return;
    }
    domainEntity.handleValidatedAction(payload.action());
});
```

新增流程：

1. 在 `network/packet/` 定义数据和 Codec。
2. 在 `ModPackets` 的正确方向按兼容顺序注册。
3. 在对应 `receiver/client|server/` 新建职责明确的接收器。
4. 在装配器固定位置调用 `init()`。
5. 增加 ID、Codec 往返、范围校验和拒绝分支测试。
6. 更新本文与协议兼容账本。

## 性能与安全检查

- 接收器内没有无必要的 `.execute(`。
- 高频 Payload 不分配重复解析表、不扫描全世界实体、不进行反射。
- 大对象、世界对象和玩家对象不跨线程捕获。
- 日志受调试开关控制，禁止在每 tick 高频路径无条件拼接长字符串。
- Codec 字段顺序和注册顺序由测试锁定；构建通过不能替代客户端和服务端运行验证。
