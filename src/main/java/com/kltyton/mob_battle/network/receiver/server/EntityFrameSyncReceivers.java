package com.kltyton.mob_battle.network.receiver.server;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.command.CombatLogSystem;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntity;
import com.kltyton.mob_battle.entity.piglingeneral.PiglinGeneralEntity;
import com.kltyton.mob_battle.entity.support.EntityQueries;
import com.kltyton.mob_battle.network.packet.HighbirdAttackPayload;
import com.kltyton.mob_battle.network.packet.HulkbusterEntityPayload;
import com.kltyton.mob_battle.network.packet.PiglinGeneralBonePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * 实体渲染帧同步接收器（服务端）。
 *
 * <p>三个接收器均由客户端实体渲染器/动画控制器驱动：高鸟攻击信号、
 * 绿巨人炮口位置同步、猪灵将军骨剑能量位置同步。Fabric play 阶段回调已经
 * 运行在服务器线程，所有校验与写入直接完成，避免二次排队引入帧位置延迟。
 *
 * <p>信任边界：实体由发送方所在维度按 ID/UUID 解析，因此接收器必须先做
 * {@code instanceof} 类型门（修复原先 {@code HulkbusterEntity} 无类型检查
 * 强转导致的 ClassCastException 崩溃向量），并要求发送玩家正在跟踪目标实体，
 * 防止客户端凭任意 ID/UUID 修改同维度不可见实体；未知指令名仅做调试日志。
 */
public final class EntityFrameSyncReceivers {
    private EntityFrameSyncReceivers() {
    }

    /**
     * 按原注册顺序注册本组三个接收器（高鸟、绿巨人、猪灵将军）。
     */
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(HighbirdAttackPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    Entity attacker = context.player().level().getEntity(payload.attackerId());
                    if (attacker instanceof HighbirdBaseEntity highbird
                            && isAuthorizedSender(highbird, player)
                            && highbird.level() instanceof ServerLevel serverWorld) {
                        if (highbird.performAttack(serverWorld, highbird.getTarget())) {
                            CombatLogSystem.logSkill(highbird, "highbird_attack");
                        }
                    }
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(HulkbusterEntityPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    Entity entity = context.player().level().getEntity(payload.uuid());
                    if (entity instanceof HulkbusterEntity hulkbuster
                            && isAuthorizedSender(hulkbuster, player)) {
                        if (isKnownMuzzleName(payload.name())) {
                            hulkbuster.acceptMuzzlePosition(payload.name(), payload.pos());
                        } else {
                            logUnknownMuzzleName(payload);
                        }
                    }
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(PiglinGeneralBonePayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    Entity entity = context.player().level().getEntity(payload.uuid());
                    if (entity instanceof PiglinGeneralEntity piglinGeneral
                            && isAuthorizedSender(piglinGeneral, player)) {
                        piglinGeneral.acceptSwordEnergyPosition(payload.swordEnergyPos());
                    }
                }
        );
    }

    /**
     * 帧同步包只能由正在跟踪实体的玩家提交；实体存在明确的玩家 owner 时，
     * 还必须匹配该 owner。无 owner 的自然实体保持原有 tracking 兼容行为。
     */
    static boolean isAuthorizedSender(Entity entity, ServerPlayer player) {
        return PlayerLookup.tracking(entity).contains(player)
                && isKnownPlayerOwner(entity, player);
    }

    static boolean isKnownPlayerOwner(Entity entity, ServerPlayer player) {
        Entity knownOwner = EntityQueries.getKnownPlayerOwner(entity);
        return knownOwner == null || knownOwner.getUUID().equals(player.getUUID());
    }

    private static boolean isKnownMuzzleName(String name) {
        return "right_muzzle".equals(name) || "left_muzzle".equals(name);
    }

    private static void logUnknownMuzzleName(HulkbusterEntityPayload payload) {
        if (!MobBattleConfig.isDebugLoggingEnabled()) {
            return;
        }
        Mob_battle.LOGGER.warn(
                "[MobBattle][HulkbusterEntityPayload] unknown muzzle name={} uuid={}",
                payload.name(),
                payload.uuid()
        );
    }
}
