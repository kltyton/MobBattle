package com.kltyton.mob_battle.event;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.config.MobBattleConfig;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class SkillAiRecoveryEvent {
    private static final int DEFAULT_ACTIVE_SKILL_LIMIT = 10 * 20;
    private static final int LONG_ACTIVE_SKILL_LIMIT = 14 * 20;
    private static final int IDLE_NO_AI_LIMIT = 3 * 20;

    private static final Map<Mob, Integer> ACTIVE_SKILL_TICKS = new WeakHashMap<>();
    private static final Map<Mob, Integer> IDLE_NO_AI_TICKS = new WeakHashMap<>();
    private static final Map<Class<?>, ReflectiveSkillAccess> REFLECTIVE_ACCESS = new HashMap<>();

    private SkillAiRecoveryEvent() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var level : server.getAllLevels()) {
                level.getAllEntities().forEach(entity -> {
                    if (entity instanceof Mob mob) {
                        tickMob(mob);
                    }
                });
            }
        });
    }

    private static void tickMob(Mob mob) {
        if (!isMobBattleEntity(mob)) {
            clear(mob);
            return;
        }

        SkillState skillState = readSkillState(mob);
        if (!skillState.available()) {
            clear(mob);
            return;
        }

        if (skillState.hasSkill()) {
            int ticks = ACTIVE_SKILL_TICKS.merge(mob, 1, Integer::sum);
            IDLE_NO_AI_TICKS.remove(mob);
            int limit = activeSkillLimit(mob);
            if (ticks >= limit) {
                recover(mob, skillState, "active_skill_timeout", ticks);
            }
            return;
        }

        ACTIVE_SKILL_TICKS.remove(mob);
        if (mob.isNoAi() && mob.getTarget() != null) {
            int ticks = IDLE_NO_AI_TICKS.merge(mob, 1, Integer::sum);
            if (ticks >= IDLE_NO_AI_LIMIT) {
                recover(mob, skillState, "idle_no_ai_timeout", ticks);
            }
        } else {
            IDLE_NO_AI_TICKS.remove(mob);
        }
    }

    private static boolean isMobBattleEntity(Mob mob) {
        var id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        return id != null && Mob_battle.MOD_ID.equals(id.getNamespace());
    }

    private static int activeSkillLimit(Mob mob) {
        String className = mob.getClass().getName();
        if (className.contains(".deepcreature.") || className.endsWith(".SexEntity")) {
            return LONG_ACTIVE_SKILL_LIMIT;
        }
        return DEFAULT_ACTIVE_SKILL_LIMIT;
    }

    private static void recover(Mob mob, SkillState skillState, String reason, int ticks) {
        skillState.setHasSkill(false);
        mob.setNoAi(false);
        ACTIVE_SKILL_TICKS.remove(mob);
        IDLE_NO_AI_TICKS.remove(mob);
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

    private static void clear(Mob mob) {
        ACTIVE_SKILL_TICKS.remove(mob);
        IDLE_NO_AI_TICKS.remove(mob);
    }

    private static SkillState readSkillState(Mob mob) {
        if (mob instanceof GeneralEntity<?> skillEntity) {
            return new SkillState(true, skillEntity.hasSkill(), skillEntity::setHasSkill);
        }
        if (mob instanceof GeneralEntityOnlyOneSkill<?> skillEntity) {
            return new SkillState(true, skillEntity.hasSkill(), skillEntity::setHasSkill);
        }

        ReflectiveSkillAccess access = REFLECTIVE_ACCESS.computeIfAbsent(mob.getClass(), SkillAiRecoveryEvent::findReflectiveAccess);
        if (!access.available()) {
            return SkillState.UNAVAILABLE;
        }
        return new SkillState(true, access.hasSkill(mob), value -> access.setHasSkill(mob, value));
    }

    private static ReflectiveSkillAccess findReflectiveAccess(Class<?> type) {
        try {
            Method hasSkill = type.getMethod("hasSkill");
            Method setHasSkill = type.getMethod("setHasSkill", boolean.class);
            if (hasSkill.getReturnType() != boolean.class) {
                return ReflectiveSkillAccess.UNAVAILABLE;
            }
            return new ReflectiveSkillAccess(hasSkill, setHasSkill);
        } catch (NoSuchMethodException ignored) {
            return ReflectiveSkillAccess.UNAVAILABLE;
        }
    }

    @FunctionalInterface
    private interface SkillSetter {
        void set(boolean value);
    }

    private record SkillState(boolean available, boolean hasSkill, SkillSetter setter) {
        private static final SkillState UNAVAILABLE = new SkillState(false, false, value -> {
        });

        private void setHasSkill(boolean value) {
            setter.set(value);
        }
    }

    private record ReflectiveSkillAccess(Method hasSkill, Method setHasSkill) {
        private static final ReflectiveSkillAccess UNAVAILABLE = new ReflectiveSkillAccess(null, null);

        private boolean available() {
            return hasSkill != null && setHasSkill != null;
        }

        private boolean hasSkill(Mob mob) {
            try {
                return Boolean.TRUE.equals(hasSkill.invoke(mob));
            } catch (ReflectiveOperationException e) {
                if (MobBattleConfig.isDebugLoggingEnabled()) {
                    Mob_battle.LOGGER.warn("[MobBattle][SkillAiRecovery] failed to read hasSkill for {}", mob.getClass().getName(), e);
                }
                return false;
            }
        }

        private void setHasSkill(Mob mob, boolean value) {
            try {
                setHasSkill.invoke(mob, value);
            } catch (ReflectiveOperationException e) {
                if (MobBattleConfig.isDebugLoggingEnabled()) {
                    Mob_battle.LOGGER.warn("[MobBattle][SkillAiRecovery] failed to write hasSkill for {}", mob.getClass().getName(), e);
                }
            }
        }
    }
}
