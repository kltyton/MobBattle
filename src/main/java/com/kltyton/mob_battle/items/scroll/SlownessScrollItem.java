package com.kltyton.mob_battle.items.scroll;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;
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

            /* 7 格半径立方体 */
            Box box = Box.of(user.getPos(), 14, 14, 14); // 中心±7
            List<Entity> targets = world.getOtherEntities(user, box,
                    e -> e instanceof LivingEntity         // 只选生物
                            && !e.isSpectator()                   // 忽略旁观
                            && !e.isInvulnerable());              // 忽略无敌

            /* 如果玩家有队伍，则提前拿到队伍引用 */
            AbstractTeam userTeam = user.getScoreboardTeam();

            for (Entity e : targets) {
                /* 跳过同队 */
                if (userTeam != null
                        && userTeam.isEqual(e.getScoreboardTeam())) {
                    continue;
                }
                /* 给缓慢 */
                ((LivingEntity)e).addStatusEffect(slowness, user);
                System.out.println("给" + e.getName().getString() + "加了缓慢");
            }
        }
        // 增加玩家使用统计
        user.incrementStat(Stats.USED.getOrCreateStat(this));

        // 消耗物品
        if (!user.getAbilities().creativeMode) itemStack.decrement(1);

        return ActionResult.SUCCESS;
    }
}
