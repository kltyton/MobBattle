package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class DecayEffect extends MobEffect {
    public DecayEffect() {
        super(MobEffectCategory.HARMFUL, 0x2B1B2F);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (entity.getHealth() <= 1.0F) {
            return true;
        }
        float damage = Math.min(20.0F, entity.getHealth() - 1.0F);
        entity.hurtServer(world, entity.damageSources().magic(), damage);
        return true;
    }
}
