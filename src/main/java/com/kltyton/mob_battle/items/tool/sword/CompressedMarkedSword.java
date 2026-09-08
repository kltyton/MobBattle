package com.kltyton.mob_battle.items.tool.sword;

import com.kltyton.mob_battle.items.tool.BaseSword;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class CompressedMarkedSword extends BaseSword {
    private final Holder<MobEffect> markEffect;

    public CompressedMarkedSword(Properties settings, Holder<MobEffect> markEffect) {
        super(settings);
        this.markEffect = markEffect;
    }

    @Override
    public void addStatusEffect(LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(this.markEffect, 7 * 20, 0, false, true, true), attacker);
    }
}
