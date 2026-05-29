package com.kltyton.mob_battle.event.team;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.event.EntitySelectionEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.scores.PlayerTeam;
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
    }

    private static void updateTeamTargets(ServerLevel world) {
        world.getAllEntities().forEach(entity -> {
            try {
                if (entity instanceof LivingEntity mob && !entity.isRemoved()) {
                    PlayerTeam team = mob.getTeam();
                    if (team != null && TeamFightManager.isInFight(team)) {

                        // 获取团队生存成员数量
                        long aliveMembers = world.getEntitiesOfClass(
                                LivingEntity.class,
                                mob.getBoundingBox().inflate(100),
                                e -> e.isAlliedTo(mob) && e.isAlive()
                        ).size();

                        if (aliveMembers == 0) {
                            TeamFightManager.stopTeamFight(team);
                        } else {
                            // 添加寻找目标的逻辑（如果需要）
                            PlayerTeam targetTeam = TeamFightManager.getOpponent(team);
                            if (targetTeam != null) {
                                findAndSetTarget(mob, targetTeam);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Mob_battle.LOGGER.error("更新实体 {} 的团队目标时发生异常", entity.getName(), e);
            }
        });
    }

    private static void findAndSetTarget(LivingEntity mob, PlayerTeam targetTeam) {
        Mob_battle.LOGGER.debug("开始为 {} 寻找目标", mob.getName());
        try {
            if (!TeamFightManager.isInFight(targetTeam)) return;

            List<LivingEntity> candidates = mob.level().getEntitiesOfClass(
                    LivingEntity.class,
                    mob.getBoundingBox().inflate(30),
                    e -> !e.isAlliedTo(mob) &&
                            !e.hasInfiniteMaterials() &&
                            !e.isSpectator() &&
                            e.isAlive() &&
                            !e.isRemoved() &&
                            !e.equals(mob)
            );


            LivingEntity nearest = null;
            double closest = Double.MAX_VALUE;

            for (LivingEntity candidate : candidates) {
                double distance = mob.distanceToSqr(candidate);
                if (distance < closest) {
                    closest = distance;
                    nearest = candidate;
                }
            }
            if (mob instanceof Mob mob1) {
                if (nearest != null && mob1.getTarget() != nearest) {
                    // 特殊处理坚守者
                    if (mob instanceof Warden warden) {
                        EntitySelectionEvent.forceWardenTarget(warden, nearest, warden.level());
                    } else {
                        mob1.setTarget(nearest);
                    }
                }
            }
        } catch (Exception e) {
            Mob_battle.LOGGER.error("为 {} 寻找目标时发生异常", mob.getName(), e);
        }
    }
}