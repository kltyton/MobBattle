# Mob Battle Mixin 规范

## 使用边界

Mixin 只用于公开事件、接口或 Access Widener 无法可靠完成的原版/依赖行为注入。每个 Mixin 必须解决一个明确问题；可以按同一目标类聚合紧密相关的注入，但不得建立包罗万象的公共注入类。

当前配置为 `src/main/resources/mob_battle.mixins.json`，其中 `mixins` 是公共侧清单，`client` 是客户端清单。源码根包为 `com.kltyton.mob_battle.mixin`。

## 目录规则

按“被修改的目标领域”分包，而不是按作者、日期或技术动作分包：

- `entity/ai/goal`：实体 AI 与 Goal。
- `entity/boss/dragon`：末影龙实体与战斗。
- `entity/player`：玩家实体行为。
- `client/gui`、`client/render`：客户端界面与渲染。
- `inventory/stack`：堆叠上限和库存语义。
- `command/argument`：命令与参数解析。
- `network`、`spawn`、`projectile`、`effect` 等：对应目标领域。

禁止恢复 `net.minecraft` 镜像式扁平路径、拼写错误目录、`misc`、`old` 或仅表示注入动作的目录。类名统一以 `Mixin` 结尾，名称表达目标类或行为。

## 清单一致性

每次移动、新增或删除 Mixin 必须同步 JSON 清单，并满足：

1. 每个带 `@Mixin` 的源码类恰好出现在一个配置分区。
2. 每个 JSON 条目都能解析到真实源码类。
3. 公共 `mixins` 分区不得引用专用客户端类型。
4. 客户端注入只放入 `client` 分区。
5. 删除未注册、不可达或已被公开 API 替代的 Mixin。
6. 不因整理包路径改变 `@At`、方法描述符、优先级或取消语义。

## 注入与覆写

`entity.player.ServerPlayerIceKnifeSwingMixin` 在原版 `ServerPlayer.swing` 重置攻击冷却前分发冰刀主手空挥；服务端校验实际物品与蓄力值，实体攻击和后续自定义左键包共用去重边界。该入口不更改原版挥手及冷却重置行为。

`wither.WitherBossMixin` 仅在受伤路径跳过二阶段挡箭分支，并将二阶段弹射物伤害乘以 0.2；出生无敌、同阵营免疫、近战伤害和二阶段外观仍使用原版逻辑。

- 优先使用最窄的 `@Inject`、`@Redirect`、`@ModifyExpressionValue` 等注入点。
- `@Overwrite` 风险最高，只在无法用更窄注入表达时使用。
- 每个 `@Overwrite` 必须有中文注释，说明为什么必须整体覆写、保留了哪些原版语义、目标版本升级时要复核什么。
- 注入方法名描述行为，不使用 `foo`、`test`、`inject1` 等临时名称。
- Shadow、Accessor 和 Invoker 只暴露实际使用的最小成员。
- 禁止吞掉注入失败；配置保持 `required: true` 和 `defaultRequire: 1`。

## 验证

### GeckoLib 边界

GeckoLib 5.5.1 自行加载、缓存和回退动画资源，并管理控制器初始化、原生过渡时间轴及骨骼复位。实体仅通过 `SkillAnimationPlayback` 消费原生触发动作完成状态，回传既有技能收尾指令；服务端死亡能力由实体的 `getDeathAnimationName()` 声明，不读取客户端资源缓存。粒子定位沿用 ParticleStorm 的原生集成。

保留的 `AnimationTimelineMixin` 只修正 5.5.1 无序关键帧标记导致的遗漏：库烘焙阶段使用无序映射，而区间查询提前结束，不能依靠 JSON 字段排序避免。升级 GeckoLib 时应先用多时间点标记验证原生查询，再决定是否移除补丁。`GeoEntityCombatLogMixin` 仅记录动作，不取消第三方实体的死亡触发。

静态检查：

1. 统计所有 `@Mixin` 类和 JSON 条目，集合必须完全相等。
2. 检查重复条目、旧包名和不存在的类。
3. 搜索 `@Overwrite`，逐个核对中文兼容说明。
4. 编译主源码，确保描述符和映射可解析。

运行检查：

1. 在实际服务端验证公共侧注入及受影响行为。
2. 执行 `runClient` 到主菜单，检查同一次 `latest.log` 中是否存在 Mixin apply、target、descriptor、ClassNotFound 或客户端类加载错误。
3. 涉及渲染、界面或输入时，必须进入对应可见表面进行人工验证；仅主菜单不能覆盖具体视觉行为。

升级 Minecraft、Fabric、MixinSquared 或映射版本时，所有 `@Overwrite` 与非简单描述符注入都必须重新审计。
