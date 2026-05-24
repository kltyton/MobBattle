package com.kltyton.mob_battle.effect.harmful;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class DecayEffect extends StatusEffect {
    public DecayEffect() {
        super(StatusEffectCategory.HARMFUL, 0x2B1B2F);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        if (entity.getHealth() <= 1.0F) {
            return true;
        }
        float damage = Math.min(1.0F, entity.getHealth() - 1.0F);
        entity.timeUntilRegen = 0;
        entity.damage(world, entity.getDamageSources().magic(), damage);
        entity.timeUntilRegen = 0;
        return true;
    }
}
