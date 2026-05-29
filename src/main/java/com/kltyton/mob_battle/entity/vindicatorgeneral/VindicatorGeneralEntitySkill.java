package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VindicatorGeneralEntitySkill {
    public static void runAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 3.0D;
        Level world = vindicatorGeneralEntity.level();
        if (vindicatorGeneralEntity.tryAttackBase((ServerLevel)world, vindicatorGeneralEntity.getTarget())) {
            AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(range, range, range);
            world.getEntities(vindicatorGeneralEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                    .filter(entity -> entity.distanceToSqr(vindicatorGeneralEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != vindicatorGeneralEntity.getTarget()) {
                            vindicatorGeneralEntity.tryAttackBase((ServerLevel) world, (LivingEntity) entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        Level world = vindicatorGeneralEntity.level();
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.distanceToSqr(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 320.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerLevel) world, entity, attackDamage);
                });

    }
    public static void runMiniAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        Level world = vindicatorGeneralEntity.level();
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.distanceToSqr(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    entity.hurtServer((ServerLevel) world, entity.damageSources().mobAttack(vindicatorGeneralEntity), 200);
                    ((LivingEntity) entity).knockback(1.5D / 2, vindicatorGeneralEntity.getX() - entity.getX(), vindicatorGeneralEntity.getZ() - entity.getZ());
                });
    }
    public static void runMaxAttackSkill_1(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        Level world = vindicatorGeneralEntity.level();
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.distanceToSqr(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 350.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerLevel) world, entity, attackDamage);
                });
    }
    public static void runMaxAttackSkill_2(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        Level world = vindicatorGeneralEntity.level();
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.distanceToSqr(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 200.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerLevel) world, entity, attackDamage);
                });
    }
    public static void runMaxAttackSkill_3(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        Level world = vindicatorGeneralEntity.level();
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.distanceToSqr(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 280.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerLevel) world, entity, attackDamage);
                });
    }
    public static void runCollisionKillSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        LivingEntity target = vindicatorGeneralEntity.getTarget();
        if (target == null) {
            return;
        }
        Vec3 direction = target.position().subtract(vindicatorGeneralEntity.position());
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 destination = vindicatorGeneralEntity.position().add(horizontal.normalize().scale(4.0D));
        vindicatorGeneralEntity.teleportTo(destination.x, vindicatorGeneralEntity.getY(), destination.z);
    }

    public static void runCollisionKillDamageSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        if (!(vindicatorGeneralEntity.level() instanceof ServerLevel world)) {
            return;
        }
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(1.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))) {
            vindicatorGeneralEntity.tryAttackBaseDamage(world, target, 220.0F);
        }
    }

    public static void runSpinChopSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        if (!(vindicatorGeneralEntity.level() instanceof ServerLevel world)) {
            return;
        }
        AABB damageBox = vindicatorGeneralEntity.getBoundingBox().inflate(6.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living)
                        && living.distanceToSqr(vindicatorGeneralEntity) <= 36.0D)) {
            vindicatorGeneralEntity.tryAttackBaseDamage(world, target, 260.0F);
            target.knockback(4.0D, vindicatorGeneralEntity.getX() - target.getX(), vindicatorGeneralEntity.getZ() - target.getZ());
        }
    }

    public static void runThrowAxeSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        LivingEntity target = vindicatorGeneralEntity.getTarget();
        if (target == null || !(vindicatorGeneralEntity.level() instanceof ServerLevel world)) {
            return;
        }
        VindicatorGeneralAxeEntity axe = ModEntities.VINDICATOR_GENERAL_AXE.create(world, EntitySpawnReason.MOB_SUMMONED);
        if (axe == null) {
            return;
        }
        Vec3 start = vindicatorGeneralEntity.getEyePosition().add(vindicatorGeneralEntity.getViewVector(1.0F).scale(1.0D));
        Vec3 velocity = target.getEyePosition().subtract(start).normalize().scale(1.6D);
        axe.configure(vindicatorGeneralEntity, start, velocity);
        world.addFreshEntity(axe);
    }
}
