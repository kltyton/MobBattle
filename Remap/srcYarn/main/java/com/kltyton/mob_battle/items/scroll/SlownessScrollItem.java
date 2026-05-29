package com.kltyton.mob_battle.items.scroll;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class SlownessScrollItem extends Item {
    public SlownessScrollItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                0.5F, 1.0F);
        /* ---------- 只在服务端施加效果 ---------- */
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
            double range = 7.0F;
            /* 7 格半径立方体 */
            Box box = user.getBoundingBox().expand(range, range, range);
            List<Entity> targets = world.getOtherEntities(user, box,
                    e -> e instanceof LivingEntity         // 只选生物
                            && !e.isSpectator()                   // 忽略旁观
                            && !e.isInvulnerable());              // 忽略无敌


            for (Entity e : targets) {
                /* 跳过同队 */
                if (user.isTeammate(e)) {
                    continue;
                }
                /* 给缓慢 */
                ((LivingEntity)e).addStatusEffect(slowness, user);
            }
        }
        // 增加玩家使用统计
        user.incrementStat(Stats.USED.getOrCreateStat(this));

        // 消耗物品
        if (!user.getAbilities().creativeMode) itemStack.decrement(1);

        return ActionResult.SUCCESS;
    }
}
