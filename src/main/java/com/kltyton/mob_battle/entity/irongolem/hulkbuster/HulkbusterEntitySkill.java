package com.kltyton.mob_battle.entity.irongolem.hulkbuster;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HulkbusterEntitySkill {
    public static void runAttackSkill(HulkbusterEntity hulkbusterEntity) {
        double range = 5.0D;
        Level world = hulkbusterEntity.level();
        if (hulkbusterEntity.tryAttackBase((ServerLevel)world, hulkbusterEntity.getTarget())) {
            AABB damageBox = hulkbusterEntity.getBoundingBox().inflate(range, range, range);
            world.getEntities(hulkbusterEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(hulkbusterEntity, living))
                    .filter(entity -> entity.distanceToSqr(hulkbusterEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != hulkbusterEntity.getTarget()) {
                            hulkbusterEntity.tryAttackBase((ServerLevel) world, (LivingEntity) entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(HulkbusterEntity hulkbusterEntity) {
        double range = 5.0D;
        Level world = hulkbusterEntity.level();
        AABB damageBox = hulkbusterEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(hulkbusterEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(hulkbusterEntity, living))
                .filter(entity -> entity.distanceToSqr(hulkbusterEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 250.0f;
                    hulkbusterEntity.tryAttackBaseDamage((ServerLevel) world, entity, attackDamage);
                });

        world.getEntities(hulkbusterEntity, damageBox).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.isAlive() && !EntityUtil.isCreativeOrSpectator(e))
                .filter(e -> e.isAlliedTo(hulkbusterEntity))
                .forEach(ally -> {
                    ally.addEffect(
                            new MobEffectInstance(
                            ModEffects.SUPER_SELF_DESTRUCT_ENTRY,
                            -1
                    ));
                });
    }
    public static void runMiniAttackSkill(HulkbusterEntity hulkbusterEntity) {
        double range = 6.0D;
        Level world = hulkbusterEntity.level();
        AABB damageBox = hulkbusterEntity.getBoundingBox().inflate(range, range, range);
        world.getEntities(hulkbusterEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(hulkbusterEntity, living))
                .filter(entity -> entity.distanceToSqr(hulkbusterEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 350.0f;
                    hulkbusterEntity.tryAttackBaseDamage((ServerLevel) world, entity, attackDamage);
                });
        world.getEntities(hulkbusterEntity, damageBox).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.isAlive() && !EntityUtil.isCreativeOrSpectator(e))
                .filter(e -> e.isAlliedTo(hulkbusterEntity))
                .forEach(ally -> {
                    ally.heal(50.0f);
                });
    }
    public static void runMaxAttackSkill(HulkbusterEntity hulkbusterEntity) {
        Level world = hulkbusterEntity.level();
        Vec3 lookVec = hulkbusterEntity.getViewVector(1.0f);
        double speed = 1.5;

        Vec3 velocity = lookVec.scale(speed);
        MissileEntity left_meteorite = new MissileEntity(ModEntities.MISSILE, world, hulkbusterEntity, 5.0f, false, 0, 400);
        Vec3 left_spawnPos = hulkbusterEntity.leftMuzzle;
        left_meteorite.snapTo(left_spawnPos.x, left_spawnPos.y, left_spawnPos.z, hulkbusterEntity.getYRot(), hulkbusterEntity.getXRot());
        left_meteorite.setDeltaMovement(velocity);

        MissileEntity right_meteorite = new MissileEntity(ModEntities.MISSILE, world, hulkbusterEntity, 5.0f, false, 0, 400);
        Vec3 right_spawnPos = hulkbusterEntity.rightMuzzle;
        right_meteorite.snapTo(right_spawnPos.x, right_spawnPos.y, right_spawnPos.z, hulkbusterEntity.getYRot(), hulkbusterEntity.getXRot());
        right_meteorite.setDeltaMovement(velocity);

        world.addFreshEntity(left_meteorite);
        world.addFreshEntity(right_meteorite);
    }
    public static void runClapHandsSkill(HulkbusterEntity hulkbusterEntity) {
        if (!(hulkbusterEntity.level() instanceof ServerLevel world)) {
            return;
        }
        Vec3 center = hulkbusterEntity.position();
        world.levelEvent(LevelEvent.PARTICLES_SMASH_ATTACK, hulkbusterEntity.getOnPos(), 750);
        for (int radius = 1; radius <= 3; radius++) {
            for (int i = 0; i < 48; i++) {
                double angle = Math.PI * 2.0D * i / 48.0D;
                world.sendParticles(ParticleTypes.SONIC_BOOM,
                        center.x + Math.cos(angle) * radius,
                        center.y + 0.25D,
                        center.z + Math.sin(angle) * radius,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        AABB damageBox = hulkbusterEntity.getBoundingBox().inflate(3.0D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(hulkbusterEntity, living)
                        && living.distanceToSqr(hulkbusterEntity) <= 9.0D)) {
            hulkbusterEntity.tryAttackBaseDamage(world, target, 280.0F);
            target.knockback(1.5D, hulkbusterEntity.getX() - target.getX(), hulkbusterEntity.getZ() - target.getZ());
        }
        hulkbusterEntity.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.4F, 0.75F);
    }

    public static void runPunchStartSkill(HulkbusterEntity hulkbusterEntity) {
        hulkbusterEntity.startPunch();
    }

    public static void runPunchEndSkill(HulkbusterEntity hulkbusterEntity) {
        if (!(hulkbusterEntity.level() instanceof ServerLevel world)) {
            return;
        }
        AABB damageBox = hulkbusterEntity.getBoundingBox().inflate(1.75D);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(hulkbusterEntity, living))) {
            hulkbusterEntity.tryAttackBaseDamage(world, target, 20.0F);
        }
        world.sendParticles(ParticleTypes.EXPLOSION, hulkbusterEntity.getX(), hulkbusterEntity.getY() + 0.5D, hulkbusterEntity.getZ(),
                8, 0.8D, 0.2D, 0.8D, 0.0D);
    }
    /**
     * 在服务端生成一个水平烟圈
     * @param world  服务器世界实例
     * @param center 中心坐标
     * @param radius 半径
     * @param density 粒子密度（圆周上的点数）
     */
    public static void spawnSmokeRing(ServerLevel world, Vec3 center, double radius, int density) {
        for (int i = 0; i < density; i++) {
            // 计算当前点的弧度
            double angle = 2 * Math.PI * i / density;

            // 计算相对于中心的偏移量
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            world.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    center.x() + dx,
                    center.y(),
                    center.z() + dz,
                    1,
                    0, 0, 0,
                    0.0     // 速度
            );
        }
    }
}
