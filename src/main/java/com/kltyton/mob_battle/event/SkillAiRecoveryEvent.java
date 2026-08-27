package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.littleperson.skillentity.SexEntity;
import com.kltyton.mob_battle.skill.api.SkillEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Mob;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 技能实体 AI 自动恢复器：为长时间卡在技能流程（hasSkill=true）或空闲 noAi
 * 状态的实体提供生命周期安全的兜底恢复。
 *
 * <p>生命周期不变式：
 * <ul>
 *   <li>只通过 {@link ServerEntityEvents#ENTITY_LOAD} / {@link ServerEntityEvents#ENTITY_UNLOAD}
 *       跟踪“已加载且实现 {@link SkillEntity}”的 {@link Mob}，不再扫描世界中的全部实体；</li>
 *   <li>状态按服务器实例隔离，服务器停止（{@link ServerLifecycleEvents#SERVER_STOPPING}）时
 *       整体清除，不形成跨世界/跨会话的静态弱引用缓存；</li>
 *   <li>不使用反射、不检查包名：280 tick 长时上限通过显式类型
 *       （DeepCreature/SexEntity）判定，保持类型安全。</li>
 * </ul>
 */
public final class SkillAiRecoveryEvent {
    private static final int DEFAULT_ACTIVE_SKILL_LIMIT = 10 * 20;
    private static final int LONG_ACTIVE_SKILL_LIMIT = 14 * 20;
    private static final int IDLE_NO_AI_LIMIT = 3 * 20;

    /** 每服务器一份跟踪状态；服务器停止时移除，避免跨服务器泄漏。 */
    private static final Map<MinecraftServer, ServerSkillTracker> TRACKERS = new IdentityHashMap<>();

    private SkillAiRecoveryEvent() {
    }

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Mob mob && mob instanceof SkillEntity) {
                tracker(level.getServer()).track(mob);
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> {
            if (entity instanceof Mob mob) {
                ServerSkillTracker tracker = TRACKERS.get(level.getServer());
                if (tracker != null) {
                    tracker.untrack(mob);
                }
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tracker(server).tick();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(TRACKERS::remove);
    }

    private static ServerSkillTracker tracker(MinecraftServer server) {
        return TRACKERS.computeIfAbsent(server, key -> new ServerSkillTracker());
    }

    private static int activeSkillLimit(Mob mob) {
        if (mob instanceof DeepCreatureEntity || mob instanceof SexEntity) {
            return LONG_ACTIVE_SKILL_LIMIT;
        }
        return DEFAULT_ACTIVE_SKILL_LIMIT;
    }

    /**
     * 单个服务器的技能实体跟踪集合与超时计数。
     *
     * <p>实体在 ENTITY_LOAD 时加入、ENTITY_UNLOAD 时移除，因此 {@link #tick()} 只处理
     * 仍处于加载状态的跟踪实体。集合与计数全部使用身份语义（IdentityHashMap），
     * 不受实体 equals 实现影响；状态随本对象在服务器停止时整体释放。
     */
    private static final class ServerSkillTracker {
        private final Set<Mob> tracked = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Map<Mob, Integer> activeSkillTicks = new IdentityHashMap<>();
        private final Map<Mob, Integer> idleNoAiTicks = new IdentityHashMap<>();

        private void track(Mob mob) {
            tracked.add(mob);
        }

        private void untrack(Mob mob) {
            tracked.remove(mob);
            activeSkillTicks.remove(mob);
            idleNoAiTicks.remove(mob);
        }

        private void tick() {
            tracked.removeIf(mob -> {
                if (mob.isRemoved()) {
                    activeSkillTicks.remove(mob);
                    idleNoAiTicks.remove(mob);
                    return true;
                }
                tickMob(mob);
                return false;
            });
        }

        private void tickMob(Mob mob) {
            // 只有实现 SkillEntity 的 Mob 会被 track() 加入，此处转换安全。
            SkillEntity skillEntity = (SkillEntity) mob;
            if (skillEntity.hasSkill()) {
                int ticks = activeSkillTicks.merge(mob, 1, Integer::sum);
                idleNoAiTicks.remove(mob);
                if (ticks >= activeSkillLimit(mob)) {
                    recover(mob, "active_skill_timeout", ticks);
                }
                return;
            }

            activeSkillTicks.remove(mob);
            if (mob.isNoAi() && mob.getTarget() != null) {
                int ticks = idleNoAiTicks.merge(mob, 1, Integer::sum);
                if (ticks >= IDLE_NO_AI_LIMIT) {
                    recover(mob, "idle_no_ai_timeout", ticks);
                }
            } else {
                idleNoAiTicks.remove(mob);
            }
        }

        private void recover(Mob mob, String reason, int ticks) {
            ((SkillEntity) mob).setHasSkill(false);
            mob.setNoAi(false);
            activeSkillTicks.remove(mob);
            idleNoAiTicks.remove(mob);
            if (MobBattleConfig.isDebugLoggingEnabled()) {
                Mob_battle.LOGGER.warn(
                        "[MobBattle][SkillAiRecovery] reason={} entity={} id={} class={} ticks={} target={}",
                        reason,
                        mob.getType(),
                        mob.getId(),
                        mob.getClass().getName(),
                        ticks,
                        mob.getTarget() == null ? "null" : mob.getTarget().getType().toString()
                );
            }
        }
    }
}
