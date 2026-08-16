package com.kltyton.mob_battle.event.team;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.event.EntitySelectionEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.scores.PlayerTeam;
import java.util.Comparator;
import java.util.List;

public class TeamFightHandler {
    private static final int TARGET_UPDATE_INTERVAL = 20; // 每20 tick（1秒）更新一次
    private static int tickCounter = 0;

    public static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            try {
                if (++tickCounter >= TARGET_UPDATE_INTERVAL) {
                    tickCounter = 0;
                    server.getAllLevels().forEach(world -> {
                        if (world instanceof ServerLevel serverWorld) {
                            updateTeamTargets(serverWorld);
                        }
                    });
                }
            } catch (Exception e) {
                Mob_battle.LOGGER.error("团队战斗处理器发生异常", e);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            tickCounter = 0;
            TeamFightManager.clearAllFights();
        });
    }

    private static void updateTeamTargets(ServerLevel world) {
        world.getAllEntities().forEach(entity -> {
            try {
                if (entity instanceof Mob mob && mob.isAlive() && !mob.isRemoved()) {
                    PlayerTeam team = mob.getTeam();
                    if (team != null && TeamFightManager.isInFight(team)) {
                        findAndSetTarget(mob);
                    }
                }
            } catch (Exception e) {
                Mob_battle.LOGGER.error("更新实体 {} 的团队目标时发生异常", entity.getName(), e);
            }
        });
    }

    private static void findAndSetTarget(Mob mob) {
        Mob_battle.LOGGER.debug("开始为 {} 寻找目标", mob.getName());
        try {
            LivingEntity currentTarget = mob.getTarget();
            if (isValidForcedTarget(mob, currentTarget)) {
                applyForcedTarget(mob, currentTarget);
                return;
            }

            double targetRange = Math.max(1.0D, mob.getAttributeValue(Attributes.FOLLOW_RANGE));
            List<LivingEntity> candidates = mob.level().getEntitiesOfClass(
                    LivingEntity.class,
                    mob.getBoundingBox().inflate(targetRange),
                    candidate -> isValidForcedTarget(mob, candidate)
            );
            candidates.stream()
                    .min(Comparator.comparingDouble(mob::distanceToSqr))
                    .ifPresent(target -> applyForcedTarget(mob, target));
        } catch (Exception e) {
            Mob_battle.LOGGER.error("为 {} 寻找目标时发生异常", mob.getName(), e);
        }
    }

    private static boolean isValidForcedTarget(Mob source, LivingEntity target) {
        return target != null
                && target.isAlive()
                && !target.isRemoved()
                && !target.hasInfiniteMaterials()
                && !target.isSpectator()
                && source.canAttack(target)
                && TeamFightManager.areForcedOpponents(source, target);
    }

    private static void applyForcedTarget(Mob mob, LivingEntity target) {
        if (mob instanceof Warden warden) {
            EntitySelectionEvent.forceWardenTarget(warden, target, warden.level());
            return;
        }

        mob.setTarget(target);
        mob.setAggressive(true);
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        Brain<?> brain = mob.getBrain();
        if (brain.checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED)) {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
    }
}
