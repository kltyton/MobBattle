package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 刺丛效果的注册类型；反伤行为由服务端伤害边界统一处理。
 */
public final class ThicketEffect extends MobEffect {
    public ThicketEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x3F7D38);
    }
}
