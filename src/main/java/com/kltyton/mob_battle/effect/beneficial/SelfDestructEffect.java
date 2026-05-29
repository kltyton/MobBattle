package com.kltyton.mob_battle.effect.beneficial;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SelfDestructEffect extends MobEffect {
    public SelfDestructEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
    }
}