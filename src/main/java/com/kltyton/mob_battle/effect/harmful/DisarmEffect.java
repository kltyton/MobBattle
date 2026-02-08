package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class DisarmEffect extends StatusEffect {
    public DisarmEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF0000);
    }
}
