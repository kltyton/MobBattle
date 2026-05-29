package com.kltyton.mob_battle.items.scroll;

import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SlownessScrollItem extends Item {
    public SlownessScrollItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        // 播放使用音效
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                0.5F, 1.0F);
        /* ---------- 只在服务端施加效果 ---------- */
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
            double range = 7.0F;
            /* 7 格半径立方体 */
            AABB box = user.getBoundingBox().inflate(range, range, range);
            List<Entity> targets = world.getEntities(user, box,
                    e -> e instanceof LivingEntity         // 只选生物
                            && !e.isSpectator()                   // 忽略旁观
                            && !e.isInvulnerable());              // 忽略无敌


            for (Entity e : targets) {
                /* 跳过同队 */
                if (user.isAlliedTo(e)) {
                    continue;
                }
                /* 给缓慢 */
                ((LivingEntity)e).addEffect(slowness, user);
            }
        }
        // 增加玩家使用统计
        user.awardStat(Stats.ITEM_USED.get(this));

        // 消耗物品
        if (!user.getAbilities().instabuild) itemStack.shrink(1);

        return InteractionResult.SUCCESS;
    }
}
