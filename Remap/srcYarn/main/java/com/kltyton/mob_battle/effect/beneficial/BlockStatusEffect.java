package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BlockStatusEffect extends StatusEffect {
    public BlockStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x808080);
    }

    // 该效果通过 Mixin 触发，这里不需要写 applyUpdateEffect
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}
