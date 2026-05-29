package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class StutterEffect extends StatusEffect {
    public StutterEffect() {
        super(StatusEffectCategory.HARMFUL, 0x404040);
    }
}
