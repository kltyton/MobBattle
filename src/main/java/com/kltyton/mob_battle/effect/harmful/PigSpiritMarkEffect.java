package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PigSpiritMarkEffect extends MobEffect {
    public PigSpiritMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0x925a5a);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}