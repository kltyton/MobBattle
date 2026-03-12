package com.kltyton.mob_battle.utils;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class ParticleUtils {
    /**
     * 计算基于中心点、旋转角度和偏移量的世界坐标
     * 模拟数据包中的: execute rotated yaw 0 positioned ~ ~ ~ run particle ... ^x ^y ^z
     */
    public static void spawnLocalParticle(ServerWorld world, ParticleEffect particle,
                                          double centerX, double centerY, double centerZ,
                                          float yaw, float pitch,
                                          double localX, double localY, double localZ,
                                          int count, double speed) {

        // 将角度转换为弧度
        float f = pitch * ((float)Math.PI / 180F);
        float g = -yaw * ((float)Math.PI / 180F);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);

        // 局部坐标转世界坐标的矩阵变换 (Simplified for local Z/X)
        double worldX = centerX + (localX * h - localZ * i);
        double worldY = centerY + localY; // 数据包中 y 通常是固定偏移
        double worldZ = centerZ + (localZ * h + localX * i);

        world.spawnParticles(particle, worldX, worldY, worldZ, count, 0, 0, 0, speed);
    }
}