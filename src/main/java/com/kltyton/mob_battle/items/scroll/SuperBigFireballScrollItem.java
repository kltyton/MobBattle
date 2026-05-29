package com.kltyton.mob_battle.items.scroll;

import com.kltyton.mob_battle.entity.ModEntities;
import com.kltyton.mob_battle.entity.customfireball.CustomSuperBigFireballEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SuperBigFireballScrollItem extends FireballScrollItem {
    public SuperBigFireballScrollItem(Properties settings) {
        super(settings);
    }
    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                0.5F, 1.0F);

        if (!world.isClientSide) {
            Vec3 eyePos = user.getEyePosition();
            Vec3 lookVec = user.getViewVector(1.0F);
            float speed = 1.5F; // 发射速度
            Vec3 speedVec = new Vec3(lookVec.x * speed, lookVec.y * speed, lookVec.z * speed);
            // 创建自定义火球
            CustomSuperBigFireballEntity fireball = new CustomSuperBigFireballEntity(ModEntities.BIG_CUSTOM_FIREBALL, world, user, 5.5F, true, 70.0F, speedVec, false);
            // 设置发射位置（玩家眼睛位置）
            fireball.setPos(eyePos.add(lookVec.x * 2, lookVec.y - 1, lookVec.z * 2));
            // 生成火球实体
            world.addFreshEntity(fireball);
        }

        // 增加玩家使用统计
        user.awardStat(Stats.ITEM_USED.get(this));

        // 消耗物品
        if (!user.getAbilities().instabuild) itemStack.shrink(1);

        return InteractionResult.SUCCESS;
    }
}
