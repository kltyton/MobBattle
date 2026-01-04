package com.kltyton.mob_battle.event.masterscepter;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SbBfbp {
    public static void runCommand(ServerPlayerEntity user, World world) {
        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                0.5F, 1.0F);

        if (!world.isClient) {
            Vec3d eyePos = user.getEyePos();
            Vec3d lookVec = user.getRotationVec(1.0F);
            float speed = 1.5F; // 发射速度
            Vec3d speedVec = new Vec3d(lookVec.x * speed, lookVec.y * speed, lookVec.z * speed);
            // 创建自定义火球
            CustomSuperBigFireballEntity fireball = new CustomSuperBigFireballEntity(ModEntities.BIG_CUSTOM_FIREBALL, world, user, 5.5F, true, 70.0F, speedVec, true);
            // 设置发射位置（玩家眼睛位置）
            fireball.setPosition(eyePos.add(lookVec.x * 2, lookVec.y - 1, lookVec.z * 2));
            // 生成火球实体
            world.spawnEntity(fireball);
        }
    }
}
