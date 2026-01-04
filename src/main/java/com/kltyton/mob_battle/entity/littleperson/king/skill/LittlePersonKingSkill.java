package com.kltyton.mob_battle.entity.littleperson.king.skill;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.littleperson.guard.LittlePersonGuardEntity;
import com.kltyton.mob_battle.entity.littleperson.king.LittlePersonKingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class LittlePersonKingSkill {

    public static void runSkill_2(LittlePersonKingEntity littlePersonKingEntity) {
        summonLittlePersonGuardEntity(littlePersonKingEntity, littlePersonKingEntity.isViolent() ? 6 : 2);
    }

    public static void runSkill_3(LittlePersonKingEntity littlePersonKingEntity) {
        if (littlePersonKingEntity.getWorld().isClient()) return;
        ServerWorld serverWorld = (ServerWorld) littlePersonKingEntity.getWorld();
        // 根据国王朝向计算前方约2格的位置
        Vec3d look = littlePersonKingEntity.getRotationVec(1.0F).normalize().multiply(2.0);
        Vec3d lightningPos = littlePersonKingEntity.getEyePos().add(look);

        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld, SpawnReason.EVENT);
        if (lightning == null) return;

        lightning.setCosmetic(true);
        lightning.setPosition(lightningPos.x, lightningPos.y, lightningPos.z);
        serverWorld.spawnEntity(lightning);
        List<LivingEntity> target = getNearbyLivingEntities(littlePersonKingEntity, 4.0);
        for (LivingEntity targetEntity : target) {
            targetEntity.damage(serverWorld, targetEntity.getDamageSources().lightningBolt(), 120.0F);
        }
        serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                lightningPos.x, lightningPos.y + 0.5, lightningPos.z,
                50, 0.5, 0.5, 0.5, 0.2);
    }
    public static void summonLittlePersonGuardEntity(LittlePersonKingEntity littlePersonKingEntity, int count) {
        if (littlePersonKingEntity.getWorld().isClient()) return;
        ServerWorld serverWorld = (ServerWorld) littlePersonKingEntity.getWorld();
        Vec3d kingPos = littlePersonKingEntity.getPos();
        double radius = 3.0;
        for (int i = 0; i < count; i++) {
            // 在国王周围随机生成一个位置（水平3格，垂直±1格）
            double angle = serverWorld.random.nextDouble() * Math.PI * 2;
            double distance = 1.0 + serverWorld.random.nextDouble() * (radius - 1.0);
            double x = kingPos.x + Math.cos(angle) * distance;
            double z = kingPos.z + Math.sin(angle) * distance;
            double y = kingPos.y + serverWorld.random.nextInt(2);

            BlockPos spawnPos = BlockPos.ofFloored(x, y, z);
            LittlePersonGuardEntity littlePersonGuard = ModEntities.LITTLE_PERSON_GUARD.create(serverWorld, SpawnReason.EVENT);
            if (littlePersonGuard == null) continue;
            littlePersonGuard.refreshPositionAndAngles(x, y, z, serverWorld.random.nextFloat() * 360.0F, 0.0F);
            littlePersonGuard.initialize(serverWorld, serverWorld.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null);
            littlePersonGuard.setLife(1200);
            serverWorld.spawnEntity(littlePersonGuard);
            Scoreboard scoreboard = serverWorld.getScoreboard();
            Team littlePersonKingEntityTeam = scoreboard.getTeam(littlePersonKingEntity.getName().getString());
            if (littlePersonKingEntityTeam != null) {
                String littlePersonGuardScoreName = littlePersonGuard.getNameForScoreboard();
                scoreboard.addScoreHolderToTeam(littlePersonGuardScoreName, littlePersonKingEntityTeam);
            }
        }
        serverWorld.spawnParticles(ParticleTypes.POOF,
                kingPos.x, kingPos.y + 1, kingPos.z,
                30, 1.0, 1.0, 1.0, 0.1);
    }
    public static List<LivingEntity> getNearbyLivingEntities(LittlePersonKingEntity entity, double radius) {
        return entity.getWorld().getEntitiesByClass(
                LivingEntity.class,
                entity.getBoundingBox().expand(radius),
                p -> p.isAlive() &&
                        entity.distanceTo(p) <= radius &&
                        p != entity && !p.isTeammate(entity)
        );
    }
    public static List<LittlePersonGuardEntity> getNearbyLittlePersonGuardEntity(LittlePersonKingEntity entity, double radius) {
        return entity.getWorld().getEntitiesByClass(
                LittlePersonGuardEntity.class,
                entity.getBoundingBox().expand(radius),
                p -> p.isAlive() && entity.distanceTo(p) <= radius
        );
    }
}
