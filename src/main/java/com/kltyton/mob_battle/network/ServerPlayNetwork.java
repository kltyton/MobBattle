package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.deepcreature.skill.Skill;
import com.kltyton.mob_battle.entity.drone.DroneManager;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.highbird.adulthood.HighbirdAdulthoodEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.irongolem.skill.IronGolemSkill;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.skill.LittlePersonGiantSkill;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.skill.LittlePersonGuardSkill;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.king.skill.LittlePersonKingSkill;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.KingSkill;
import com.kltyton.mob_battle.network.packet.*;
import com.kltyton.mob_battle.utils.EnchantmentUtil;
import com.kltyton.mob_battle.utils.HeadStoneUtil;
import com.kltyton.mob_battle.utils.IronGoldArmorUtil;
import com.kltyton.mob_battle.utils.LeftClickUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
                        if (anger instanceof HighbirdAdulthoodEntity highbird && highbird.getWorld() instanceof ServerWorld)
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
                        switch (entity) {
                            case DeepCreatureEntity deepCreature -> {
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
                                        Skill.runSmashGround(deepCreature, 18, 0.5, 1.5, 0.2, 3.5, 0.1);
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
                                }
                            }
                            case WitherSkeletonKingEntity kingSkeletonKing -> {
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
                                }
                            }
                            case VillagerIronGolemEntity villagerIronGolemEntity -> {
                                switch (payload.skillName()) {
                                    case "damage_1_5" -> IronGolemSkill.runSkill_1_5(villagerIronGolemEntity);
                                    case "damage_2" -> IronGolemSkill.runSkill_2(villagerIronGolemEntity);
                                    case "stop_ai" -> villagerIronGolemEntity.setAiDisabled(true);
                                    case "start_ai" -> villagerIronGolemEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        villagerIronGolemEntity.setHasSkill(false);
                                        villagerIronGolemEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case LittlePersonGiantEntity littlePersonGiant -> {
                                switch (payload.skillName()) {
                                    case "attack2" -> LittlePersonGiantSkill.runSkill_2(littlePersonGiant);
                                    case "attack3" -> LittlePersonGiantSkill.runSkill_3(littlePersonGiant);
                                    case "attack4" -> LittlePersonGiantSkill.runSkill_4(littlePersonGiant);
                                    case "stop_ai" -> littlePersonGiant.setAiDisabled(true);
                                    case "start_ai" -> littlePersonGiant.setAiDisabled(false);
                                    case "stop" -> {
                                        littlePersonGiant.setHasSkill(false);
                                        littlePersonGiant.setAiDisabled(false);
                                    }
                                }
                            }
                            case LittlePersonGuardEntity littlePersonGuardEntity -> {
                                switch (payload.skillName()) {
                                    case "attack2" -> LittlePersonGuardSkill.runSkill_2(littlePersonGuardEntity);
                                    case "stop_ai" -> littlePersonGuardEntity.setAiDisabled(true);
                                    case "start_ai" -> littlePersonGuardEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        littlePersonGuardEntity.setHasSkill(false);
                                        littlePersonGuardEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case LittlePersonKingEntity littlePersonKing -> {
                                switch (payload.skillName()) {
                                    case "attack2" -> LittlePersonKingSkill.runSkill_2(littlePersonKing);
                                    case "attack3" -> LittlePersonKingSkill.runSkill_3(littlePersonKing);
                                    case "stop_ai" -> littlePersonKing.setAiDisabled(true);
                                    case "start_ai" -> littlePersonKing.setAiDisabled(false);
                                    case "stop" -> {
                                        littlePersonKing.setHasSkill(false);
                                        littlePersonKing.setAiDisabled(false);
                                    }
                                }
                            }
                            case null, default ->
                                    Mob_battle.LOGGER.warn("没有找到实体：{}的技能：{}", entity != null ? entity.getDisplayName() : "实体不存在", payload.skillName());
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
        ServerPlayNetworking.registerGlobalReceiver(EnchantmentPayload.ID,
                (payload, context) -> {
                    ItemStack stack = payload.itemStack();
                    RegistryKey<Enchantment> enchantment = payload.enchantment();
                    int level = payload.level();

                    ServerPlayerEntity player = context.player();
                    EnchantmentUtil.addEnchantment(player, stack, enchantment, level);
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(SummonDronePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                int type = payload.type();
                if (!IronGoldArmorUtil.hasFullDiamondArmor(player)) {
                    player.sendMessage(Text.literal("您没有装备全套铁合金盔甲，无法召唤或管理无人机").formatted(Formatting.RED), true);
                    return;
                }
                if (type == 1) DroneManager.handleSummonRequest(player);
                if (type == 2) DroneManager.handleAttackDroneMode(player);
                if (type == 3) DroneManager.handleTreatmentDroneMode(player);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(ItemGroupPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> ServerPlayNetworking.send(player, new ItemGroupPayload(player.getCommandTags().contains("shen"))));
        });
    }
}
