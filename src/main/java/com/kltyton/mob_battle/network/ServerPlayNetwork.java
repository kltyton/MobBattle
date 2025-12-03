package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.deepcreature.skill.Skill;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.irongolem.skill.IronGolemSkill;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.KingSkill;
import com.kltyton.mob_battle.network.packet.*;
import com.kltyton.mob_battle.utils.HeadStoneUtil;
import com.kltyton.mob_battle.utils.LeftClickUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerPlayNetwork {
    public static void init() {
        // 注册服务器端接收器
        ServerPlayNetworking.registerGlobalReceiver(HighbirdAttackPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity attacker = context.player().getWorld().getEntityById(payload.attackerId());
                        if (attacker instanceof HighbirdBaseEntity highbird && highbird.getWorld() instanceof ServerWorld serverWorld)
                            highbird.performAttack(serverWorld, highbird.getTarget());
                    });
                }
        );
        // 注册服务器端接收器
        ServerPlayNetworking.registerGlobalReceiver(HighbirdAngerPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity anger = context.player().getWorld().getEntityById(payload.angerId());
                        if (anger instanceof HighbirdAdulthoodEntity highbird && highbird.getWorld() instanceof ServerWorld serverWorld)
                            highbird.setAiDisabled(false);
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(KeepInventoryPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity entity = context.player().getWorld().getEntityById(payload.keeperId());
                        if (entity != null) {
                            HeadStoneUtil.setKeep(entity.getUuid(), payload.isKeep());
                        }
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(SkillPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        Entity entity = context.player().getWorld().getEntityById(payload.entityId());
                        if (entity instanceof DeepCreatureEntity deepCreature) {
                            switch (payload.skillName()) {
                                case "roar" -> Skill.runRoarSkill(deepCreature);
                                case "earthquake" -> Skill.runEarthquake(deepCreature);
                                case "smash" -> Skill.runSmash(deepCreature);
                                case "side" -> Skill.runSideSkill(deepCreature);
                                case "sonic_boom" -> Skill.runSonicBoom(deepCreature);
                                case "charge" -> Skill.runCharge(deepCreature);
                                case "stop_ai" -> deepCreature.setAiDisabled(true);
                                case "start_ai" -> deepCreature.setAiDisabled(false);
                                case "smash_ground_s" -> {
                                    deepCreature.setAiDisabled(true);
                                    Skill.runSmashGround(deepCreature, 10, 0.5, 1.0, 0.25, 3.0, 0.15);
                                }
                                case "smash_ground_xl" -> {
                                    deepCreature.setAiDisabled(true);
                                    Skill.runSmashGround(deepCreature, 18,0.5, 1.5, 0.2, 3.5, 0.1);
                                }
                                case "kill" -> deepCreature.remove(Entity.RemovalReason.KILLED);
                                case "catch" -> Skill.runCatch(deepCreature);
                                case "catch_end" -> Skill.runCatchEnd(deepCreature);
                                case "stop_run_catch" -> Skill.stopRunCatch(deepCreature);
                                case "damage" -> Skill.runDamage(deepCreature);
                                case "stop" -> {
                                    deepCreature.setHasSkill(false);
                                    deepCreature.setAiDisabled(false);
                                }
                                default -> Mob_battle.LOGGER.warn("没有找到技能：" + payload.skillName());
                            }
                        } else if (entity instanceof WitherSkeletonKingEntity kingSkeletonKing) {
                            switch (payload.skillName()) {
                                case "attack" -> KingSkill.runAttackSkill(kingSkeletonKing);
                                case "super_attack" -> KingSkill.runSuperAttackSkill(kingSkeletonKing);
                                case "shot_wither_skull" -> KingSkill.runWitherSkullSkill(kingSkeletonKing);
                                case "shot_all_wither_skull" -> KingSkill.runWitherAllSkullSkill(kingSkeletonKing);
                                case "stop_ai" -> kingSkeletonKing.setAiDisabled(true);
                                case "start_ai" -> kingSkeletonKing.setAiDisabled(false);
                                case "stop" -> {
                                    kingSkeletonKing.setHasSkill(false);
                                    kingSkeletonKing.setAiDisabled(false);
                                }
                                default -> Mob_battle.LOGGER.warn("没有找到技能： " + payload.skillName());
                            }
                        } else if (entity instanceof VillagerIronGolemEntity villagerIronGolemEntity) {
                            switch (payload.skillName()) {
                                case "damage_1_5" -> IronGolemSkill.runSkill_1_5(villagerIronGolemEntity);
                                case "damage_2" -> IronGolemSkill.runSkill_2(villagerIronGolemEntity);
                                case "stop_ai" -> villagerIronGolemEntity.setAiDisabled(true);
                                case "start_ai" -> villagerIronGolemEntity.setAiDisabled(false);
                                case "stop" -> {
                                    villagerIronGolemEntity.setHasSkill(false);
                                    villagerIronGolemEntity.setAiDisabled(false);
                                }
                                default -> Mob_battle.LOGGER.warn("没有找到技能： " + payload.skillName());
                            }
                        }
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(LeftClickPacket.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    LeftClickUtil.leftClick(player, payload.pressing(), true);
                }
        );
    }
}
