package com.kltyton.mob_battle.entity.vindicatorgeneral;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VindicatorGeneralEntitySkill {
    public static void runAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 3.0D;
        World world = vindicatorGeneralEntity.getWorld();
        if (vindicatorGeneralEntity.tryAttackBase((ServerWorld)world, vindicatorGeneralEntity.getTarget())) {
            Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
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
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 320.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                });

    }
    public static void runMiniAttackSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
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
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 350.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                });
    }
    public static void runMaxAttackSkill_2(VindicatorGeneralEntity vindicatorGeneralEntity) {
        double range = 6.0D;
        World world = vindicatorGeneralEntity.getWorld();
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(vindicatorGeneralEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
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
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))
                .filter(entity -> entity.squaredDistanceTo(vindicatorGeneralEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 280.0f;
                    vindicatorGeneralEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(
                            ModEffects.STUN_ENTRY,
                            20
                    ));
                });
    }
    public static void runCollisionKillSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        LivingEntity target = vindicatorGeneralEntity.getTarget();
        if (target == null) {
            return;
        }
        Vec3d direction = target.getPos().subtract(vindicatorGeneralEntity.getPos());
        Vec3d horizontal = new Vec3d(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSquared() < 1.0E-4D) {
            return;
        }
        Vec3d destination = vindicatorGeneralEntity.getPos().add(horizontal.normalize().multiply(4.0D));
        vindicatorGeneralEntity.requestTeleport(destination.x, vindicatorGeneralEntity.getY(), destination.z);
    }

    public static void runCollisionKillDamageSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        if (!(vindicatorGeneralEntity.getWorld() instanceof ServerWorld world)) {
            return;
        }
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(1.0D);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living))) {
            vindicatorGeneralEntity.tryAttackBaseDamage(world, target, 220.0F);
        }
    }

    public static void runSpinChopSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        if (!(vindicatorGeneralEntity.getWorld() instanceof ServerWorld world)) {
            return;
        }
        Box damageBox = vindicatorGeneralEntity.getBoundingBox().expand(6.0D);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(vindicatorGeneralEntity, living)
                        && living.squaredDistanceTo(vindicatorGeneralEntity) <= 36.0D)) {
            vindicatorGeneralEntity.tryAttackBaseDamage(world, target, 260.0F);
            target.takeKnockback(4.0D, vindicatorGeneralEntity.getX() - target.getX(), vindicatorGeneralEntity.getZ() - target.getZ());
        }
    }

    public static void runThrowAxeSkill(VindicatorGeneralEntity vindicatorGeneralEntity) {
        LivingEntity target = vindicatorGeneralEntity.getTarget();
        if (target == null || !(vindicatorGeneralEntity.getWorld() instanceof ServerWorld world)) {
            return;
        }
        VindicatorGeneralAxeEntity axe = ModEntities.VINDICATOR_GENERAL_AXE.create(world, SpawnReason.MOB_SUMMONED);
        if (axe == null) {
            return;
        }
        Vec3d start = vindicatorGeneralEntity.getEyePos().add(vindicatorGeneralEntity.getRotationVec(1.0F).multiply(1.0D));
        Vec3d velocity = target.getEyePos().subtract(start).normalize().multiply(1.6D);
        axe.configure(vindicatorGeneralEntity, start, velocity);
        world.spawnEntity(axe);
    }
}
