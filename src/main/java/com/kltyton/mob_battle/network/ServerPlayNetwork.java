package com.kltyton.mob_battle.network;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.deepcreature.DeepCreatureEntity;
import com.kltyton.mob_battle.entity.deepcreature.skill.Skill;
import com.kltyton.mob_battle.entity.drone.DroneManager;
import com.kltyton.mob_battle.entity.general.GeneralEntity;
import com.kltyton.mob_battle.entity.general.GeneralEntityOnlyOneSkill;
import com.kltyton.mob_battle.entity.general.GeneralEntitySkillHelper;
import com.kltyton.mob_battle.entity.highbird.HighbirdBaseEntity;
import com.kltyton.mob_battle.entity.irongolem.VillagerIronGolemEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntity;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.HulkbusterEntitySkill;
import com.kltyton.mob_battle.entity.irongolem.skill.IronGolemSkill;
import com.kltyton.mob_battle.entity.littleperson.giant.LittlePersonGiantEntity;
import com.kltyton.mob_battle.entity.littleperson.giant.skill.LittlePersonGiantSkill;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.guard.skill.LittlePersonGuardSkill;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.entity.littleperson.king.skill.LittlePersonKingSkill;
import com.kltyton.mob_battle.entity.littleperson.skillentity.base.BaseSkillLittlePersonEntity;
import com.kltyton.mob_battle.entity.misc.shield.ShieldEntity;
import com.kltyton.mob_battle.entity.player.PlayerEntitySkill;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntity;
import com.kltyton.mob_battle.entity.skull.archer.SkullArcherEntitySkill;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntity;
import com.kltyton.mob_battle.entity.skull.king.SkullKingEntitySkill;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntity;
import com.kltyton.mob_battle.entity.skull.mage.SkullMageEntitySkill;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntity;
import com.kltyton.mob_battle.entity.skull.warrior.SkullWarriorEntitySkill;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralEntity;
import com.kltyton.mob_battle.entity.vindicatorgeneral.VindicatorGeneralEntitySkill;
import com.kltyton.mob_battle.entity.witherskeletonking.WitherSkeletonKingEntity;
import com.kltyton.mob_battle.entity.witherskeletonking.skill.WitherSkeletonKingEntitySkill;
import com.kltyton.mob_battle.event.masterscepter.MasterScepterManager;
import com.kltyton.mob_battle.items.ModMaterial;
import com.kltyton.mob_battle.network.packet.*;
import com.kltyton.mob_battle.utils.ArmorUtil;
import com.kltyton.mob_battle.utils.EnchantmentUtil;
import com.kltyton.mob_battle.utils.EntityUtil;
import com.kltyton.mob_battle.utils.LeftClickUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

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
        ServerPlayNetworking.registerGlobalReceiver(HulkbusterEntityPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    server.execute(() -> {
                        HulkbusterEntity hulkbuster = (HulkbusterEntity) context.player().getWorld().getEntity(payload.uuid());
                        if (hulkbuster != null) {
                            switch (payload.name()) {
                                case "right_muzzle" -> hulkbuster.rightMuzzle = payload.pos();
                                case "left_muzzle" -> hulkbuster.leftMuzzle = payload.pos();
                            }
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
                                    case "catch_damage" -> Skill.runCatchDamage(deepCreature);
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
                                    case "attack" -> WitherSkeletonKingEntitySkill.runAttackSkill(kingSkeletonKing);
                                    case "super_attack" -> WitherSkeletonKingEntitySkill.runSuperAttackSkill(kingSkeletonKing);
                                    case "shot_wither_skull" -> WitherSkeletonKingEntitySkill.runWitherSkullSkill(kingSkeletonKing);
                                    case "shot_all_wither_skull" -> WitherSkeletonKingEntitySkill.runWitherAllSkullSkill(kingSkeletonKing);
                                    case "super_shot_wither_skull" -> WitherSkeletonKingEntitySkill.runSuperWitherSkullSkill(kingSkeletonKing);
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
                            case BaseSkillLittlePersonEntity baseSkillLittlePersonEntity -> {
                                switch (payload.skillName()) {
                                    case "attack2" -> baseSkillLittlePersonEntity.runSkill_2(baseSkillLittlePersonEntity);
                                    case "attack3" -> baseSkillLittlePersonEntity.runSkill_3(baseSkillLittlePersonEntity);
                                    case "attack4" -> baseSkillLittlePersonEntity.runSkill_4(baseSkillLittlePersonEntity);
                                    case "attack5" -> baseSkillLittlePersonEntity.runSkill_5(baseSkillLittlePersonEntity);
                                    case "attack6" -> baseSkillLittlePersonEntity.runSkill_6(baseSkillLittlePersonEntity);
                                    case "attack7" -> baseSkillLittlePersonEntity.runSkill_7(baseSkillLittlePersonEntity);
                                    case "attack8" -> baseSkillLittlePersonEntity.runSkill_8(baseSkillLittlePersonEntity);
                                    case "attack9" -> baseSkillLittlePersonEntity.runSkill_9(baseSkillLittlePersonEntity);
                                    case "attack10" -> baseSkillLittlePersonEntity.runSkill_10(baseSkillLittlePersonEntity);
                                    case "attack11" -> baseSkillLittlePersonEntity.runSkill_11(baseSkillLittlePersonEntity);
                                    case "die" -> baseSkillLittlePersonEntity.deathTime = 400;
                                    case "stop_ai" -> baseSkillLittlePersonEntity.setAiDisabled(true);
                                    case "start_ai" -> baseSkillLittlePersonEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        baseSkillLittlePersonEntity.setHasSkill(false);
                                        baseSkillLittlePersonEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case SkullKingEntity skullKingEntity -> {
                                switch (payload.skillName()) {
                                    case "attack" -> SkullKingEntitySkill.runAttackSkill(skullKingEntity);
                                    case "super_attack" -> SkullKingEntitySkill.runSuperAttackSkill(skullKingEntity);
                                    case "summon_skull" -> SkullKingEntitySkill.runSummonSkullSkill(skullKingEntity);
                                    case "stop_ai" -> skullKingEntity.setAiDisabled(true);
                                    case "start_ai" -> skullKingEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        skullKingEntity.setHasSkill(false);
                                        skullKingEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case SkullArcherEntity skullArcherEntity -> {
                                switch (payload.skillName()) {
                                    case "attack" -> SkullArcherEntitySkill.runAttackSkill(skullArcherEntity);
                                    case "stop_ai" -> skullArcherEntity.setAiDisabled(true);
                                    case "start_ai" -> skullArcherEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        skullArcherEntity.setHasSkill(false);
                                        skullArcherEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case SkullWarriorEntity skullWarriorEntity -> {
                                switch (payload.skillName()) {
                                    case "attack" -> SkullWarriorEntitySkill.runAttackSkill(skullWarriorEntity);
                                    case "stop_ai" -> skullWarriorEntity.setAiDisabled(true);
                                    case "start_ai" -> skullWarriorEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        skullWarriorEntity.setHasSkill(false);
                                        skullWarriorEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case SkullMageEntity skullMageEntity -> {
                                switch (payload.skillName()) {
                                    case "attack" -> SkullMageEntitySkill.runAttackSkill(skullMageEntity);
                                    case "summon_skull" -> SkullMageEntitySkill.runSummonSkullSkill(skullMageEntity);
                                    case "stop_ai" -> skullMageEntity.setAiDisabled(true);
                                    case "start_ai" -> skullMageEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        skullMageEntity.setHasSkill(false);
                                        skullMageEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case VindicatorGeneralEntity vindicatorGeneralEntity -> {
                                switch (payload.skillName()) {
                                    case "attack" -> VindicatorGeneralEntitySkill.runAttackSkill(vindicatorGeneralEntity);
                                    case "super_attack" -> VindicatorGeneralEntitySkill.runSuperAttackSkill(vindicatorGeneralEntity);
                                    case "mini_attack" -> VindicatorGeneralEntitySkill.runMiniAttackSkill(vindicatorGeneralEntity);
                                    case "max_attack_1" -> VindicatorGeneralEntitySkill.runMaxAttackSkill_1(vindicatorGeneralEntity);
                                    case "max_attack_2" -> VindicatorGeneralEntitySkill.runMaxAttackSkill_2(vindicatorGeneralEntity);
                                    case "max_attack_3" -> VindicatorGeneralEntitySkill.runMaxAttackSkill_3(vindicatorGeneralEntity);
                                    case "stop_ai" -> vindicatorGeneralEntity.setAiDisabled(true);
                                    case "start_ai" -> vindicatorGeneralEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        vindicatorGeneralEntity.setHasSkill(false);
                                        vindicatorGeneralEntity.setAiDisabled(false);
                                    }
                                }
                            }
                            case HulkbusterEntity hulkbusterEntity -> {
                                switch (payload.skillName()) {
                                    case "attack" -> HulkbusterEntitySkill.runAttackSkill(hulkbusterEntity);
                                    case "super_attack" -> HulkbusterEntitySkill.runSuperAttackSkill(hulkbusterEntity);
                                    case "mini_attack" -> HulkbusterEntitySkill.runMiniAttackSkill(hulkbusterEntity);
                                    case "max_attack" -> HulkbusterEntitySkill.runMaxAttackSkill(hulkbusterEntity);
                                    case "stop_ai" -> hulkbusterEntity.setAiDisabled(true);
                                    case "start_ai" -> hulkbusterEntity.setAiDisabled(false);
                                    case "stop" -> {
                                        hulkbusterEntity.setHasSkill(false);
                                        hulkbusterEntity.setAiDisabled(false);
                                    }
                                }
                            }

                            case null -> Mob_battle.LOGGER.warn("实体不存在或者已死亡");

                            default -> {
                                if (entity instanceof GeneralEntityOnlyOneSkill<?> skillInterface) {
                                    GeneralEntitySkillHelper.handleSkillPayload(skillInterface, payload);
                                } else if (entity instanceof GeneralEntity<?> skillInterface) {
                                    GeneralEntitySkillHelper.handleSkillPayload(skillInterface, payload);
                                } else {
                                    Mob_battle.LOGGER.warn("没有找到实体：'{}' 的技能：{}", entity.getDisplayName(), payload.skillName());
                                }
                            }

                        }
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(PlayerSkillPayload.ID,
                (payload, context) -> {
                    MinecraftServer server = context.server();
                    ServerPlayerEntity player = context.player();
                    server.execute(() -> {
                        switch (payload.skillName()) {
                            case "attack" -> PlayerEntitySkill.runAttackSkill(player);

                            case "attack2" -> PlayerEntitySkill.runAttackSkill_2(player);
                            case "attack2_run" -> PlayerEntitySkill.runAttackSkill_2Run(player);

                            case "left_whip" -> PlayerEntitySkill.runLeftWhipSkill(player);
                            case "left_whip_run" -> PlayerEntitySkill.runLeftWhipSkillRun(player);

                            case "top_knee" -> PlayerEntitySkill.runTopKneeSkill(player);
                            case "upper_hook" -> PlayerEntitySkill.runUpperHookSkill(player);
                            case "top_knee_run" -> PlayerEntitySkill.runTopKneeSkillRun(player);

                            case "collision_run" -> PlayerEntitySkill.runCollisionSkillRun(player);
                            case "collision_start" -> PlayerEntitySkill.startCollisionSkillState(player);
                            case "collision_end" -> PlayerEntitySkill.stopCollisionSkillState(player);

                            case "run_collision_run" -> PlayerEntitySkill.runRunCollisionSkillRun(player);
                            case "run_collision" -> PlayerEntitySkill.runRunCollisionSkill(player);

                            case "smashing_the_ground_run" -> PlayerEntitySkill.runSmashGroundSkillRun(player);
                            case "run_jump" -> PlayerEntitySkill.runJumpSkill(player);
                            case "smashing_the_ground" -> PlayerEntitySkill.runSmashGroundSkill(player);

                            case "scraping" -> PlayerEntitySkill.runScraping(player);
                            case "scraping_run" -> PlayerEntitySkill.runScrapingRun(player);
                            case "scraping_attack" -> PlayerEntitySkill.runScrapingAttack(player);
                            case "scraping_end" -> PlayerEntitySkill.runScrapingEnd(player);

                            case "retreat_step" -> PlayerEntitySkill.runRetreatStepRunSkill(player);

                            case "stop" -> PlayerEntitySkill.stopSkill(player);
                            case "can_move" -> PlayerEntitySkill.canMove(player);
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
                if (!ArmorUtil.hasFullArmor(player, ModMaterial.IRON_GOLD_INSTANCE)) {
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
        ServerPlayNetworking.registerGlobalReceiver(MasterScepterPayload.ID, (payload, context) -> {
            context.server().execute(() -> { // 切换到主线程
                ServerPlayerEntity player = context.player();
                String command = payload.id();
                MasterScepterManager.runCommand(player, command);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ShieldSpawnPayload.ID, (payload, context) -> {
            //翠绿套装护盾效果
            ServerPlayerEntity player = context.player();
            ServerWorld world = player.getWorld();
            context.server().execute(() -> {
                if (ArmorUtil.hasFullArmor(player, ModMaterial.EMERALD_DIAMOND_ALLOY_INSTANCE)) {
                    ItemStack cooldownItem = Items.AIR.getDefaultStack();
                    if (player.getItemCooldownManager().isCoolingDown(cooldownItem)) {
                        // 获取剩余冷却进度 (0.0 到 1.0 之间的浮点数)
                        float progress = player.getItemCooldownManager().getCooldownProgress(cooldownItem, 0);
                        float remainingSeconds = (progress * 1300) / 20.0F;
                        player.sendMessage(
                                Text.literal("护盾冷却中！还需等待 " + String.format("%.1f", remainingSeconds) + " 秒")
                                        .formatted(Formatting.RED),
                                true
                        );
                        return;
                    }
                    ShieldEntity shield = new ShieldEntity(ModEntities.SHIELD, world);
                    shield.setPosition(player.getX(), player.getY(), player.getZ());
                    shield.setOwner(player);
                    world.spawnEntity(shield);
                    player.getItemCooldownManager().set(cooldownItem, 1300);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.PLAYERS, 1.0F, 1.2F);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(ZiJinPayload.ID, (payload, context) -> {
            //紫金套装效果
            ServerPlayerEntity player = context.player();
            ServerWorld world = player.getWorld();
            context.server().execute(() -> {
                if (ArmorUtil.hasFullArmor(player, ModMaterial.ZIJIN_ARMOR_INSTANCE)) {
                    ItemStack cooldownItem = Items.AIR.getDefaultStack();
                    if (player.getItemCooldownManager().isCoolingDown(cooldownItem)) {
                        // 获取剩余冷却进度 (0.0 到 1.0 之间的浮点数)
                        float progress = player.getItemCooldownManager().getCooldownProgress(cooldownItem, 0);
                        float remainingSeconds = (progress * 700) / 20.0F;
                        player.sendMessage(
                                Text.literal("套装技能冷却中！还需等待 " + String.format("%.1f", remainingSeconds) + " 秒")
                                        .formatted(Formatting.RED),
                                true
                        );
                        return;
                    }
                    List<LivingEntity> firstRangeTargets = EntityUtil.getNearbyEntity(player, LivingEntity.class, 5.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);
                    for (LivingEntity target : firstRangeTargets) {
                        int currentAmplifier = -1; // -1 表示当前没有该效果
                        if (target.hasStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
                            currentAmplifier = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY).getAmplifier();
                        }
                        int newAmplifier = Math.min(currentAmplifier + 5, 79);
                        target.addStatusEffect(new StatusEffectInstance(ModEffects.PIG_SPIRIT_MARK_ENTRY, 160, newAmplifier, false,  false));
                    }

                    List<LivingEntity> secondRangeTargets = EntityUtil.getNearbyEntity(player, LivingEntity.class, 20.0, false, EntityUtil.TeamFilter.EXCLUDE_TEAM);
                    for (LivingEntity target : secondRangeTargets) {
                        if (target.hasStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
                            StatusEffectInstance effect = target.getStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                            int level = effect.getAmplifier() + 1;
                            // 造成魔法伤害 (等同于等级)
                            target.damage(world, player.getDamageSources().indirectMagic(player, player), (float) level);
                            // 造成攻击伤害 (等级的 5 倍)
                            // 使用 playerAttack 确保伤害来源被计入玩家
                            target.damage(world, player.getDamageSources().playerAttack(player), (float) (level * 5));
                            target.removeStatusEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY);
                        }
                    }
                    world.spawnParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0);
                    player.getItemCooldownManager().set(cooldownItem, 700);
                }
            });
        });
    }
}
