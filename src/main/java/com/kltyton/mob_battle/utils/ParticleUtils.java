package com.kltyton.mob_battle.utils;

import com.kltyton.mob_battle.effect.ModEffects;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class ParticleUtils {
    /**
     * 计算基于中心点、旋转角度和偏移量的世界坐标
     * 模拟数据包中的: execute rotated yaw 0 positioned ~ ~ ~ run particle ... ^x ^y ^z
     */
    public static void spawnLocalParticle(ServerLevel world, ParticleOptions particle,
                                          double centerX, double centerY, double centerZ,
                                          float yaw, float pitch,
                                          double localX, double localY, double localZ,
                                          int count, double speed) {

        // 将角度转换为弧度
        float f = pitch * ((float)Math.PI / 180F);
        float g = -yaw * ((float)Math.PI / 180F);
        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);

        // 局部坐标转世界坐标的矩阵变换 (Simplified for local Z/X)
        double worldX = centerX + (localX * h - localZ * i);
        double worldY = centerY + localY; // 数据包中 y 通常是固定偏移
        double worldZ = centerZ + (localZ * h + localX * i);

        world.sendParticles(particle, worldX, worldY, worldZ, count, 0, 0, 0, speed);
    }
    public static void spawnZiJinSkill0MarkParticles(ServerLevel world, ServerPlayer player, List<LivingEntity> targets) {
        double px = player.getX();
        double py = player.getY() + 0.15D;
        double pz = player.getZ();

        // 地面紫金圆环
        for (int i = 0; i < 96; i++) {
            double angle = Math.PI * 2.0D * i / 96.0D;
            double radius = 5.0D;
            double x = px + Math.cos(angle) * radius;
            double z = pz + Math.sin(angle) * radius;

            world.sendParticles(
                    ParticleTypes.WITCH,
                    x,
                    py,
                    z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );

            if (i % 3 == 0) {
                world.sendParticles(
                        ParticleTypes.ENCHANT,
                        x,
                        py + 0.25D,
                        z,
                        1,
                        0.0D,
                        0.05D,
                        0.0D,
                        0.2D
                );
            }
        }

        // 玩家身上向外扩散的紫金能量
        world.sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                px,
                player.getY(0.5D),
                pz,
                80,
                1.2D,
                0.8D,
                1.2D,
                0.08D
        );

        world.sendParticles(
                ParticleTypes.END_ROD,
                px,
                player.getY(0.8D),
                pz,
                24,
                0.5D,
                0.7D,
                0.5D,
                0.03D
        );

        // 每个被上印记的目标身上冒出印记粒子
        for (LivingEntity target : targets) {
            spawnPigSpiritMarkTargetParticles(world, target, false);
        }
    }

    public static void spawnZiJinSkill0DetonateParticles(ServerLevel world, ServerPlayer player, List<LivingEntity> targets) {
        double px = player.getX();
        double py = player.getY() + 0.1D;
        double pz = player.getZ();

        // 20格范围的冲击波，多层圆环
        for (double radius = 4.0D; radius <= 20.0D; radius += 4.0D) {
            int count = (int) (radius * 18.0D);

            for (int i = 0; i < count; i++) {
                double angle = Math.PI * 2.0D * i / count;
                double x = px + Math.cos(angle) * radius;
                double z = pz + Math.sin(angle) * radius;

                world.sendParticles(
                        ParticleTypes.ENCHANT,
                        x,
                        py,
                        z,
                        1,
                        0.0D,
                        0.02D,
                        0.0D,
                        0.15D
                );

                if (i % 5 == 0) {
                    world.sendParticles(
                            ParticleTypes.WITCH,
                            x,
                            py + 0.15D,
                            z,
                            1,
                            0.0D,
                            0.0D,
                            0.0D,
                            0.0D
                    );
                }
            }
        }

        // 中心爆发
        world.sendParticles(
                ParticleTypes.EXPLOSION,
                px,
                player.getY(0.5D),
                pz,
                3,
                0.8D,
                0.4D,
                0.8D,
                0.0D
        );

        world.sendParticles(
                ParticleTypes.SONIC_BOOM,
                px,
                player.getY(0.6D),
                pz,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        world.sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                px,
                player.getY(0.6D),
                pz,
                120,
                2.5D,
                1.2D,
                2.5D,
                0.12D
        );

        // 被引爆印记的目标身上爆开
        for (LivingEntity target : targets) {
            if (!target.hasEffect(ModEffects.PIG_SPIRIT_MARK_ENTRY)) {
                continue;
            }

            world.sendParticles(
                    ParticleTypes.EXPLOSION,
                    target.getX(),
                    target.getY(0.5D),
                    target.getZ(),
                    1,
                    0.2D,
                    0.2D,
                    0.2D,
                    0.0D
            );

            world.sendParticles(
                    ParticleTypes.DAMAGE_INDICATOR,
                    target.getX(),
                    target.getY(0.7D),
                    target.getZ(),
                    12,
                    0.35D,
                    0.35D,
                    0.35D,
                    0.15D
            );

            world.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(),
                    target.getY(0.5D),
                    target.getZ(),
                    18,
                    0.35D,
                    0.6D,
                    0.35D,
                    0.03D
            );
        }
    }

    public static void spawnZiJinSkill1MarkParticles(ServerLevel world, ServerPlayer player, List<LivingEntity> targets) {
        double px = player.getX();
        double py = player.getY() + 0.15D;
        double pz = player.getZ();

        // 6格范围的快速旋转法阵
        for (int layer = 0; layer < 3; layer++) {
            double radius = 2.0D + layer * 2.0D;
            int count = 48 + layer * 24;

            for (int i = 0; i < count; i++) {
                double angle = Math.PI * 2.0D * i / count + layer * 0.45D;
                double x = px + Math.cos(angle) * radius;
                double z = pz + Math.sin(angle) * radius;

                world.sendParticles(
                        layer % 2 == 0 ? ParticleTypes.WITCH : ParticleTypes.ENCHANT,
                        x,
                        py + layer * 0.15D,
                        z,
                        1,
                        0.0D,
                        0.02D,
                        0.0D,
                        0.1D
                );
            }
        }

        // 玩家身上升起能量柱
        world.sendParticles(
                ParticleTypes.END_ROD,
                px,
                player.getY(0.5D),
                pz,
                36,
                0.45D,
                1.0D,
                0.45D,
                0.04D
        );

        world.sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                px,
                player.getY(0.6D),
                pz,
                70,
                1.0D,
                1.0D,
                1.0D,
                0.08D
        );

        // 目标身上更明显的印记特效
        for (LivingEntity target : targets) {
            spawnPigSpiritMarkTargetParticles(world, target, true);
        }
    }

    private static void spawnPigSpiritMarkTargetParticles(ServerLevel world, LivingEntity target, boolean strong) {
        double x = target.getX();
        double y = target.getY(0.5D);
        double z = target.getZ();

        int ringCount = strong ? 36 : 24;
        double radius = strong ? 0.9D : 0.65D;

        // 目标腰部印记环
        for (int i = 0; i < ringCount; i++) {
            double angle = Math.PI * 2.0D * i / ringCount;
            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;

            world.sendParticles(
                    ParticleTypes.WITCH,
                    px,
                    y,
                    pz,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        // 目标身上冒紫金能量
        world.sendParticles(
                ParticleTypes.ENCHANT,
                x,
                y + 0.2D,
                z,
                strong ? 30 : 16,
                0.35D,
                0.55D,
                0.35D,
                0.3D
        );

        world.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                x,
                y,
                z,
                strong ? 18 : 10,
                0.25D,
                0.45D,
                0.25D,
                0.02D
        );

        if (strong) {
            world.sendParticles(
                    ParticleTypes.CRIT,
                    x,
                    y + 0.3D,
                    z,
                    16,
                    0.3D,
                    0.45D,
                    0.3D,
                    0.15D
            );
        }
    }
}