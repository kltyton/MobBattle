package com.kltyton.mob_battle.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SelfDestructEffect extends StatusEffect {
    public SelfDestructEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF0000);
    }
}