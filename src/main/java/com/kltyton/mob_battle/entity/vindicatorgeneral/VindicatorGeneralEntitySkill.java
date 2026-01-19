package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class VindicatorGeneralEntitySkill {
    public static void runAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 3.0D;
        World world = vindicatorGeneralEntity.getWorld();
        if (vindicatorGeneralEntity.tryAttackBase((ServerWorld)world, vindicatorGeneralEntity.getTarget())) {
            Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .filter(entity -> !entity.isTeammate(vindicatorGeneralEntity))
                    .filter(entity -> !entity.isSpectator() && entity.isAlive())
                    .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != vindicatorGeneralEntity.getTarget()) {
                            vindicatorGeneralEntity.tryAttackBase((ServerWorld) world, (LivingEntity) entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(vindicatorGeneralEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 350.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                });

    }
    public static void runMiniAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(vindicatorGeneralEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    entity.damage((ServerWorld) world, entity.getDamageSources().mobAttack(vindicatorGeneralEntity), 200);
                    ((LivingEntity) entity).takeKnockback(1.5D / 2, vindicatorGeneralEntity.getX() - entity.getX(), vindicatorGeneralEntity.getZ() - entity.getZ());
                });
    }
    public static void runMaxAttackSkill_1(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(vindicatorGeneralEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 400.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                });
    }
    public static void runMaxAttackSkill_2(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(vindicatorGeneralEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 200.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SLOWNESS,
                            20,
                            4
                    ));
                });
    }
    public static void runMaxAttackSkill_3(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !entity.isTeammate(vindicatorGeneralEntity))
                .filter(entity -> !entity.isSpectator() && entity.isAlive())
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 300.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(
                            ModEffects.STUN_ENTRY,
                            20
                    ));
                });
    }
}
