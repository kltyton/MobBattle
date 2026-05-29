package com.kltyton.mob_battle.event.masterscepter;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class SbS {
    public static void runCommand(ServerPlayer user) {
        double range = 7.0F;
        ServerLevel world = user.level();
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                0.5F, 1.0F);
        if (!world.isClientSide) {
            /* 缓慢 IV，100 tick */
            MobEffectInstance slowness = new MobEffectInstance(
                    MobEffects.SLOWNESS,   // 缓慢
                    100,                      // 持续时间
                    3,                        // amplifier = 3 → 等级 IV
                    false,                    // 是否来自信标
                    true,                     // 显示粒子
                    true                      // 显示图标
            );
            /* 半径立方体 */
            AABB box = user.getBoundingBox().inflate(range, range, range);
            List<Entity> targets = world.getEntities(user, box,
                    e -> e instanceof LivingEntity         // 只选生物
                            && !e.isSpectator()                   // 忽略旁观
                            && !e.isInvulnerable());              // 忽略无敌


            for (Entity e : targets) {
                if (user.isAlliedTo(e)) {
                    continue;
                }
                ((LivingEntity)e).addEffect(slowness, user);
            }
        }
    }
}
