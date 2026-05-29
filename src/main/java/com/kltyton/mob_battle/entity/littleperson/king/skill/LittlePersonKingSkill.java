package com.kltyton.mob_battle.entity.littleperson.king.skill;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import com.kltyton.mob_battle.utils.EntityUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class LittlePersonKingSkill {

    public static void runSkill_2(LittlePersonKingEntity littlePersonKingEntity) {
        summonLittlePersonGuardEntity(littlePersonKingEntity, littlePersonKingEntity.isViolent() ? 6 : 2);
    }

    public static void runSkill_3(LittlePersonKingEntity littlePersonKingEntity) {
        if (littlePersonKingEntity.level().isClientSide()) return;
        ServerLevel serverWorld = (ServerLevel) littlePersonKingEntity.level();
        // 根据国王朝向计算前方约2格的位置
        Vec3 look = littlePersonKingEntity.getViewVector(1.0F).normalize().scale(2.0);
        Vec3 lightningPos = littlePersonKingEntity.getEyePosition().add(look);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverWorld, EntitySpawnReason.EVENT);
        if (lightning == null) return;

        lightning.setVisualOnly(true);
        lightning.setPos(lightningPos.x, lightningPos.y, lightningPos.z);
        serverWorld.addFreshEntity(lightning);
        List<LivingEntity> target = getNearbyLivingEntities(littlePersonKingEntity, 4.0);
        for (LivingEntity targetEntity : target) {
            targetEntity.hurtServer(serverWorld, targetEntity.damageSources().lightningBolt(), 120.0F);
        }
        serverWorld.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                lightningPos.x, lightningPos.y + 0.5, lightningPos.z,
                50, 0.5, 0.5, 0.5, 0.2);
    }
    public static void summonLittlePersonGuardEntity(LittlePersonKingEntity littlePersonKingEntity, int count) {
        if (littlePersonKingEntity.level().isClientSide()) return;
        ServerLevel serverWorld = (ServerLevel) littlePersonKingEntity.level();
        Vec3 kingPos = littlePersonKingEntity.position();
        double radius = 3.0;
        for (int i = 0; i < count; i++) {
            // 在国王周围随机生成一个位置（水平3格，垂直±1格）
            double angle = serverWorld.random.nextDouble() * Math.PI * 2;
            double distance = 1.0 + serverWorld.random.nextDouble() * (radius - 1.0);
            double x = kingPos.x + Math.cos(angle) * distance;
            double z = kingPos.z + Math.sin(angle) * distance;
            double y = kingPos.y + serverWorld.random.nextInt(2);

            BlockPos spawnPos = BlockPos.containing(x, y, z);
            LittlePersonGuardEntity littlePersonGuard = ModEntities.LITTLE_PERSON_GUARD.create(serverWorld, EntitySpawnReason.EVENT);
            if (littlePersonGuard == null) continue;
            littlePersonGuard.snapTo(x, y, z, serverWorld.random.nextFloat() * 360.0F, 0.0F);
            littlePersonGuard.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.EVENT, null);
            littlePersonGuard.setLife(1200);
            littlePersonGuard.setSummonOwner(littlePersonKingEntity);
            littlePersonGuard.setPos(EntityUtil.findSafeSpawnPosition(serverWorld, littlePersonGuard, spawnPos.getCenter()).orElse(spawnPos.getCenter()));
            serverWorld.addFreshEntity(littlePersonGuard);
        }
        serverWorld.sendParticles(ParticleTypes.POOF,
                kingPos.x, kingPos.y + 1, kingPos.z,
                30, 1.0, 1.0, 1.0, 0.1);
    }
    public static List<LivingEntity> getNearbyLivingEntities(LittlePersonKingEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(radius),
                p -> p.isAlive() &&
                        entity.distanceTo(p) <= radius &&
                        p != entity && !p.isAlliedTo(entity)
        );
    }
    public static List<LittlePersonGuardEntity> getNearbyLittlePersonGuardEntity(LittlePersonKingEntity entity, double radius) {
        return entity.level().getEntitiesOfClass(
                LittlePersonGuardEntity.class,
                entity.getBoundingBox().inflate(radius),
                p -> p.isAlive() && entity.distanceTo(p) <= radius
        );
    }
}
