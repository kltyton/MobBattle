package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 失衡效果的注册类型；实际视角扰动由客户端在玩家鞘翅滑翔且仍持有效果时执行。
 */
public final class ImbalanceEffect extends MobEffect {
    public ImbalanceEffect() {
        super(MobEffectCategory.HARMFUL, 0x7A526E);
    }
}
