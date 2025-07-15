package com.kltyton.mob_battle.items;

import com.kltyton.mob_battle.entity.CustomFireballEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BIgFireballScrollItem extends FireballScrollItem{
    public BIgFireballScrollItem(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                0.5F, 1.0F);

        if (!world.isClient) {
            Vec3d eyePos = user.getEyePos();
            // 创建自定义火球
            CustomFireballEntity fireball = new CustomFireballEntity(world, user, 2.5F, true, 50.0F);
            // 设置发射位置（玩家眼睛位置）
            fireball.setPosition(eyePos);

            // 设置发射方向（玩家视线方向）
            Vec3d lookVec = user.getRotationVec(1.0F);
            float speed = 1.5F; // 发射速度
            fireball.setVelocity(lookVec.x * speed, lookVec.y * speed, lookVec.z * speed);

            // 生成火球实体
            world.spawnEntity(fireball);
        }

        // 增加玩家使用统计
        user.incrementStat(Stats.USED.getOrCreateStat(this));

        // 消耗物品
        if (!user.getAbilities().creativeMode) itemStack.decrement(1);

        return ActionResult.SUCCESS;
    }
}
