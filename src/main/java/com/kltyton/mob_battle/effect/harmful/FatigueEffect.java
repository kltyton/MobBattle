package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FatigueEffect extends StatusEffect {
    public FatigueEffect() {
        super(StatusEffectCategory.HARMFUL, 0x6B6B6B);
    }
}
