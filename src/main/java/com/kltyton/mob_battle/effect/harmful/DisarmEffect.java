package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DisarmEffect extends MobEffect {
    public DisarmEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }
}
