package com.kltyton.mob_battle.event.masterscepter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.List;

public class SbS {
    public static void runCommand(ServerPlayerEntity user) {
        double range = 7.0F;
        ServerWorld world = user.getWorld();
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                0.5F, 1.0F);
        if (!world.isClient) {
            /* 缓慢 IV，100 tick */
            StatusEffectInstance slowness = new StatusEffectInstance(
                    StatusEffects.SLOWNESS,   // 缓慢
                    100,                      // 持续时间
                    3,                        // amplifier = 3 → 等级 IV
                    false,                    // 是否来自信标
                    true,                     // 显示粒子
                    true                      // 显示图标
            );
            /* 半径立方体 */
            Box box = user.getBoundingBox().expand(range, range, range);
            List<Entity> targets = world.getOtherEntities(user, box,
                    e -> e instanceof LivingEntity         // 只选生物
                            && !e.isSpectator()                   // 忽略旁观
                            && !e.isInvulnerable());              // 忽略无敌


            for (Entity e : targets) {
                if (user.isTeammate(e)) {
                    continue;
                }
                ((LivingEntity)e).addStatusEffect(slowness, user);
            }
        }
    }
}
