package com.kltyton.mob_battle.items.armor.presence;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** 套装动作的定量粒子爆发；仅在动作关键帧发送，不为五个环绕物持续广播粒子。 */
public final class PresenceArmorParticles {
    private static final DustParticleOptions VIOLET = new DustParticleOptions(0xB59AEF, 1.2F);
    private static final DustParticleOptions SILVER = new DustParticleOptions(0xDDD7ED, 0.85F);

    private PresenceArmorParticles() {}

    /** 双层地面冲击环、碎屑及五个弹药生成点，共用服务端已确认的踩地时点。 */
    public static void stomp(ServerPlayer player) {
        ServerLevel world = player.level();
        Vec3 center = player.position();
        ring(world, center.add(0, 0.1, 0), 2, VIOLET);
        ring(world, center.add(0, 0.16, 0), 0.9, SILVER);
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(player.blockPosition().below())),
                center.x, center.y + 0.1, center.z, 35, 1.5, 0.1, 1.5, 0.08);
        world.sendParticles(ParticleTypes.END_ROD, center.x, center.y + 0.2, center.z, 18, 0.8, 0.15, 0.8, 0.09);
        for (int i = 0; i < 5; i++) {
            double angle = world.getGameTime() * 0.065 + i * Math.PI * 2 / 5;
            world.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x + Math.cos(angle) * 1.7,
                    center.y + 1, center.z + Math.sin(angle) * 1.7, 6, 0.12, 0.18, 0.12, 0.02);
        }
    }

    public static void kick(ServerPlayer player, Vec3 origin) {
        player.level().sendParticles(ParticleTypes.SWEEP_ATTACK, origin.x, origin.y, origin.z, 1, 0, 0, 0, 0);
        player.level().sendParticles(VIOLET, origin.x, origin.y, origin.z, 14, 0.2, 0.2, 0.2, 0.04);
        player.level().sendParticles(ParticleTypes.ELECTRIC_SPARK, origin.x, origin.y, origin.z, 10, 0.15, 0.15, 0.15, 0.1);
    }

    public static void awaken(ServerLevel world, Vec3 center, BlockState block) {
        ring(world, center.add(0, 0.1, 0), 0.7, VIOLET);
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, block), center.x, center.y + 0.5, center.z,
                20, 0.3, 0.4, 0.3, 0.06);
        world.sendParticles(ParticleTypes.ENCHANT, center.x, center.y + 0.6, center.z, 24, 0.5, 0.5, 0.5, 0.25);
    }

    public static void mode(ServerPlayer player, boolean aggressive) {
        player.level().sendParticles(aggressive ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 0.8, player.getZ(), 16, 0.5, 0.5, 0.5, 0.06);
    }

    /** 视觉半径保持在小型爆炸周围，不参与原有的伤害与冷却结算。 */
    public static void impact(ServerLevel world, Vec3 center) {
        world.sendParticles(VIOLET, center.x, center.y, center.z, 24, 0.4, 0.4, 0.4, 0.05);
        world.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 12, 0.25, 0.25, 0.25, 0.1);
    }

    private static void ring(ServerLevel world, Vec3 center, double radius, DustParticleOptions dust) {
        for (int i = 0; i < 24; i++) {
            double angle = i * Math.PI * 2 / 24;
            world.sendParticles(dust, center.x + Math.cos(angle) * radius, center.y,
                    center.z + Math.sin(angle) * radius, 1, 0, 0, 0, 0);
        }
    }
}
