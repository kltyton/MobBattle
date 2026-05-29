package com.kltyton.mob_battle.entity.player;

import com.kltyton.mob_battle.Mob_battle;
import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.event.DataTrackersEvent;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;

public class PlayerEntitySkill {
    private static boolean isValidSkillTarget(ServerPlayer player, LivingEntity target) {
        return target != null && target != player && !target.getUUID().equals(player.getUUID());
    }

    public static void canMove(ServerPlayer player) {
        player.getEntityData().set(DataTrackersEvent.CAN_MOVE, true);
    }
    public static void stopSkill(ServerPlayer player) {
        player.getEntityData().set(DataTrackersEvent.HAS_SKILL, false);
    }
    public static void runAttackSkill(ServerPlayer player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            if (!isValidSkillTarget(player, livingEntity)) return;
            livingEntity.hurtServer(player.level(), player.damageSources().playerAttack(player), 130);
            livingEntity.knockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        });
    }
    public static void runAttackSkill_2(ServerPlayer player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            if (!isValidSkillTarget(player, livingEntity)) return;
            livingEntity.hurtServer(player.level(), player.damageSources().playerAttack(player), 150);
            livingEntity.knockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        });
    }
    public static void runAttackSkill_2Run(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("attack2", false);
    }


    public static void runUpperHookSkill(ServerPlayer player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            if (!isValidSkillTarget(player, livingEntity)) return;
            livingEntity.hurtServer(player.level(), player.damageSources().playerAttack(player), 160);
            livingEntity.knockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        });
    }
    public static void runTopKneeSkill(ServerPlayer player) {
        LivingEntity livingEntity = EntityUtil.getClosestNearbyEntity(player, LivingEntity.class, 8, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (isValidSkillTarget(player, livingEntity)) {
            livingEntity.hurtServer(player.level(), player.damageSources().playerAttack(player), 120);
            livingEntity.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 2 * 20));
            livingEntity.knockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
            livingEntity.hasImpulse = true;
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 1.5, livingEntity.getDeltaMovement().z);
        }
    }
    public static void runTopKneeSkillRun(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("top_knee", false);
    }




    public static void runCollisionSkillRun(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("collision", false);
    }
    // 供 Networking 调用，实际上逻辑由 Mixin 的 Tick 接管
    public static void startCollisionSkillState(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$startCollision();
    }
    public static void stopCollisionSkillState(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$stopCollision();
    }



    public static void runLeftWhipSkill(ServerPlayer player) {
        LivingEntity livingEntity = EntityUtil.getClosestNearbyEntity(player, LivingEntity.class, 8, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (isValidSkillTarget(player, livingEntity)) {
            livingEntity.hurtServer(player.level(), player.damageSources().playerAttack(player), 120);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 5 * 20, 4));
            livingEntity.knockback(1.5, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
        }
    }
    public static void runLeftWhipSkillRun(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("left_whip", false);
    }




    public static void runJumpSkill(ServerPlayer player) {
        player.hasImpulse = true;
        player.push(0, 1.3, 0);
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) {
            AttributeModifier gravityModifier = new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_jump_skill_gravity_modifier"),
                    1.5,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
            gravity.addTransientModifier(gravityModifier);
        }
        ((IPlayerSkillAccessor)player).mobBattle$setCanMove(true);
    }
    public static void runSmashGroundSkillRun(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("smashing_the_ground", false);
    }
    public static void runSmashGroundSkill(ServerPlayer player) {
        ServerLevel world = player.level();
        ((IPlayerSkillAccessor)player).mobBattle$setCanMove(false);
        // 8 格范围搜索
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(target -> {
            // 1. 造成 350 点巨额伤害
            if (!isValidSkillTarget(player, target)) return;
            target.hurtServer(world, player.damageSources().playerAttack(player), 300f);
            target.knockback(5.0, player.getX() - target.getX(), player.getZ() - target.getZ());
        });

        world.levelEvent(LevelEvent.PARTICLES_SMASH_ATTACK, player.getOnPos(), 750);
        player.setSpawnExtraParticlesOnFall(true);  // 生成额外的坠落粒子

        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) {
            gravity.removeModifier(ResourceLocation.fromNamespaceAndPath(Mob_battle.MOD_ID, "player_jump_skill_gravity_modifier"));
        }
    }



    public static void runRunCollisionSkillRun(ServerPlayer player) {
        if (player.isSprinting()) {
            ((IPlayerSkillAccessor)player).mobBattle$runAttack("run_collision", false);
            Vec3 lookVec = player.getViewVector(1.0F);
            Vec3 velocity = new Vec3(lookVec.x, 0, lookVec.z).normalize().scale(2.5);
            player.setDeltaMovement(velocity.x, player.getDeltaMovement().y + 0.22, velocity.z);
            player.hasImpulse = true;
        }
    }
    public static void runRunCollisionSkill(ServerPlayer player) {
        EntityUtil.getNearbyEntity(player, LivingEntity.class, 8, false, EntityUtil.TeamFilter.EXCLUDE_TEAM).forEach(livingEntity -> {
            if (isValidSkillTarget(player, livingEntity)) {
                livingEntity.hurtServer(player.level(), player.damageSources().playerAttack(player), 210);
                livingEntity.addEffect(new MobEffectInstance(ModEffects.STUN_ENTRY, 2 * 20));
                if (livingEntity instanceof Player) {
                    livingEntity.knockback(1.2, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, Math.max(livingEntity.getDeltaMovement().y, 0.35), livingEntity.getDeltaMovement().z);
                    livingEntity.hasImpulse = true;
                } else {
                    livingEntity.teleportTo(livingEntity.getX(), livingEntity.getY() - 1, livingEntity.getZ());
                }
            }
        });
    }



    public static void runScrapingRun(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("scraping", false);
    }
    public static void runScraping(ServerPlayer player) {
        // 1. 寻找 8 格内最近的实体
        LivingEntity target = EntityUtil.getClosestNearbyEntity(player, LivingEntity.class, 8, EntityUtil.TeamFilter.EXCLUDE_TEAM);
        if (isValidSkillTarget(player, target)) {
            ((IPlayerSkillAccessor)player).mobBattle$setGrabbedEntity(target);
            ((GeoEntity)player).triggerAnim("attack_controller", "yes_scraping");
        } else {
            ((IPlayerSkillAccessor)player).mobBattle$setGrabbedEntity(null);
            ((GeoEntity)player).triggerAnim("attack_controller", "no_scraping");
        }
    }
    public static void runScrapingAttack(ServerPlayer player) {
        // 从 Mixin 获取被抓取的实体
        LivingEntity target = ((IPlayerSkillAccessor)player).mobBattle$getGrabbedEntity();
        if (target != null && target.isAlive() && isValidSkillTarget(player, target)) {
            target.hurtServer(player.level(), player.damageSources().playerAttack(player), 135f);
            // 反胃 V (Nausea 5) - 10秒
            target.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 4));
            // 虚弱 V (Weakness 5) - 10秒
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 4));
            // 黑暗 (Darkness) - 10秒
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
            // 缓慢 I (Slowness 1) - 10秒
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 0));
        }
    }

    public static void runScrapingEnd(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$setGrabbedEntity(null);
    }



    public static void runRetreatStepRunSkill(ServerPlayer player) {
        ((IPlayerSkillAccessor)player).mobBattle$runAttack("retreat_step", true);
        if (((IPlayerSkillAccessor)player).mobBattle$canAttack("retreat_step")) {
            runRetreatStepSkill(player);
        }
    }
    public static void runRetreatStepSkill(Player player) {
        // 获取玩家当前朝向（yaw）
        Vec3 viewForward = player.getViewVector(1.0F);
        // 计算视角反方向（后方），只取水平分量（忽略垂直 Y），并归一化
        Vec3 viewBack = new Vec3(-viewForward.x, 0, -viewForward.z).normalize();

        player.hasImpulse = true;
        double speed = 1.5;

        // 设置水平速度 + 轻微向
        double motionX = viewBack.x * speed;
        double motionZ = viewBack.z * speed;
        player.setDeltaMovement(motionX, 0.22, motionZ);  // 0.22 ≈ 小跳高度

        if (!player.level().isClientSide) {
            ((ServerLevel)(player.level())).sendParticles(
                    ParticleTypes.CLOUD,
                    player.getX(), player.getY() + 0.8, player.getZ(),
                    12, 0.3, 0.4, 0.3, 0.05
            );
        }
    }
}
