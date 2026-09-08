package com.kltyton.mob_battle.network.receiver.server;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.command.CombatLogSystem;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.network.packet.SkillPayload;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.skill.api.SkillEntity;
import com.kltyton.mob_battle.skill.server.SkillCommandLedger;
import com.kltyton.mob_battle.skill.server.SkillRequestPolicy;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * 统一技能指令接收器（服务端），替代原先 {@code ServerPlayNetwork} 中
 * 按实体类型分派的大型 {@code switch (entity)}。
 *
 * <p>Fabric {@code ServerPlayNetworking} 的 play 阶段回调已保证在服务器线程执行，
 * 因此本接收器不再用 {@code server.execute} 二次派发，直接在回调内完成校验与分发。
 *
 * <p>信任边界（本文件是技能线唯一的服务端入口）：
 * <ol>
 *   <li>实体必须实现 {@link SkillEntity}，未迁移或类型不符的实体一律拒绝；</li>
 *   <li>发送方必须正在跟踪目标实体（{@link PlayerLookup#tracking}），
 *       防止客户端仅凭 entityId 驱动同维度任意实体；</li>
 *   <li>若实体存在已知的玩家 owner，发送方必须是该 owner；无 owner 或非玩家 owner
 *       的自然怪仍保持 tracking 兼容行为；</li>
 *   <li>策略 {@link SkillRequestPolicy} 要求实体处于服务端已启动的技能流程
 *       （{@code hasSkill()}），或对 {@code kill}/{@code die} 死亡收尾指令
 *       处于死亡/濒死状态；{@code spawn} 出生收尾指令仅当实体自身通过
 *       {@link SkillEntity#canAcceptSpawnCommand()} 声明当前处于服务端出生流程时
 *       才放行；</li>
 *   <li>影响命令由 {@link SkillCommandLedger} 在每个活动技能会话内按命令字符串只消费一次，
 *       生命周期命令不占用该账本；</li>
 *   <li>命令格式（非空、长度上限、字符集）与上述状态一起由策略裁决。</li>
 * </ol>
 *
 * <p>实体技能状态（{@code hasSkill}）、死亡收尾阶段
 * （{@code isSkillDeathSequenceActive}）与斧恢复状态
 * （{@code isWaitingForAxeRecovery}）全部通过 {@link SkillEntity} 契约读取，
 * 不再使用反射。未识别指令由 {@link SkillEntity#handleSkillPayload} 返回
 * {@code false} 并仅记录日志，不改变实体状态。
 *
 * <p>所有拒绝与未识别原因仅在 {@link MobBattleConfig#isDebugLoggingEnabled()}
 * 开启时输出，行为与原调试状态日志保持一致。
 */
public final class SkillPayloadReceiver {
    private SkillPayloadReceiver() {
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SkillPayload.ID,
                (payload, context) -> handleSkillPayload(context.player(), payload)
        );
    }

    private static void handleSkillPayload(ServerPlayer player, SkillPayload payload) {
        Entity entity = player.level().getEntity(payload.entityId());
        logSkillPayloadState("before", player, payload, entity);
        if (!(entity instanceof SkillEntity skill)) {
            logRejected(player, payload, entity,
                    entity == null ? "ENTITY_NOT_FOUND" : "NOT_SKILL_ENTITY");
            logSkillPayloadState("after", player, payload, entity);
            return;
        }

        var server = player.level().getServer();
        SkillCommandLedger.observeSession(server, entity, skill.hasSkill());
        boolean senderTracks = PlayerLookup.tracking(entity).contains(player);
        var knownPlayerOwner = EntityQueries.getKnownPlayerOwner(entity);
        boolean senderIsKnownPlayerOwner = knownPlayerOwner == null
                || knownPlayerOwner.getUUID().equals(player.getUUID());
        SkillRequestPolicy.Result result = SkillRequestPolicy.decide(
                senderTracks,
                senderIsKnownPlayerOwner,
                skill.hasSkill(),
                isDeadOrDying(entity, skill),
                skill.canAcceptSpawnCommand(),
                payload.skillName()
        );
        if (!result.accepted()) {
            logRejected(player, payload, entity, result.decision().name());
            logSkillPayloadState("after", player, payload, entity);
            return;
        }

        boolean impactCommand = !SkillRequestPolicy.isLifecycleCommand(payload.skillName());
        if (impactCommand && !SkillCommandLedger.consumeImpactCommand(server, entity, payload.skillName())) {
            logRejected(player, payload, entity, "COMMAND_ALREADY_CONSUMED");
            logSkillPayloadState("after", player, payload, entity);
            return;
        }

        boolean handled = handleSkillCommand(skill, payload.skillName(),
                () -> CombatLogSystem.logAction(entity, "触发技能事件 " + payload.skillName()));
        if (!handled) {
            if (impactCommand) {
                SkillCommandLedger.releaseImpactCommand(server, entity, payload.skillName());
            }
            logRejected(player, payload, entity, "UNRECOGNIZED");
        } else {
            SkillCommandLedger.observeSession(server, entity, skill.hasSkill());
            if (SkillRequestPolicy.endsSkillSession(payload.skillName())) {
                SkillCommandLedger.clearEntity(server, entity);
            }
        }
        logSkillPayloadState("after", player, payload, entity);
    }

    /**
     * 先让实体确认指令，再执行成功后的战斗日志；未知指令不产生日志。
     */
    static boolean handleSkillCommand(SkillEntity skill, String command, Runnable onHandled) {
        boolean handled = skill.handleSkillPayload(command);
        if (handled) {
            onHandled.run();
        }
        return handled;
    }

    /**
     * 判断实体是否处于“已死亡或正在死亡”状态。
     *
     * <p>绝大多数实体直接使用 vanilla {@link LivingEntity#isDeadOrDying()}。
     * 自定义死亡动画实体通过 {@link SkillEntity#isSkillDeathSequenceActive()}
     * 显式暴露死亡收尾阶段，网络边界不读取实体私有实现。
     */
    private static boolean isDeadOrDying(Entity entity, SkillEntity skill) {
        return entity instanceof LivingEntity living && living.isDeadOrDying()
                || skill.isSkillDeathSequenceActive();
    }

    private static void logRejected(ServerPlayer player, SkillPayload payload, Entity entity, String reason) {
        if (!MobBattleConfig.isDebugLoggingEnabled()) {
            return;
        }
        Mob_battle.LOGGER.warn(
                "[MobBattle][SkillPayload] rejected player={} skill={} entityId={} entity={} reason={}",
                player.getName().getString(),
                payload.skillName(),
                payload.entityId(),
                entity == null ? "null" : entity.getType() + "#" + entity.getId(),
                reason
        );
    }

    private static void logSkillPayloadState(String phase, ServerPlayer player, SkillPayload payload, Entity entity) {
        if (!MobBattleConfig.isDebugLoggingEnabled()) {
            return;
        }

        if (entity == null) {
            Mob_battle.LOGGER.warn(
                    "[MobBattle][SkillPayload] phase={} player={} skill={} entityId={} entity=null",
                    phase,
                    player.getName().getString(),
                    payload.skillName(),
                    payload.entityId()
            );
            return;
        }

        Mob_battle.LOGGER.info(
                "[MobBattle][SkillPayload] phase={} player={} skill={} entity={} id={} uuid={} class={} tick={} removed={} alive={} noAi={} hasSkill={} deathSequence={} waitingAxeRecovery={} target={} pos=({}, {}, {}) delta=({}, {}, {})",
                phase,
                player.getName().getString(),
                payload.skillName(),
                entity.getType(),
                entity.getId(),
                entity.getUUID(),
                entity.getClass().getName(),
                entity.tickCount,
                entity.isRemoved(),
                entity.isAlive(),
                entity instanceof Mob mob && mob.isNoAi(),
                readSkillState(entity, SkillEntity::hasSkill),
                readSkillState(entity, SkillEntity::isSkillDeathSequenceActive),
                readSkillState(entity, SkillEntity::isWaitingForAxeRecovery),
                describeTarget(entity),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getDeltaMovement().x,
                entity.getDeltaMovement().y,
                entity.getDeltaMovement().z
        );
    }

    /**
     * 通过 {@link SkillEntity} 契约读取调试日志需要的技能状态，取代反射；
     * 尚未实现该契约的实体显示为 {@code n/a}。
     */
    private static String readSkillState(Entity entity, java.util.function.Function<SkillEntity, Boolean> stateGetter) {
        if (!(entity instanceof SkillEntity skillEntity)) {
            return "n/a";
        }
        return String.valueOf(stateGetter.apply(skillEntity));
    }

    private static String describeTarget(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return "n/a";
        }

        LivingEntity target = mob.getTarget();
        return target == null ? "null" : target.getType() + "#" + target.getId();
    }
}
