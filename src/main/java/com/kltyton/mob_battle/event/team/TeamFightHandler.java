package com.kltyton.mob_battle.event.team;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.event.EntitySelectionEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class TeamFightHandler {
    private static final int TARGET_UPDATE_INTERVAL = 20; // 每20 tick（1秒）更新一次
    private static int tickCounter = 0;

    public static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            try {
                if (++tickCounter >= TARGET_UPDATE_INTERVAL) {
                    tickCounter = 0;
                    server.getWorlds().forEach(world -> {
                        if (world instanceof ServerWorld serverWorld) {
                            updateTeamTargets(serverWorld);
                        }
                    });
                }
            } catch (Exception e) {
                Mob_battle.LOGGER.error("团队战斗处理器发生异常", e);
            }
        });
    }

    private static void updateTeamTargets(ServerWorld world) {
        world.iterateEntities().forEach(entity -> {
            try {
                if (entity instanceof LivingEntity mob && !entity.isRemoved()) {
                    Team team = mob.getScoreboardTeam();
                    if (team != null && TeamFightManager.isInFight(team)) {

                        // 获取团队生存成员数量
                        long aliveMembers = world.getEntitiesByClass(
                                LivingEntity.class,
                                mob.getBoundingBox().expand(100),
                                e -> e.isTeammate(mob) && e.isAlive()
                        ).size();

                        if (aliveMembers == 0) {
                            TeamFightManager.stopTeamFight(team);
                        } else {
                            // 添加寻找目标的逻辑（如果需要）
                            Team targetTeam = TeamFightManager.getOpponent(team);
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

    private static void findAndSetTarget(LivingEntity mob, Team targetTeam) {
        Mob_battle.LOGGER.debug("开始为 {} 寻找目标", mob.getName());
        try {
            if (!TeamFightManager.isInFight(targetTeam)) return;

            List<LivingEntity> candidates = mob.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    mob.getBoundingBox().expand(30),
                    e -> !e.isTeammate(mob) &&
                            e.isAlive() &&
                            !e.isRemoved() &&
                            !e.equals(mob)
            );


            LivingEntity nearest = null;
            double closest = Double.MAX_VALUE;

            for (LivingEntity candidate : candidates) {
                double distance = mob.squaredDistanceTo(candidate);
                if (distance < closest) {
                    closest = distance;
                    nearest = candidate;
                }
            }
            if (mob instanceof MobEntity mob1) {
                if (nearest != null && mob1.getTarget() != nearest) {
                    // 特殊处理坚守者
                    if (mob instanceof WardenEntity warden) {
                        EntitySelectionEvent.forceWardenTarget(warden, nearest, warden.getWorld());
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