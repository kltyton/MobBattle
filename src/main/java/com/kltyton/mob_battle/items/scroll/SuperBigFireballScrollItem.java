package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SuperBigFireballScrollItem extends FireballScrollItem {
    public SuperBigFireballScrollItem(Settings settings) {
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
            Vec3d lookVec = user.getRotationVec(1.0F);
            float speed = 1.5F; // 发射速度
            Vec3d speedVec = new Vec3d(lookVec.x * speed, lookVec.y * speed, lookVec.z * speed);
            // 创建自定义火球
            CustomSuperBigFireballEntity fireball = new CustomSuperBigFireballEntity(ModEntities.BIG_CUSTOM_FIREBALL, world, user, 5.5F, true, 70.0F, speedVec, false);
            // 设置发射位置（玩家眼睛位置）
            fireball.setPosition(eyePos.add(lookVec.x * 2, lookVec.y - 1, lookVec.z * 2));
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
