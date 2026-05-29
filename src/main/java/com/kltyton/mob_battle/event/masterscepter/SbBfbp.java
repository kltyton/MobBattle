package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SbBfbp {
    public static void runCommand(ServerPlayer user, Level world) {
        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                0.5F, 1.0F);

        if (!world.isClientSide) {
            Vec3 eyePos = user.getEyePosition();
            Vec3 lookVec = user.getViewVector(1.0F);
            float speed = 1.5F * 2; // 发射速度
            Vec3 speedVec = new Vec3(lookVec.x * speed, lookVec.y * speed, lookVec.z * speed);
            // 创建自定义火球
            CustomSuperBigFireballEntity fireball = new CustomSuperBigFireballEntity(ModEntities.BIG_CUSTOM_FIREBALL, world, user, 5.5F, true, 70.0F, speedVec, true);
            // 设置发射位置（玩家眼睛位置）
            fireball.setPos(eyePos.add(lookVec.x * 2, lookVec.y - 1, lookVec.z * 2));
            // 生成火球实体
            world.addFreshEntity(fireball);
        }
    }
}
