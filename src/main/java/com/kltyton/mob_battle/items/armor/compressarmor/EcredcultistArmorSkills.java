package com.kltyton.mob_battle.items.armor.compressarmor;

import com.kltyton.mob_battle.entity.customfireball.CustomFireballEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ECREDCULTIST 套装技能：火球弹药恢复、HUD 弹药提示与火球发射。
 * 弹药量、恢复刻度、HUD 刻度三张状态表按玩家 UUID 常驻内存，生命周期与原 Manager 完全一致。
 */
final class EcredcultistArmorSkills {
    private static final String TEXT_ECREDCULTIST_FIREBALL_AMMO = "message.mob_battle.ecredcultist_fireball_ammo";
    private static final String TEXT_ECREDCULTIST_FIREBALL_EMPTY = "message.mob_battle.ecredcultist_fireball_empty";

    private static final int ECREDCULTIST_MAX_FIREBALLS = 64;
    private static final int ECREDCULTIST_REGEN_TICKS = 3 * 20;

    private static final Map<UUID, Integer> ECREDCULTIST_FIREBALLS = new HashMap<>();
    private static final Map<UUID, Long> ECREDCULTIST_LAST_REGEN_TICK = new HashMap<>();
    private static final Map<UUID, Long> ECREDCULTIST_LAST_HUD_TICK = new HashMap<>();

    private EcredcultistArmorSkills() {
    }

    /**
     * 释放指定玩家的全部常驻状态：弹药量、恢复刻度与 HUD 刻度。
     * 玩家断线时调用，防止已离线玩家的 UUID 残留在静态 Map 中。
     *
     * @param playerId 目标玩家 UUID
     */
    static void clearPlayer(UUID playerId) {
        ECREDCULTIST_FIREBALLS.remove(playerId);
        ECREDCULTIST_LAST_REGEN_TICK.remove(playerId);
        ECREDCULTIST_LAST_HUD_TICK.remove(playerId);
    }

    /**
     * 清空全部玩家的常驻状态。服务器停止时调用，避免跨会话复用旧玩家状态。
     */
    static void clearAll() {
        ECREDCULTIST_FIREBALLS.clear();
        ECREDCULTIST_LAST_REGEN_TICK.clear();
        ECREDCULTIST_LAST_HUD_TICK.clear();
    }

    /**
     * 每 tick 恢复弹药并周期更新 HUD：满弹药时只推进恢复刻度，未满且间隔达到
     * {@value #ECREDCULTIST_REGEN_TICKS} tick 时加一；HUD 每 20 tick 刷新一次。
     */
    static void tickEcredcultistArmor(ServerPlayer player) {
        UUID playerId = player.getUUID();
        long gameTime = player.level().getGameTime();
        int ammo = ECREDCULTIST_FIREBALLS.computeIfAbsent(playerId, ignored -> ECREDCULTIST_MAX_FIREBALLS);
        long lastRegen = ECREDCULTIST_LAST_REGEN_TICK.getOrDefault(playerId, gameTime);

        if (ammo >= ECREDCULTIST_MAX_FIREBALLS) {
            ECREDCULTIST_LAST_REGEN_TICK.put(playerId, gameTime);
        } else if (gameTime - lastRegen >= ECREDCULTIST_REGEN_TICKS) {
            ammo++;
            ECREDCULTIST_FIREBALLS.put(playerId, ammo);
            ECREDCULTIST_LAST_REGEN_TICK.put(playerId, gameTime);
        }

        long lastHud = ECREDCULTIST_LAST_HUD_TICK.getOrDefault(playerId, 0L);
        if (gameTime - lastHud >= 20L) {
            player.sendOverlayMessage(Component.translatable(TEXT_ECREDCULTIST_FIREBALL_AMMO, ammo).withStyle(ChatFormatting.GOLD));
            ECREDCULTIST_LAST_HUD_TICK.put(playerId, gameTime);
        }
    }

    /**
     * 消耗一发弹药发射跟踪火球并播放粒子/音效；弹药不足时只显示空弹 HUD。
     */
    static void runEcredcultistFireball(ServerPlayer player) {
        UUID playerId = player.getUUID();
        int ammo = ECREDCULTIST_FIREBALLS.computeIfAbsent(playerId, ignored -> ECREDCULTIST_MAX_FIREBALLS);
        if (ammo <= 0) {
            player.sendOverlayMessage(Component.translatable(TEXT_ECREDCULTIST_FIREBALL_EMPTY).withStyle(ChatFormatting.RED));
            return;
        }

        ECREDCULTIST_FIREBALLS.put(playerId, ammo - 1);
        player.sendOverlayMessage(Component.translatable(TEXT_ECREDCULTIST_FIREBALL_AMMO, ammo - 1).withStyle(ChatFormatting.GOLD));

        ServerLevel world = player.level();
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F).normalize();
        CustomFireballEntity fireball = new CustomFireballEntity(world, player, 2.5F, true, 150.0F);
        fireball.setPos(eyePos.add(look.scale(0.8D)));
        fireball.setDeltaMovement(look.scale(1.8D));
        world.addFreshEntity(fireball);

        world.sendParticles(ParticleTypes.FLAME, fireball.getX(), fireball.getY(), fireball.getZ(), 45, 0.35, 0.35, 0.35, 0.08);
        world.sendParticles(ParticleTypes.LAVA, fireball.getX(), fireball.getY(), fireball.getZ(), 10, 0.25, 0.25, 0.25, 0.02);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.65F);
    }
}
