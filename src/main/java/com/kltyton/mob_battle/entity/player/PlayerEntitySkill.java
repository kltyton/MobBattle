package com.kltyton.mob_battle.entity.player;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import software.bernie.geckolib.animatable.GeoEntity;

public class PlayerEntitySkill {
    public static void canMove(ServerPlayerEntity player) {
        player.getDataTracker().set(DataTrackersEvent.CAN_MOVE, true);
    }
    public static void stopSkill(ServerPlayerEntity player) {
        player.getDataTracker().set(DataTrackersEvent.HAS_SKILL, false);
    }
    public static void runAttackSkill(ServerPlayerEntity player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 80);
            livingEntity.takeKnockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        });
    }
    public static void runAttackSkill_2(ServerPlayerEntity player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 150);
            livingEntity.takeKnockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        });
    }
    public static void runAttackSkill_2Run(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("attack2", false);
    }


    public static void runUpperHookSkill(ServerPlayerEntity player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            livingEntity.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 130);
            livingEntity.takeKnockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        });
    }
    public static void runTopKneeSkill(ServerPlayerEntity player) {
        LivingEntity livingEntity = EntityUtil.getClosestNearbyEntity(player, LivingEntity.class, 8, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (livingEntity != null && livingEntity != player) {
            livingEntity.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 60);
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 20));
            livingEntity.takeKnockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
            livingEntity.velocityDirty = true;
            livingEntity.setVelocity(livingEntity.getVelocity().x, 1.5, livingEntity.getVelocity().z);
        }
    }
    public static void runTopKneeSkillRun(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("top_knee", false);
    }




    public static void runCollisionSkillRun(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("collision", false);
    }
    // 供 Networking 调用，实际上逻辑由 Mixin 的 Tick 接管
    public static void startCollisionSkillState(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$startCollision();
    }
    public static void stopCollisionSkillState(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$stopCollision();
    }



    public static void runLeftWhipSkill(ServerPlayerEntity player) {
        LivingEntity livingEntity = EntityUtil.getClosestNearbyEntity(player, LivingEntity.class, 8, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (livingEntity != null && livingEntity != player) {
            livingEntity.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 70);
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            livingEntity.takeKnockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        }
    }
    public static void runLeftWhipSkillRun(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("left_whip", false);
    }




    public static void runJumpSkill(ServerPlayerEntity player) {
        player.velocityDirty = true;
        player.addVelocity(0, 1.3, 0);
        EntityAttributeInstance gravity = player.getAttributeInstance(EntityAttributes.GRAVITY);
        if (gravity != null) {
            EntityAttributeModifier gravityModifier = new EntityAttributeModifier(
                    Identifier.of(Mob_battle.MOD_ID, "player_jump_skill_gravity_modifier"),
                    1.5,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
            gravity.addTemporaryModifier(gravityModifier);
        }
        ((IPlayerSkillAccessor)player).mobBattle$setCanMove(true);
    }
    public static void runSmashGroundSkillRun(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("smashing_the_ground", false);
    }
    public static void runSmashGroundSkill(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        ((IPlayerSkillAccessor)player).mobBattle$setCanMove(false);
        // 8 格范围搜索
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(target -> {
            // 1. 造成 350 点巨额伤害
            target.damage(world, player.getDamageSources().playerAttack(player), 350f);
            target.takeKnockback(5.0, player.getX() - target.getX(), player.getZ() - target.getZ());
        });

        world.syncWorldEvent(WorldEvents.SMASH_ATTACK, player.getSteppingPos(), 750);
        player.setSpawnExtraParticlesOnFall(true);  // 生成额外的坠落粒子

        EntityAttributeInstance gravity = player.getAttributeInstance(EntityAttributes.GRAVITY);
        if (gravity != null) {
            gravity.removeModifier(Identifier.of(Mob_battle.MOD_ID, "player_jump_skill_gravity_modifier"));
        }
    }



    public static void runRunCollisionSkillRun(ServerPlayerEntity player) {
        if (player.isSprinting()) {
            ((IPlayerSkillAccessor)player).mobBattle$runAttack("run_collision", false);
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d velocity = new Vec3d(lookVec.x, 0, lookVec.z).normalize().multiply(2.5);
            player.setVelocity(velocity.x, player.getVelocity().y + 0.22, velocity.z);
            player.velocityDirty = true;
        }
    }
    public static void runRunCollisionSkill(ServerPlayerEntity player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            if (livingEntity != null && livingEntity != player) {
                livingEntity.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 200);
                livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.STUN_ENTRY, 30));
                livingEntity.requestTeleport(livingEntity.getX(), livingEntity.getY() - 1, livingEntity.getZ());
            }
        });
    }



    public static void runScrapingRun(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("scraping", false);
    }
    public static void runScraping(ServerPlayerEntity player) {
        // 1. 寻找 8 格内最近的实体
        LivingEntity target = EntityUtil.getClosestNearbyEntity(player, LivingEntity.class, 8, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (target != null) {
            ((IPlayerSkillAccessor)player).mobBattle$setGrabbedEntity(target);
            ((GeoEntity)player).triggerAnim("attack_controller", "yes_scraping");
        } else {
            ((IPlayerSkillAccessor)player).mobBattle$setGrabbedEntity(null);
            ((GeoEntity)player).triggerAnim("attack_controller", "no_scraping");
        }
    }
    public static void runScrapingAttack(ServerPlayerEntity player) {
        // 从 Mixin 获取被抓取的实体
        LivingEntity target = ((IPlayerSkillAccessor)player).mobBattle$getGrabbedEntity();
        if (target != null && target.isAlive() && target != player) {
            target.damage(player.getWorld(), player.getDamageSources().playerAttack(player), 100f);
            // 反胃 V (Nausea 5) - 10秒
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 4));
            // 虚弱 V (Weakness 5) - 10秒
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 4));
            // 黑暗 (Darkness) - 10秒
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0));
            // 缓慢 I (Slowness 1) - 10秒
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 0));
        }
    }

    public static void runScrapingEnd(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$setGrabbedEntity(null);
    }



    public static void runRetreatStepRunSkill(ServerPlayerEntity player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("retreat_step", true);
        if (((IPlayerSkillAccessor)player).mobBattle$canAttack("retreat_step")) {
            runRetreatStepSkill(player);
        }
    }
    public static void runRetreatStepSkill(PlayerEntity player) {
        // 获取玩家当前朝向（yaw）
        Vec3d viewForward = player.getRotationVec(1.0F);
        // 计算视角反方向（后方），只取水平分量（忽略垂直 Y），并归一化
        Vec3d viewBack = new Vec3d(-viewForward.x, 0, -viewForward.z).normalize();

        player.velocityDirty = true;
        double speed = 1.5;

        // 设置水平速度 + 轻微向
        double motionX = viewBack.x * speed;
        double motionZ = viewBack.z * speed;
        player.setVelocity(motionX, 0.22, motionZ);  // 0.22 ≈ 小跳高度

        if (!player.getWorld().isClient) {
            ((ServerWorld)(player.getWorld())).spawnParticles(
                    ParticleTypes.CLOUD,
                    player.getX(), player.getY() + 0.8, player.getZ(),
                    12, 0.3, 0.4, 0.3, 0.05
            );
        }
    }
}
