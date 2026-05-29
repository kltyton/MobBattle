package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class PigSpiritMarkEffect extends StatusEffect {
    public PigSpiritMarkEffect() {
        super(StatusEffectCategory.HARMFUL, 0x925a5a);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}