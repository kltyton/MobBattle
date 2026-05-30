package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BlockStatusEffect extends MobEffect {
    public BlockStatusEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x808080);
    }

    // 该效果通过 Mixin 触发，这里不需要写 applyUpdateEffect
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}
