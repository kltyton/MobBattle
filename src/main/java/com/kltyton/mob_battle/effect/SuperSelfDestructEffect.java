package com.kltyton.mob_battle.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SuperSelfDestructEffect extends StatusEffect {
    public SuperSelfDestructEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF0000);
    }
}
