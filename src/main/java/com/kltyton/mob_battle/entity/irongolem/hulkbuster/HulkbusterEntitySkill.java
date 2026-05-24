package com.kltyton.mob_battle.entity.irongolem.hulkbuster;

import com.kltyton.mob_battle.effect.ModEffects;
import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.irongolem.hulkbuster.missile.MissileEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.World;

public class HulkbusterEntitySkill {
    public static void runAttackSkill(HulkbusterEntity hulkbusterEntity) {
        double range = 5.0D;
        World world = hulkbusterEntity.getWorld();
        if (hulkbusterEntity.tryAttackBase((ServerWorld)world, hulkbusterEntity.getTarget())) {
            Box damageBox = hulkbusterEntity.getBoundingBox().expand(range, range, range);
            world.getOtherEntities(hulkbusterEntity, damageBox).stream()
                    .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(hulkbusterEntity, living))
                    .filter(entity -> entity.squaredDistanceTo(hulkbusterEntity) <= range * range)
                    .forEach(entity -> {
                        if (entity != hulkbusterEntity.getTarget()) {
                            hulkbusterEntity.tryAttackBase((ServerWorld) world, (LivingEntity) entity);
                        }
                    });
        }
    }
    public static void runSuperAttackSkill(HulkbusterEntity hulkbusterEntity) {
        double range = 5.0D;
        World world = hulkbusterEntity.getWorld();
        Box damageBox = hulkbusterEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(hulkbusterEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(hulkbusterEntity, living))
                .filter(entity -> entity.squaredDistanceTo(hulkbusterEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 250.0f;
                    hulkbusterEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                });

        world.getOtherEntities(hulkbusterEntity, damageBox).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.isAlive() && !EntityUtil.isCreativeOrSpectator(e))
                .filter(e -> e.isTeammate(hulkbusterEntity))
                .forEach(ally -> {
                    ally.addStatusEffect(
                            new StatusEffectInstance(
                            ModEffects.SUPER_SELF_DESTRUCT_ENTRY,
                            -1
                    ));
                });
    }
    public static void runMiniAttackSkill(HulkbusterEntity hulkbusterEntity) {
        double range = 6.0D;
        World world = hulkbusterEntity.getWorld();
        Box damageBox = hulkbusterEntity.getBoundingBox().expand(range, range, range);
        world.getOtherEntities(hulkbusterEntity, damageBox).stream()
                .filter(entity -> entity instanceof LivingEntity living && EntityUtil.isValidCombatTarget(hulkbusterEntity, living))
                .filter(entity -> entity.squaredDistanceTo(hulkbusterEntity) <= range * range)
                .forEach(entity -> {
                    float attackDamage = 350.0f;
                    hulkbusterEntity.tryAttackBaseDamage((ServerWorld) world, entity, attackDamage);
                });
        world.getOtherEntities(hulkbusterEntity, damageBox).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.isAlive() && !EntityUtil.isCreativeOrSpectator(e))
                .filter(e -> e.isTeammate(hulkbusterEntity))
                .forEach(ally -> {
                    ally.heal(50.0f);
                });
    }
    public static void runMaxAttackSkill(HulkbusterEntity hulkbusterEntity) {
        World world = hulkbusterEntity.getWorld();
        Vec3d lookVec = hulkbusterEntity.getRotationVec(1.0f);
        double speed = 1.5;

        Vec3d velocity = lookVec.multiply(speed);
        MissileEntity left_meteorite = new MissileEntity(ModEntities.MISSILE, world, hulkbusterEntity, 5.0f, false, 0, 400);
        Vec3d left_spawnPos = hulkbusterEntity.leftMuzzle;
        left_meteorite.refreshPositionAndAngles(left_spawnPos.x, left_spawnPos.y, left_spawnPos.z, hulkbusterEntity.getYaw(), hulkbusterEntity.getPitch());
        left_meteorite.setVelocity(velocity);

        MissileEntity right_meteorite = new MissileEntity(ModEntities.MISSILE, world, hulkbusterEntity, 5.0f, false, 0, 400);
        Vec3d right_spawnPos = hulkbusterEntity.rightMuzzle;
        right_meteorite.refreshPositionAndAngles(right_spawnPos.x, right_spawnPos.y, right_spawnPos.z, hulkbusterEntity.getYaw(), hulkbusterEntity.getPitch());
        right_meteorite.setVelocity(velocity);

        world.spawnEntity(left_meteorite);
        world.spawnEntity(right_meteorite);
    }
    public static void runClapHandsSkill(HulkbusterEntity hulkbusterEntity) {
        if (!(hulkbusterEntity.getWorld() instanceof ServerWorld world)) {
            return;
        }
        Vec3d center = hulkbusterEntity.getPos();
        world.syncWorldEvent(WorldEvents.SMASH_ATTACK, hulkbusterEntity.getSteppingPos(), 750);
        for (int radius = 1; radius <= 3; radius++) {
            for (int i = 0; i < 48; i++) {
                double angle = Math.PI * 2.0D * i / 48.0D;
                world.spawnParticles(ParticleTypes.SONIC_BOOM,
                        center.x + Math.cos(angle) * radius,
                        center.y + 0.25D,
                        center.z + Math.sin(angle) * radius,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        Box damageBox = hulkbusterEntity.getBoundingBox().expand(3.0D);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(hulkbusterEntity, living)
                        && living.squaredDistanceTo(hulkbusterEntity) <= 9.0D)) {
            target.timeUntilRegen = 0;
            hulkbusterEntity.tryAttackBaseDamage(world, target, 280.0F);
            target.takeKnockback(1.5D, hulkbusterEntity.getX() - target.getX(), hulkbusterEntity.getZ() - target.getZ());
        }
        hulkbusterEntity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), 1.4F, 0.75F);
    }

    public static void runPunchStartSkill(HulkbusterEntity hulkbusterEntity) {
        hulkbusterEntity.startPunch();
    }

    public static void runPunchEndSkill(HulkbusterEntity hulkbusterEntity) {
        if (!(hulkbusterEntity.getWorld() instanceof ServerWorld world)) {
            return;
        }
        Box damageBox = hulkbusterEntity.getBoundingBox().expand(1.75D);
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, damageBox,
                living -> EntityUtil.isValidCombatTarget(hulkbusterEntity, living))) {
            target.timeUntilRegen = 0;
            hulkbusterEntity.tryAttackBaseDamage(world, target, 20.0F);
        }
        world.spawnParticles(ParticleTypes.EXPLOSION, hulkbusterEntity.getX(), hulkbusterEntity.getY() + 0.5D, hulkbusterEntity.getZ(),
                8, 0.8D, 0.2D, 0.8D, 0.0D);
    }
    /**
     * 在服务端生成一个水平烟圈
     * @param world  服务器世界实例
     * @param center 中心坐标
     * @param radius 半径
     * @param density 粒子密度（圆周上的点数）
     */
    public static void spawnSmokeRing(ServerWorld world, Vec3d center, double radius, int density) {
        for (int i = 0; i < density; i++) {
            // 计算当前点的弧度
            double angle = 2 * Math.PI * i / density;

            // 计算相对于中心的偏移量
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            world.spawnParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    center.getX() + dx,
                    center.getY(),
                    center.getZ() + dz,
                    1,
                    0, 0, 0,
                    0.0     // 速度
            );
        }
    }
}
